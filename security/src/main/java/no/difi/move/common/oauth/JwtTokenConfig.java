package no.difi.move.common.oauth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.difi.move.common.config.KeystoreProperties;

import javax.validation.Valid;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtTokenConfig {

    private String clientId;
    private String tokenUri;
    private String audience;
    private List<String> scopes;

    @Valid
    private KeystoreProperties keystore;

}
