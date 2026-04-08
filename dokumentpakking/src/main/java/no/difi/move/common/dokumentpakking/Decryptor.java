package no.difi.move.common.dokumentpakking;

import lombok.RequiredArgsConstructor;
import no.difi.move.common.cert.KeystoreHelper;
import org.springframework.core.io.Resource;

@RequiredArgsConstructor
public class Decryptor {

    private final DecryptCMSDocument decryptCMSDocument;
    private final KeystoreHelper keystoreHelper;

    public Resource decrypt(Resource encrypted) {
        return decryptCMSDocument.decrypt(DecryptCMSDocument.Input.builder()
            .resource(encrypted)
            .keystoreHelper(keystoreHelper)
            .build());
    }
}
