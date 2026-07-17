package no.difi.move.common.oauth;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.client.MockClientHttpResponse;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OidcErrorHandlerTest {

    private static final URI URL = URI.create("http://localhost/token");
    private static final String BODY = "{\"error\":\"invalid_client\"}";

    private final OidcErrorHandler errorHandler = new OidcErrorHandler();

    // Bruker InputStream-konstruktøren slik at body berre kan lesast éin gong,
    // som for ein ekte HTTP-respons.
    private MockClientHttpResponse oneShotResponse(HttpStatus status) {
        return new MockClientHttpResponse(
            new ByteArrayInputStream(BODY.getBytes(StandardCharsets.UTF_8)), status);
    }

    @Test
    void clientErrorShouldRetainResponseBodyInException() {
        var response = oneShotResponse(HttpStatus.BAD_REQUEST);

        var exception = assertThrows(HttpClientErrorException.class,
            () -> errorHandler.handleError(URL, HttpMethod.POST, response));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals(BODY, exception.getResponseBodyAsString(),
            "Exception should retain the response body even though it was already read for logging");
    }

    @Test
    void serverErrorShouldRetainResponseBodyInException() {
        var response = oneShotResponse(HttpStatus.INTERNAL_SERVER_ERROR);

        var exception = assertThrows(HttpServerErrorException.class,
            () -> errorHandler.handleError(URL, HttpMethod.POST, response));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertEquals(BODY, exception.getResponseBodyAsString(),
            "Exception should retain the response body even though it was already read for logging");
    }

    @Test
    void unknownStatusShouldThrowRestClientException() {
        var response = oneShotResponse(HttpStatus.FOUND);

        assertThrows(RestClientException.class,
            () -> errorHandler.handleError(URL, HttpMethod.POST, response));
    }
}
