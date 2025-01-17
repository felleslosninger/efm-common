package no.difi.move.common.oauth;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
public class OidcErrorHandler extends DefaultResponseErrorHandler {

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        log.error("Response from token provider: " + IOUtils.toString(response.getBody(), StandardCharsets.UTF_8));
        var status = HttpStatus.valueOf(response.getStatusCode().value());
        switch (status.series()) {
            case CLIENT_ERROR:
                throw new HttpClientErrorException(status, response.getStatusText(),
                        response.getHeaders(), getResponseBody(response), getCharset(response));
            case SERVER_ERROR:
                throw new HttpServerErrorException(status, response.getStatusText(),
                        response.getHeaders(), getResponseBody(response), getCharset(response));
            default:
                throw new RestClientException("Unknown status code [" + status + "]");
        }
    }
}
