package no.difi.move.common.oauth;

import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import no.difi.asic.SignatureHelper;
import no.difi.move.common.config.KeystoreProperties;

/**
 * Class responsible for accessing the keystore for the Integrasjonspunkt.
 *
 * @author Glebnn Bech
 */
public class KeystoreHelper {

    private final KeystoreProperties keystore;

    public KeystoreHelper(KeystoreProperties keystore) {
        this.keystore = keystore;
    }

    /**
     * Loads the private key from the keystore
     *
     * @return the private key
     */
    public PrivateKey loadPrivateKey() {

        PrivateKey key = null;
        try (InputStream i = this.keystore.getLocation().getInputStream()) {
            KeyStore keystore = getKeystoreEntry(i);
            key = (PrivateKey) keystore.getKey(this.keystore.getAlias(), this.keystore.getEntryPassword().toCharArray());
            return key;
        } catch (CertificateException | KeyStoreException | IOException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            throw new RuntimeException(e);
        }
    }

    public KeyPair getKeyPair() {

        KeyPair result = null;
        try (InputStream i = this.keystore.getLocation().getInputStream()) {
            KeyStore keystore = getKeystoreEntry(i);
            PrivateKey key = (PrivateKey) keystore.getKey(this.keystore.getAlias(), this.keystore.getEntryPassword().toCharArray());
            X509Certificate c = (X509Certificate) keystore.getCertificate(this.keystore.getAlias());
            result = new KeyPair(c.getPublicKey(), key);
        } catch (CertificateException | KeyStoreException | IOException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public X509Certificate getX509Certificate() {

        X509Certificate result = null;
        try (InputStream i = this.keystore.getLocation().getInputStream()) {
            KeyStore keystore = getKeystoreEntry(i);
            result = (X509Certificate) keystore.getCertificate(this.keystore.getAlias());
        } catch (CertificateException | KeyStoreException | IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public SignatureHelper getSignatureHelper() {
        try {
            InputStream keyInputStream = this.keystore.getLocation().getInputStream();
            return new SignatureHelper(keyInputStream, this.keystore.getStorePassword(), this.keystore.getAlias(), this.keystore.getEntryPassword());
        } catch (IOException e) {
            throw new RuntimeException("keystore " + this.keystore.getLocation() + " not found on file system.");
        }
    }

    private KeyStore getKeystoreEntry(final InputStream i) throws IOException, RuntimeException, NoSuchAlgorithmException, CertificateException, KeyStoreException {
        KeyStore keystore = KeyStore.getInstance("JKS");
        keystore.load(i, this.keystore.getStorePassword().toCharArray());
        if (!keystore.containsAlias(this.keystore.getAlias())) {
            throw new RuntimeException("no key with alias " + this.keystore.getAlias() + " found in the keystore "
                    + this.keystore.getLocation());
        }
        if (keystore.isKeyEntry(this.keystore.getAlias())) {
            throw new RuntimeException("no key with alias " + this.keystore.getAlias() + " found in the keystore "
                    + this.keystore.getLocation());
        }
        return keystore;
    }

}
