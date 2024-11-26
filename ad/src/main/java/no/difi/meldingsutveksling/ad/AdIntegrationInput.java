package no.difi.meldingsutveksling.ad;

import org.apache.hc.client5.http.classic.HttpClient;

import java.util.Optional;

public interface AdIntegrationInput {

    int getConnectTimeout();

    int getReadTimeout();

    String getAuthUrl();

    String getGraphUrl();

    String getClientId();

    String getGraphResource();

    String getUsernamePrefix();

    default Optional<org.apache.hc.client5.http.classic.HttpClient> httpClient() {
        return Optional.empty();
    }
}
