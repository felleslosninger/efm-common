package no.difi.move.common.oauth;

import com.sun.net.httpserver.HttpServer;
import no.difi.move.common.config.JwkProperties;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Enkelt token-endepunkt for testar, basert på JDK sin HttpServer så testane ikkje er avhengige av ein bestemt HTTP-stakk.
 */
class TokenEndpointStub implements AutoCloseable {

    static final String TOKEN_RESPONSE = """
        {"access_token":"test-access-token","expires_in":120,"scope":"move/dpo.read"}""";

    record ReceivedRequest(String contentType, Map<String, String> form) {
    }

    private final HttpServer server;
    private final List<ReceivedRequest> requests = new CopyOnWriteArrayList<>();
    private volatile int status = 200;
    private volatile String responseBody = TOKEN_RESPONSE;
    private volatile Duration responseDelay = Duration.ZERO;
    // Slepper ventande svar når stubben blir lukka, så server.stop() ikkje må vente ut forseinkinga.
    private final CountDownLatch closed = new CountDownLatch(1);

    TokenEndpointStub() throws IOException {
        server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0);
        server.createContext("/token", exchange -> {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            requests.add(new ReceivedRequest(exchange.getRequestHeaders().getFirst("Content-Type"), parseForm(body)));
            awaitResponseDelay();
            byte[] response = responseBody.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(status, response.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response);
            }
        });
        server.start();
    }

    TokenEndpointStub respondWith(int status, String body) {
        this.status = status;
        this.responseBody = body;
        return this;
    }

    /**
     * Ventar så lenge før endepunktet svarar, for å simulere eit token-endepunkt som heng.
     */
    TokenEndpointStub respondAfter(Duration delay) {
        this.responseDelay = delay;
        return this;
    }

    private void awaitResponseDelay() {
        if (responseDelay.isZero()) {
            return;
        }
        try {
            closed.await(responseDelay.toMillis(), TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    String tokenUri() {
        return "http://localhost:" + server.getAddress().getPort() + "/token";
    }

    List<ReceivedRequest> requests() {
        return requests;
    }

    JwtTokenConfig config() {
        var jwk = new JwkProperties();
        jwk.setPath(new ClassPathResource("test.jwk"));
        return new JwtTokenConfig("test-client", tokenUri(), "https://test.maskinporten.no/", List.of("move/dpo.read"), jwk);
    }

    private static Map<String, String> parseForm(String body) {
        return Arrays.stream(body.split("&"))
            .map(pair -> pair.split("=", 2))
            .collect(Collectors.toMap(
                kv -> URLDecoder.decode(kv[0], StandardCharsets.UTF_8),
                kv -> kv.length > 1 ? URLDecoder.decode(kv[1], StandardCharsets.UTF_8) : ""));
    }

    @Override
    public void close() {
        closed.countDown();
        server.stop(0);
    }
}
