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

    public JwtTokenClient(JwtTokenConfig config) {
        this.config = config;
        this.wc = WebClient.create(config.getTokenUri());
    }

    public Mono<JwtTokenResponse> fetchTokenMono() {
        Mono<LinkedMultiValueMap<String, String>> body = Mono.fromSupplier(() -> {
            LinkedMultiValueMap<String, String> fields = new LinkedMultiValueMap<>();
            fields.add("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer");
            fields.add("assertion", generateJWT());
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
                .cache(r -> Duration.ofSeconds(r.getExpiresIn()-10 ), (t) -> Duration.ZERO , () -> Duration.ZERO);
    }

    @Retryable(value = HttpClientErrorException.class, maxAttempts = Integer.MAX_VALUE,
            backoff = @Backoff(delay = 5000, maxDelay = 1000 * 60 * 60, multiplier = 3))
    public JwtTokenResponse fetchToken() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new OidcErrorHandler());

        LinkedMultiValueMap<String, String> attrMap = new LinkedMultiValueMap<>();
        attrMap.add("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer");
        attrMap.add("assertion", generateJWT());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(attrMap, headers);

        FormHttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();
        restTemplate.getMessageConverters().add(formHttpMessageConverter);

        ResponseEntity<JwtTokenResponse> response = restTemplate.exchange(config.getTokenUri(), HttpMethod.POST,
                httpEntity, JwtTokenResponse.class);
        log.info("Response: {}", response.toString());

        return response.getBody();
    }

    public String generateJWT() {
        KeystoreHelper nokkel = new KeystoreHelper(config.getKeystore());

        List<Base64> certChain = new ArrayList<>();
        try {
            certChain.add(Base64.encode(nokkel.getX509Certificate().getEncoded()));
        } catch (CertificateEncodingException e) {
            log.error("Could not get encoded certificate", e);
            throw new RuntimeException(e);
        }

        JWSHeader jwsHeader = new JWSHeader.Builder(JWSAlgorithm.RS256).x509CertChain(certChain).build();

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .audience(config.getAudience())
                .issuer(config.getClientId())
                .claim("scope", config.getScopes().stream().reduce((a, b) -> a + " " + b).orElse(""))
                .jwtID(UUID.randomUUID().toString())
                .issueTime(Date.from(OffsetDateTime.now(DEFAULT_ZONE_ID).toInstant()))
                .expirationTime(Date.from(OffsetDateTime.now(DEFAULT_ZONE_ID).toInstant().plusSeconds(120)))
                .build();

        RSASSASigner signer = new RSASSASigner(nokkel.loadPrivateKey());

        if (nokkel.shouldLockProvider()) {
            signer.getJCAContext().setProvider(nokkel.getKeyStore().getProvider());
        }

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

}
