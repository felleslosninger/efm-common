package no.difi.move.common.cert;

import no.difi.move.common.config.KeystoreProperties;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;
import static org.junit.jupiter.api.Assertions.*;

public class KeystoreProviderTest {

    private static final String ALIAS = "987464291";
    private static final String PASSWORD = "changeit";
    private static final String TYPE = "JKS";

    @Test
    void testLoadKeyStoreFromFile() throws KeystoreProviderException {
        KeystoreProperties properties = new KeystoreProperties();
        properties.setAlias(ALIAS);
        properties.setPassword(PASSWORD);
        properties.setPath(new FileSystemResource("src/test/java/no/difi/move/common/cert/resources/expired-987464291.jks"));
        properties.setType(TYPE);
        KeyStore keyStore = KeystoreProvider.loadKeyStore(properties);
        assertNotNull(keyStore);
    }

    @Test
    void testLoadEmptyKeyStore() throws KeystoreProviderException {
        KeystoreProperties properties = new KeystoreProperties();
        properties.setAlias(ALIAS);
        properties.setPassword(PASSWORD);
        properties.setPath(null);
        properties.setType(TYPE);
        KeyStore keyStore = KeystoreProvider.loadKeyStore(properties);
        assertNotNull(keyStore);
    }


    @Test
    void testLoadKeyStoreFromBase64EncodedFile() throws KeystoreProviderException, IOException {
        String base64EncodedContent = new String(Files.readAllBytes(Paths.get("src/test/java/no/difi/move/common/cert/resources/encoded-987464291")), StandardCharsets.UTF_8);
        Resource keystoreResource = new ByteArrayResource(base64EncodedContent.getBytes());

        KeystoreProperties properties = new KeystoreProperties();
        properties.setAlias(ALIAS);
        properties.setPassword(PASSWORD);
        properties.setPath(keystoreResource);
        properties.setType(TYPE);

        KeyStore keyStore = KeystoreProvider.loadKeyStore(properties);
        assertNotNull(keyStore);
    }

    @Test
    void testLoadKeyStoreException() {
        KeystoreProperties properties = new KeystoreProperties();
        properties.setAlias(ALIAS);
        properties.setPassword(PASSWORD);
        properties.setPath(new FileSystemResource("/path/to/invalidType.keystore"));
        properties.setType("InvalidType");
        assertThrows(KeystoreProviderException.class, () -> KeystoreProvider.loadKeyStore(properties));
    }

    @Test
    void testLoadKeyStoreIOException() {
        KeystoreProperties properties = new KeystoreProperties();
        properties.setAlias(ALIAS);
        properties.setPassword(PASSWORD);
        properties.setPath(new FileSystemResource("/nonexistent/path/to/nonExistentPath.keystore"));
        properties.setType(TYPE);
        assertThrows(KeystoreProviderException.class, () -> KeystoreProvider.loadKeyStore(properties));
    }
}
