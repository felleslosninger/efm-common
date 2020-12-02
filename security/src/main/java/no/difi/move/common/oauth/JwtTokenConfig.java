package no.difi.move.common.oauth;

import lombok.AllArgsConstructor;
import lombok.Data;
import no.difi.move.common.config.KeystoreProperties;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@Data
@AllArgsConstructor
public class JwtTokenConfig {

    private String clientId;
    private URI tokenUri;
    private String audience;
    private List<String> scopes;

    @Valid
    private KeystoreProperties keystore;

}
