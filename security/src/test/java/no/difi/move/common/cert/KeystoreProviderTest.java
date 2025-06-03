package no.difi.move.common.cert;

import no.difi.move.common.config.KeystoreProperties;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

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

    @Test
    void testLoadKeyStoreFromByteArrayResource() throws KeystoreProviderException, IOException {
        // Arrange
        KeystoreProperties properties = new KeystoreProperties();
        properties.setAlias(ALIAS);
        properties.setPassword(PASSWORD);
        // Read the content of the file as a string
        String fileContent = String.join("\n", Files.readAllLines(Paths.get("src/test/java/no/difi/move/common/cert/resources/encoded-987464291")));
        // Create a ByteArrayResource using the decoded file content without prefix.
        fileContent = fileContent.substring("Base64:".length());
        Resource byteArrayResource = new ByteArrayResource(Base64.getDecoder().decode(fileContent));
        properties.setPath(byteArrayResource);
        // Act
        KeyStore keyStore = KeystoreProvider.loadKeyStore(properties);
        // Assert
        assertNotNull(keyStore);
    }
}
