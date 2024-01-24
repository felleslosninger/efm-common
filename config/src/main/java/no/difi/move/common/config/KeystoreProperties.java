package no.difi.move.common.config;

import lombok.Data;
import lombok.ToString;
import org.springframework.core.io.Resource;

import java.security.KeyStore;

@Data
public class KeystoreProperties {

    private String alias;
    @ToString.Exclude
    private String password;
    private String type = KeyStore.getDefaultType();
    private Resource path;
    /**
     * True if the application should only use the Provider from the
     * keyStore for crypto operations on the keys from the keystore.
     */
    private Boolean lockProvider = false;
}
