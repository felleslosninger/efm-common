package no.difi.meldingsutveksling.domain;

import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name = "encryptedmessage", namespace = "urn:no:difi:digitalpost:json:schema::dialogmelding")
public class EncryptedBusinessMessage implements BusinessMessage {

    public static final CertificateFactory cf;

    static {
        try {
            cf = CertificateFactory.getInstance("X.509");
        } catch (CertificateException e) {
            throw new RuntimeException("Not able to initialize X.509 certificate factory",e);
        }
    }

    private String base64DerEncryptionCertificate;
    private String message;

    public X509Certificate x509Certificate() throws CertificateException {
        return (X509Certificate)cf.generateCertificate(new ByteArrayInputStream( Base64.getDecoder().decode(base64DerEncryptionCertificate.getBytes(StandardCharsets.UTF_8))));
    }

}
