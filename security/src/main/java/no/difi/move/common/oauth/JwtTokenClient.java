package no.difi.move.common.oauth;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.util.Base64;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import no.difi.move.common.cert.KeystoreHelper;
import org.springframework.http.*;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.security.cert.CertificateEncodingException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.*;

@Slf4j
public class JwtTokenClient {

    public static final TimeZone DEFAULT_TIME_ZONE = TimeZone.getTimeZone("Europe/Oslo");
    public static final ZoneId DEFAULT_ZONE_ID = DEFAULT_TIME_ZONE.toZoneId();

    private final JwtTokenConfig config;
    private final WebClient wc;
    private final JWSHeader jwsHeader;
    private final RSASSASigner signer;

    public JwtTokenClient(JwtTokenConfig config) {
        this.config = config;
        this.wc = WebClient.create(config.getTokenUri());

        KeystoreHelper keystoreHelper = new KeystoreHelper(config.getKeystore());
        this.jwsHeader = getJwsHeader(keystoreHelper);
        this.signer = getSigner(keystoreHelper);
    }

    private JWSHeader getJwsHeader(KeystoreHelper keystoreHelper) {
        return new JWSHeader.Builder(JWSAlgorithm.RS256)
                .x509CertChain(getCertChain(keystoreHelper))
                .build();
    }

    private List<Base64> getCertChain(KeystoreHelper keystoreHelper) {
        try {
            return Collections.singletonList(Base64.encode(keystoreHelper.getX509Certificate().getEncoded()));
        } catch (CertificateEncodingException e) {
            log.error("Could not get encoded certificate", e);
            throw new CertificateEncodingRuntimeException("Could not get encoded certificate", e);
        }
    }

    private RSASSASigner getSigner(KeystoreHelper keystoreHelper) {
        RSASSASigner s = new RSASSASigner(keystoreHelper.loadPrivateKey());
        if (keystoreHelper.shouldLockProvider()) {
            s.getJCAContext().setProvider(keystoreHelper.getKeyStore().getProvider());
        }
        return s;
    }

    public Mono<JwtTokenResponse> fetchTokenMono() {
        return fetchTokenMono(new JwtTokenInput());
    }

    public Mono<JwtTokenResponse> fetchTokenMono(JwtTokenInput input) {
        Mono<LinkedMultiValueMap<String, String>> body = Mono.fromSupplier(() -> {
            LinkedMultiValueMap<String, String> fields = new LinkedMultiValueMap<>();
            fields.add("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer");
            fields.add("assertion", generateJWT(input));
            return fields;
        });

        return wc.post()
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
                .body(body, LinkedMultiValueMap.class)
                .retrieve()
                .onStatus(HttpStatus::isError, e -> e.bodyToMono(String.class)
                        .flatMap(s -> Mono.error(new JwtTokenException("http status: " + e.statusCode() + ", body: " + s)))
                )
                .bodyToMono(JwtTokenResponse.class)
                .retryWhen(Retry.backoff(Long.MAX_VALUE, Duration.ofSeconds(2L))
                        .maxBackoff(Duration.ofMinutes(1L))
                        .doBeforeRetry(rs -> log.warn("Error connecting to token endpoint, retrying.. " + rs)))
                .doOnNext(res -> log.info("Response: {}", res.toString()))
                .cache(r -> Duration.ofSeconds(r.getExpiresIn() - 10L), t -> Duration.ZERO, () -> Duration.ZERO);
    }

    @Retryable(value = HttpClientErrorException.class, maxAttempts = Integer.MAX_VALUE,
            backoff = @Backoff(delay = 5000, maxDelay = 1000 * 60 * 60, multiplier = 3))
    public JwtTokenResponse fetchToken() {
        return fetchToken(new JwtTokenInput());
    }

    @Retryable(value = HttpClientErrorException.class, maxAttempts = Integer.MAX_VALUE,
            backoff = @Backoff(delay = 5000, maxDelay = 1000 * 60 * 60, multiplier = 3))
    public JwtTokenResponse fetchToken(JwtTokenInput input) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new OidcErrorHandler());

        LinkedMultiValueMap<String, String> attrMap = new LinkedMultiValueMap<>();
        attrMap.add("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer");
        attrMap.add("assertion", generateJWT(input));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(attrMap, headers);

        FormHttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();
        restTemplate.getMessageConverters().add(formHttpMessageConverter);

        ResponseEntity<JwtTokenResponse> response = restTemplate.exchange(config.getTokenUri(), HttpMethod.POST,
                httpEntity, JwtTokenResponse.class);
        log.info("Response: {}", response);

        return response.getBody();
    }

    public String generateJWT() {
        return generateJWT(new JwtTokenInput());
    }

    public String generateJWT(JwtTokenInput input) {
        JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder()
                .audience(Optional.ofNullable(input.getAudience()).orElse(config.getAudience()))
                .issuer(Optional.ofNullable(input.getClientId()).orElse(config.getClientId()))
                .claim("scope", getScopeString(input))
                .jwtID(UUID.randomUUID().toString())
                .issueTime(Date.from(OffsetDateTime.now(DEFAULT_ZONE_ID).toInstant()))
                .expirationTime(Date.from(OffsetDateTime.now(DEFAULT_ZONE_ID).toInstant().plusSeconds(120)));

        Optional.ofNullable(input.getConsumerOrg())
                .ifPresent(consumerOrg -> builder.claim("consumer_org", consumerOrg));

        JWTClaimsSet claims = builder
                .build();

        SignedJWT signedJWT = new SignedJWT(jwsHeader, claims);
        try {
            signedJWT.sign(signer);
        } catch (JOSEException e) {
            log.error("Error occured during signing of JWT", e);
        }

        String serializedJwt = signedJWT.serialize();
        log.info("SerializedJWT: {}", serializedJwt);

        return serializedJwt;
    }

    private String getScopeString(JwtTokenInput input) {
        List<String> scopes = Optional.ofNullable(input.getScopes()).orElse(config.getScopes());
        return scopes.stream().reduce((a, b) -> a + " " + b).orElse("");
    }

    public static class CertificateEncodingRuntimeException extends RuntimeException {
        public CertificateEncodingRuntimeException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
