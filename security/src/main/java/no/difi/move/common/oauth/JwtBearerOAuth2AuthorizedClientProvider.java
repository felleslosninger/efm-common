package no.difi.move.common.oauth;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.client.OAuth2AuthorizationContext;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AbstractOAuth2Token;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.util.Assert;

import java.time.Clock;
import java.time.Duration;

@Slf4j
@Setter
@RequiredArgsConstructor
public class JwtBearerOAuth2AuthorizedClientProvider implements OAuth2AuthorizedClientProvider {

    private final OAuth2AccessTokenResponseClient<JwtBearerGrantRequest> tokenResponseClient;
    private Duration clockSkew = Duration.ofSeconds(30);
    private Clock clock = Clock.systemUTC();

    @Override
    @Nullable
    @Synchronized
    public OAuth2AuthorizedClient authorize(OAuth2AuthorizationContext context) {
        Assert.notNull(context, "context cannot be null");

        log.debug("Attempts to authorize client assuming JWT bearer token grant type");

        ClientRegistration clientRegistration = context.getClientRegistration();

        if (!AuthorizationGrantType.JWT_BEARER.equals(clientRegistration.getAuthorizationGrantType())) {
            log.debug("Invalid grant type: {}", clientRegistration.getAuthorizationGrantType());
            return null;
        }

        OAuth2AuthorizedClient authorizedClient = context.getAuthorizedClient();
        if (null != authorizedClient && !isExpired(authorizedClient.getAccessToken())) {
            return authorizedClient;
        }
        JwtBearerGrantRequest jwtBearerGrantRequest = new JwtBearerGrantRequest(clientRegistration);
        OAuth2AccessTokenResponse tokenResponse = tokenResponseClient.getTokenResponse(jwtBearerGrantRequest);

        return new OAuth2AuthorizedClient(clientRegistration, context.getPrincipal().getName(), tokenResponse.getAccessToken());
    }

    private boolean isExpired(AbstractOAuth2Token token) {
        if (token.getExpiresAt() != null) {
            return clock.instant().isAfter(token.getExpiresAt().minus(this.clockSkew));
        }
        return true;
    }
}
