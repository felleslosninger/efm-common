package no.difi.move.common.oauth;

import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.resource.UserApprovalRequiredException;
import org.springframework.security.oauth2.client.resource.UserRedirectRequiredException;
import org.springframework.security.oauth2.client.token.AccessTokenProvider;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;

import java.time.Instant;
import java.util.Collections;
import java.util.Date;

/**
 * @author Nikolai Luthman <nikolai dot luthman at inmeta dot no>
 */
public class Oauth2JwtAccessTokenProvider implements AccessTokenProvider {

    private final NimbusDSJwtTokenClient tokenClient;

    public Oauth2JwtAccessTokenProvider(NimbusDSJwtTokenClient tokenClient) {
        this.tokenClient = tokenClient;
    }

    private DefaultOAuth2AccessToken getAccessToken() {
        JwtTokenResponse oidcTokenResponse = tokenClient.fetchToken();
        DefaultOAuth2AccessToken oa2at = new DefaultOAuth2AccessToken(oidcTokenResponse.getAccessToken());
        oa2at.setExpiration(Date.from(Instant.now().plusSeconds(250)));
        oa2at.setScope(Collections.singleton(oidcTokenResponse.getScope()));
        return oa2at;
    }

    @Override
    public OAuth2AccessToken obtainAccessToken(OAuth2ProtectedResourceDetails details, AccessTokenRequest parameters) throws UserRedirectRequiredException, UserApprovalRequiredException {
        return getAccessToken();
    }

    @Override
    public boolean supportsResource(OAuth2ProtectedResourceDetails resource) {
        return false;
    }

    @Override
    public OAuth2AccessToken refreshAccessToken(OAuth2ProtectedResourceDetails resource, OAuth2RefreshToken refreshToken, AccessTokenRequest request) throws UserRedirectRequiredException {
        return getAccessToken();
    }

    @Override
    public boolean supportsRefresh(OAuth2ProtectedResourceDetails resource) {
        return false;
    }
}
