package no.difi.move.common.oauth;

import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.web.reactive.function.client.WebClient;

public class JwtWebClient {

    public static WebClient create(String baseUrl, String registrationId, JwtTokenClient jwtTokenClient) {
        ServletOAuth2AuthorizedClientExchangeFilterFunction filter = getAuthorizedFilter(registrationId, jwtTokenClient);
        filter.setDefaultClientRegistrationId(registrationId);

        return WebClient.builder()
                .apply(filter.oauth2Configuration())
                .baseUrl(baseUrl)
                .build();
    }

    public static WebClient createWithReactorClientConnector(String baseUrl, String registrationId, JwtTokenClient jwtTokenClient, ReactorClientHttpConnector connector) {
        ServletOAuth2AuthorizedClientExchangeFilterFunction filter = getAuthorizedFilter(registrationId, jwtTokenClient);
        filter.setDefaultClientRegistrationId(registrationId);

        return WebClient.builder()
                .apply(filter.oauth2Configuration())
                .clientConnector(connector)
                .baseUrl(baseUrl)
                .build();
    }

    private static ServletOAuth2AuthorizedClientExchangeFilterFunction getAuthorizedFilter(String registrationId, JwtTokenClient jwtTokenClient) {
        ClientRegistration clientRegistration = ClientRegistration.withRegistrationId(registrationId)
                .authorizationGrantType(AuthorizationGrantType.JWT_BEARER)
                .build();
        ClientRegistrationRepository registrationRepository = new InMemoryClientRegistrationRepository(clientRegistration);
        JwtBearerAccessTokenResponseClient responseClient = new JwtBearerAccessTokenResponseClient(jwtTokenClient);
        JwtBearerOAuth2AuthorizedClientProvider clientProvider = new JwtBearerOAuth2AuthorizedClientProvider(responseClient);
        OAuth2AuthorizedClientService authorizedClientService = new InMemoryOAuth2AuthorizedClientService(registrationRepository);

        AuthorizedClientServiceOAuth2AuthorizedClientManager clientManager
                = new AuthorizedClientServiceOAuth2AuthorizedClientManager(registrationRepository, authorizedClientService);
        OAuth2AuthorizedClientProvider authorizedClientProvider
                = OAuth2AuthorizedClientProviderBuilder.builder()
                .provider(clientProvider)
                .build();
        clientManager.setAuthorizedClientProvider(authorizedClientProvider);
        return new ServletOAuth2AuthorizedClientExchangeFilterFunction(clientManager);
    }


}
