package no.difi.move.common.oauth;

import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.web.reactive.function.client.WebClient;

public class JwtWebClient {

    public static WebClient.Builder builder(String baseUrl, String registrationId, JwtTokenClient jwtTokenClient) {
        ServletOAuth2AuthorizedClientExchangeFilterFunction filter = getAuthorizedFilter(registrationId, jwtTokenClient);
        filter.setDefaultOAuth2AuthorizedClient(true);
        filter.setDefaultClientRegistrationId(registrationId);

        return WebClient.builder()
            .apply(filter.oauth2Configuration())
            .baseUrl(baseUrl);
    }

    private static ServletOAuth2AuthorizedClientExchangeFilterFunction getAuthorizedFilter(String registrationId, JwtTokenClient jwtTokenClient) {
        ClientRegistration clientRegistration = ClientRegistration.withRegistrationId(registrationId)
            .clientAuthenticationMethod(ClientAuthenticationMethod.PRIVATE_KEY_JWT)
            .authorizationGrantType(AuthorizationGrantType.JWT_BEARER)
            .build();
        ClientRegistrationRepository registrationRepository = new InMemoryClientRegistrationRepository(clientRegistration);
        JwtBearerAccessTokenResponseClient responseClient = new JwtBearerAccessTokenResponseClient(jwtTokenClient);
        JwtBearerOAuth2AuthorizedClientProvider clientProvider = new JwtBearerOAuth2AuthorizedClientProvider(responseClient);
        OAuth2AuthorizedClientService authorizedClientService = new SyncedInMemoryOAuth2AuthorizedClientService(registrationRepository);
        SyncedOauth2AuthorizedClientManager clientManager
            = new SyncedOauth2AuthorizedClientManager(registrationRepository, authorizedClientService);

        OAuth2AuthorizedClientProvider authorizedClientProvider
            = OAuth2AuthorizedClientProviderBuilder.builder()
            .provider(clientProvider)
            .build();
        clientManager.setAuthorizedClientProvider(authorizedClientProvider);

        return new ServletOAuth2AuthorizedClientExchangeFilterFunction(clientManager);
    }
}
