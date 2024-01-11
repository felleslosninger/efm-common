package no.difi.move.common.cert;

import no.difi.move.common.config.KeystoreProperties;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;


import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

public class KeystoreProviderTest {

    private KeystoreProperties properties;
    private KeyStore keyStore;

    @Before()
    public void setUp() {
        properties = mock(KeystoreProperties.class);
        keyStore = mock(KeyStore.class);
    }

    @Test
    public void testIsBase64() {
        String base64String = "SGVsbG8gd29ybGQ=";
        assertTrue(KeystoreProvider.isBase64(base64String));
    }

    @Test
    public void testLoadKeyStore() throws KeystoreProviderException, IOException {
        KeystoreProperties properties = new KeystoreProperties();
        properties.setType("JKS");
        properties.setAlias("987464291");
        properties.setPassword("changeit");
        properties.setPath(new FileSystemResource("resources/expired-987464291.jks"));
       
            KeyStore keyStore = KeystoreProvider.loadKeyStore(properties);
            assertNotNull(keyStore);
        
    }


    
}