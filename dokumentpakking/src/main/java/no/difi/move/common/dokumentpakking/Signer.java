package no.difi.move.common.dokumentpakking;

import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.security.*;

@SuppressWarnings("unused")
@RequiredArgsConstructor
public class Signer {

    public byte[] sign(Input input) {
        try {
            Signature signature = Signature.getInstance(input.algorithm);
            signature.initSign(input.privateKey);

            byte[] buffer = new byte[input.bufferSize];

            try (InputStream is = input.getResource().getInputStream()) {
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    signature.update(buffer, 0, bytesRead);
                }
            }

            return signature.sign();
        } catch (NoSuchAlgorithmException e) {
            throw new SignatureException("No such signing algorithm", e);
        } catch (InvalidKeyException e) {
            throw new SignatureException("Invalid signing key", e);
        } catch (IOException | java.security.SignatureException e) {
            throw new SignatureException("Signing failed", e);
        }
    }

    @Value
    @Builder
    public static class Input {
        @NonNull
        Resource resource;
        @NonNull
        PrivateKey privateKey;
        @Builder.Default
        String algorithm = "SHA256withRSA";
        @Builder.Default
        int bufferSize = 16384;
    }
}
