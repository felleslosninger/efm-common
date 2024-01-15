package no.difi.move.common.cert;

import no.difi.move.common.config.KeystoreProperties;
import org.apache.commons.codec.binary.Base64InputStream;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
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

            if (path == null || "none".equalsIgnoreCase(path.getFilename())) {
                keyStore.load(null, password.toCharArray());
            } else if (path instanceof ByteArrayResource) {
                keyStore.load(new Base64InputStream(path.getInputStream()), password.toCharArray());
            } else {
                keyStore.load(path.getInputStream(), password.toCharArray());
            }

            return keyStore;
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
