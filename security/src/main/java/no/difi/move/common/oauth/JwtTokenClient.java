package no.difi.move.common.oauth;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * Hentar token frå token-endepunktet med JWT bearer grant.
 * <p>
 * {@link #fetchToken()} og {@link #generateJWT()} krev ikkje webflux på classpath.
 * {@link #fetchTokenMono()} krev at appen sjølv har {@code spring-boot-starter-webflux} som avhengigheit.
 */
@Slf4j
@SuppressWarnings("unused")
public class JwtTokenClient {

    public static final TimeZone DEFAULT_TIME_ZONE = TimeZone.getTimeZone("Europe/Oslo");
    public static final ZoneId DEFAULT_ZONE_ID = DEFAULT_TIME_ZONE.toZoneId();

    private final JwtTokenConfig config;
    private final RestClient restClient;
    private final JWSHeader jwsHeader;
    private final RSASSASigner signer;

    // Blir laga først når fetchTokenMono() blir kalla, slik at webflux ikkje trengst for det synkrone API-et.
    private volatile ReactiveJwtTokenFetcher reactiveFetcher;

    public JwtTokenClient(JwtTokenConfig config) {
        this.config = config;
        this.restClient = RestClient.builder()
            .defaultStatusHandler(new OidcErrorHandler())
            .build();

        this.jwsHeader = JwtHeaderAndSignerFactory.getJwsHeader(config);
        this.signer = JwtHeaderAndSignerFactory.getSigner(config);
    }

    public Mono<JwtTokenResponse> fetchTokenMono() {
        return fetchTokenMono(new JwtTokenInput(), null);
    }

    public Mono<JwtTokenResponse> fetchTokenMono(JwtTokenInput input) {
        return fetchTokenMonoInternal(input, null);
    }

    public Mono<JwtTokenResponse> fetchTokenMono(JwtTokenInput input, JwtTokenAdditionalClaims additionalClaims) {
        return fetchTokenMonoInternal(input, additionalClaims);
    }

    private Mono<JwtTokenResponse> fetchTokenMonoInternal(JwtTokenInput input, JwtTokenAdditionalClaims additionalClaims) {
        return getReactiveFetcher().fetchTokenMono(() -> createTokenRequestForm(input, additionalClaims));
    }

    private ReactiveJwtTokenFetcher getReactiveFetcher() {
        ReactiveJwtTokenFetcher fetcher = reactiveFetcher;
        if (fetcher == null) {
            synchronized (this) {
                fetcher = reactiveFetcher;
                if (fetcher == null) {
                    fetcher = new ReactiveJwtTokenFetcher(config.getTokenUri());
                    reactiveFetcher = fetcher;
                }
            }
        }
        return fetcher;
    }

    @Retryable(value = HttpClientErrorException.class, maxRetries = Integer.MAX_VALUE, delay = 5000, maxDelay = 1000 * 60 * 60, multiplier = 3)
    public JwtTokenResponse fetchToken() {
        return fetchToken(new JwtTokenInput(), null);
    }

    @Retryable(value = HttpClientErrorException.class, maxRetries = Integer.MAX_VALUE, delay = 5000, maxDelay = 1000 * 60 * 60, multiplier = 3)
    public JwtTokenResponse fetchToken(JwtTokenInput input) {
        return fetchTokenInternal(input, null);
    }

    @Retryable(value = HttpClientErrorException.class, maxRetries = Integer.MAX_VALUE, delay = 5000, maxDelay = 1000 * 60 * 60, multiplier = 3)
    public JwtTokenResponse fetchToken(JwtTokenInput input, JwtTokenAdditionalClaims additionalClaims) {
        return fetchTokenInternal(input, additionalClaims);
    }

    private JwtTokenResponse fetchTokenInternal(JwtTokenInput input, JwtTokenAdditionalClaims additionalClaims) {
        return restClient.post()
            .uri(config.getTokenUri())
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .accept(MediaType.APPLICATION_JSON)
            .body(createTokenRequestForm(input, additionalClaims))
            .retrieve()
            .body(JwtTokenResponse.class);
    }

    private LinkedMultiValueMap<String, String> createTokenRequestForm(JwtTokenInput input, JwtTokenAdditionalClaims additionalClaims) {
        LinkedMultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer");
        form.add("assertion", generateJWT(input, additionalClaims));
        return form;
    }

    public String generateJWT() {
        return generateJWT(new JwtTokenInput());
    }

    public String generateJWT(JwtTokenInput input) {
        return generateJWTInternal(input, null);
    }

    public String generateJWT(JwtTokenInput input, JwtTokenAdditionalClaims additionalClaims) {
        return generateJWTInternal(input, additionalClaims);
    }

    private String generateJWTInternal(JwtTokenInput input, JwtTokenAdditionalClaims additionalClaims) {
        JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder()
            .audience(Optional.ofNullable(input.getAudience()).orElse(config.getAudience()))
            .issuer(Optional.ofNullable(input.getClientId()).orElse(config.getClientId()))
            .claim("scope", getScopeString(input))
            .jwtID(UUID.randomUUID().toString())
            .issueTime(Date.from(OffsetDateTime.now(DEFAULT_ZONE_ID).toInstant()))
            .expirationTime(Date.from(OffsetDateTime.now(DEFAULT_ZONE_ID).toInstant().plusSeconds(120)));

        Optional.ofNullable(input.getConsumerOrg())
            .ifPresent(consumerOrg -> builder.claim("consumer_org", consumerOrg));

        Optional.ofNullable(input.getOnBehalfOf())
            .ifPresent(onBehalfOf -> builder.claim("iss_onbehalfof", onBehalfOf));

        if (additionalClaims != null) {
            additionalClaims.getClaims().forEach(builder::claim);
        }

        JWTClaimsSet claims = builder
            .build();

        SignedJWT signedJWT = new SignedJWT(jwsHeader, claims);
        try {
            signedJWT.sign(signer);
        } catch (JOSEException e) {
            log.error("Error occured during signing of JWT", e);
        }

        String serializedJwt = signedJWT.serialize();

        String[] chunks = serializedJwt.split("\\.");

        java.util.Base64.Decoder decoder = java.util.Base64.getUrlDecoder();

        String payload = new String(decoder.decode(chunks[1]));

        log.info("Payload: {}", payload);
        log.debug("SerializedJWT: {}", serializedJwt);

        return serializedJwt;
    }

    private String getScopeString(JwtTokenInput input) {
        List<String> scopes = Optional.ofNullable(input.getScopes()).orElse(config.getScopes());
        return scopes.stream().reduce((a, b) -> a + " " + b).orElse("");
    }
}
