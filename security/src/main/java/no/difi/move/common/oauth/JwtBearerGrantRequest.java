package no.difi.move.common.oauth;

import org.springframework.security.oauth2.client.endpoint.AbstractOAuth2AuthorizationGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

public class JwtBearerGrantRequest extends AbstractOAuth2AuthorizationGrantRequest {

    public JwtBearerGrantRequest(ClientRegistration clientRegistration) {
        super(AuthorizationGrantType.JWT_BEARER, clientRegistration);
    }
}
