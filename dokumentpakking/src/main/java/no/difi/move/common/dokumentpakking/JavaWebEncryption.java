package no.difi.move.common.dokumentpakking;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.RSADecrypter;
import com.nimbusds.jose.crypto.RSAEncrypter;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;

public class JavaWebEncryption {

    private JavaWebEncryption() {
        // Utility class
    }

    public static String encrypt(String payload, X509Certificate certificate) {
        JWEHeader header = new JWEHeader.Builder(JWEAlgorithm.RSA_OAEP_256, EncryptionMethod.A256GCM)
            .contentType("JWT") // Required to indicate nested JWT
            .build();

        JWEObject jweObject = new JWEObject(header, new Payload(payload));

        try {
            RSAEncrypter encrypter = new RSAEncrypter((RSAPublicKey) certificate.getPublicKey());
            jweObject.encrypt(encrypter);
        } catch (JOSEException e) {
            throw new EncryptException("Encryption failed!");
        }

        return jweObject.serialize();
    }

    public static String decrypt(String jweToken, PrivateKey privateKey) {
        try {
            JWEObject jweObject = JWEObject.parse(jweToken);
            jweObject.decrypt(new RSADecrypter(privateKey));
            return jweObject.getPayload().toString();
        } catch (JOSEException e) {
            throw new DecryptException("JWE decryption failed!");
        } catch (ParseException e) {
            throw new DecryptException("JWE parsing failed!");
        }
    }
}
