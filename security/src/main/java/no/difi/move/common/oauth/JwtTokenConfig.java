package no.difi.move.common.oauth;

import lombok.Data;
import lombok.NoArgsConstructor;
import no.difi.move.common.config.JwkProperties;
import no.difi.move.common.config.KeystoreProperties;

import jakarta.validation.Valid;

import java.util.List;

@Data
@NoArgsConstructor
public class JwtTokenConfig {

    public JwtTokenConfig(String clientId, String tokenUri, String audience, List<String> scopes, KeystoreProperties keystore
    ) {
        this.clientId = clientId;
        this.tokenUri = tokenUri;
        this.audience = audience;
        this.scopes = scopes;
        this.keystore = keystore;
        authenticationType = AuthenticationType.CERTIFICATE;
    }

    public JwtTokenConfig(String clientId, String tokenUri, String audience, List<String> scopes, JwkProperties jwk
    ) {
        this.clientId = clientId;
        this.tokenUri = tokenUri;
        this.audience = audience;
        this.scopes = scopes;
        this.jwk = jwk;
        authenticationType = AuthenticationType.JWK;
    }

    private String clientId;
    private String tokenUri;
    private String audience;
    private List<String> scopes;

    private AuthenticationType authenticationType = AuthenticationType.CERTIFICATE;

    @Valid
    private KeystoreProperties keystore;

    @Valid
    private JwkProperties jwk;
}
