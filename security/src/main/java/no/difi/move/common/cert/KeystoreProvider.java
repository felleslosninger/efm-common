package no.difi.move.common.cert;

import no.difi.move.common.config.KeystoreProperties;
import org.springframework.core.io.Resource;
import org.springframework.web.context.support.ServletContextResource;

import java.io.FileInputStream;
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
        System.out.println("path: " + path);
        System.out.println("path type: " + path.getClass());

        // Create an instance of KeystoreResourceLoader
        KeystoreResourceLoader keystoreResourceLoader = new KeystoreResourceLoader();

        try {
            KeyStore keyStore = KeyStore.getInstance(type);
            if (path instanceof ServletContextResource){
                String pathString = ((ServletContextResource) path).getPath().toString();
                // Check if the content starts with "base64:"
                if (pathString.startsWith("/base64:")) {
                    // Use KeystoreResourceLoader to load the keystore from base64 content
                    keyStore.load(keystoreResourceLoader.getResource(pathString.substring(1)).getInputStream(), password.toCharArray());
                    return keyStore;
                }
            } else if (path != null && path.exists()) {
                // Load the keystore from file
                keyStore.load(new FileInputStream(path.getFile()), password.toCharArray());
                return keyStore;
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
        return null;
    }

    public KeyStore getKeyStore() {
        return keystore;
    }
}