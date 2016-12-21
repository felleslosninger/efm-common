package no.difi.move.common.config;

import lombok.Data;
import org.springframework.core.io.Resource;

/**
 *
 * @author Nikolai Luthman <nikolai dot luthman at inmeta dot no>
 */
@Data
public class KeystoreProperties {

    private String alias;
    private String storePassword;
    private String entryPassword;
    private Resource location;
}
