package no.difi.move.common.cert;

import no.difi.move.common.config.KeystoreProperties;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public class KeystoreProvider {

    private final KeyStore keystore;

    public KeystoreProvider(KeyStore keyStore) {
        this.keystore = keyStore;
    }

    public static KeystoreProvider from(KeystoreProperties properties) throws KeystoreProviderException {
        final KeyStore keyStore = loadKeyStore(properties);
        return new KeystoreProvider(keyStore);
    }

    public static KeyStore loadKeyStore(KeystoreProperties properties) throws KeystoreProviderException {
        String type = properties.getType();
        String password = properties.getPassword();
        Resource path = properties.getPath();

        try {
            KeyStore keyStore = KeyStore.getInstance(type);

            if (path instanceof ByteArrayResource) {
                keyStore.load(path.getInputStream(), password.toCharArray());
                return keyStore;
            } else if (path != null && path.exists()) {
                // Load the keystore from file
                try (InputStream fileInputStream = new FileInputStream(path.getFile())) {
                    keyStore.load(fileInputStream, password.toCharArray());
                    return keyStore;
                }
            } else {
                throw new KeystoreProviderException("No keystore file found at " + path.getFile());
            }
        } catch (KeyStoreException e) {
            throw new KeystoreProviderException("Unable to load KeyStore", e);
        } catch (IOException e) {
            throw new KeystoreProviderException("Could not open keystore file", e);
        } catch (CertificateException | NoSuchAlgorithmException e) {
            throw new KeystoreProviderException("Unable to load keystore file", e);
        }
    }


    public KeyStore getKeyStore() {
        return keystore;
    }
}
