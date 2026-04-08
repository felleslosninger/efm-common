package no.difi.move.common.dokumentpakking;

import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.cert.X509Certificate;

@SuppressWarnings("unused")
@RequiredArgsConstructor
public class SignatureValidator {

    public boolean isValid(Input input) {
        try {
            Signature signature = Signature.getInstance(input.algorithm);
            signature.initVerify(input.certificate);

            byte[] buffer = new byte[input.bufferSize];

            try (InputStream is = input.getResource().getInputStream()) {
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    signature.update(buffer, 0, bytesRead);
                }
            }

            return signature.verify(input.signature);
        } catch (NoSuchAlgorithmException e) {
            throw new SignatureException("No such signing algorithm", e);
        } catch (InvalidKeyException e) {
            throw new SignatureException("Invalid certificate", e);
        } catch (IOException | java.security.SignatureException e) {
            throw new SignatureException("Signature verification failed", e);
        }
    }

    @Value
    @Builder
    public static class Input {
        @NonNull
        Resource resource;
        @NonNull
        X509Certificate certificate;
        byte[] signature;
        @Builder.Default
        String algorithm = "SHA256withRSA";
        @Builder.Default
        int bufferSize = 16384;
    }
}
