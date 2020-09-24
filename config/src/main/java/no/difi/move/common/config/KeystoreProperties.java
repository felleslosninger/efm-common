package no.difi.move.common.config;

import lombok.Data;
import org.springframework.core.io.Resource;

import java.security.KeyStore;

/**
 *
 * @author Nikolai Luthman <nikolai dot luthman at inmeta dot no>
 */
@Data
public class KeystoreProperties {

    private String alias;
    private String storePassword;
    private String entryPassword;
    private String type = KeyStore.getDefaultType();
    private Resource location;

}
