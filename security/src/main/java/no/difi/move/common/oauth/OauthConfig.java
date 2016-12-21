package no.difi.move.common.oauth;

import java.net.URL;
import java.util.List;
import javax.validation.Valid;
import lombok.Data;
import no.difi.move.common.config.KeystoreProperties;

/**
 *
 * @author Nikolai Luthman <nikolai dot luthman at inmeta dot no>
 */
@Data
public class OauthConfig {

    private String clientId;
    private URL url;
    private List<String> scopes;
    private String audience;

    @Valid
    private KeystoreProperties keystore;

}
