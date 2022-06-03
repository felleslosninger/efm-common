package no.difi.meldingsutveksling.ad;

import org.apache.http.client.HttpClient;

import java.util.Optional;

public interface AdIntegrationInput {

    int getConnectTimeout();

    int getReadTimeout();

    String getAuthUrl();

    String getGraphUrl();

    String getClientId();

    String getGraphResource();

    String getUsernamePrefix();

    default Optional<HttpClient> httpClient() {
        return Optional.empty();
    }
}
