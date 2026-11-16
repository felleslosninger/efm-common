package no.difi.move.common.oauth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.function.Supplier;

/**
 * Den reaktive delen av {@link JwtTokenClient}. Ligg i eigen klasse slik at {@link JwtTokenClient}
 * kan lastast og brukast utan webflux på classpath. Klassen blir først lasta når
 * {@link JwtTokenClient#fetchTokenMono()} blir kalla.
 */
@Slf4j
class ReactiveJwtTokenFetcher {

    private final WebClient wc;

    ReactiveJwtTokenFetcher(String tokenUri) {
        this.wc = WebClient.create(tokenUri);
    }

    Mono<JwtTokenResponse> fetchTokenMono(Supplier<LinkedMultiValueMap<String, String>> formSupplier) {
        return wc.post()
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.fromSupplier(formSupplier), LinkedMultiValueMap.class)
            .retrieve()
            .onStatus(HttpStatusCode::isError, e -> e.bodyToMono(String.class)
                .flatMap(s -> Mono.error(new JwtTokenException("http status: " + e.statusCode() + ", body: " + s)))
            )
            .bodyToMono(JwtTokenResponse.class)
            .retryWhen(Retry.backoff(Long.MAX_VALUE, Duration.ofSeconds(2L))
                .maxBackoff(Duration.ofMinutes(1L))
                .doBeforeRetry(rs -> log.warn("Error connecting to token endpoint, retrying.. {}", rs)))
            .doOnNext(res -> log.debug("Response: {}", res))
            .cache(r -> Duration.ofSeconds(r.getExpiresIn() - 10L), t -> Duration.ZERO, () -> Duration.ZERO);
    }
}
