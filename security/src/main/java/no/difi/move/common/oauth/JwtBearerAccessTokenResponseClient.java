package no.difi.move.common.oauth;

import com.google.common.collect.Sets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.endpoint.JwtBearerGrantRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.util.Assert;

import java.util.Set;

@Slf4j
@RequiredArgsConstructor
public class JwtBearerAccessTokenResponseClient implements OAuth2AccessTokenResponseClient<JwtBearerGrantRequest> {

    private final JwtTokenClient jwtTokenClient;

    @Override
    public OAuth2AccessTokenResponse getTokenResponse(JwtBearerGrantRequest authorizationGrantRequest) {
        Assert.notNull(authorizationGrantRequest, "authorizationGrantRequest cannot be null");
        JwtTokenResponse jwtTokenResponse = jwtTokenClient.fetchToken();

        String accessToken = jwtTokenResponse.getAccessToken();
        log.debug("Received access token:\n{}", accessToken);
        Set<String> scope = Sets.newHashSet(jwtTokenResponse.getScope().split(" "));
        return OAuth2AccessTokenResponse.withToken(accessToken)
                .tokenType(OAuth2AccessToken.TokenType.BEARER)
                .expiresIn(jwtTokenResponse.getExpiresIn())
                .scopes(scope)
                .build();
    }

}
