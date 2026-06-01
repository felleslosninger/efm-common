package no.difi.move.common.dokumentpakking.config;

import no.difi.certvalidator.BusinessCertificateValidator;
import no.difi.move.common.dokumentpakking.AsicParser;
import no.difi.move.common.dokumentpakking.CreateAsice;
import no.difi.move.common.dokumentpakking.CreateCMSDocument;
import no.difi.move.common.dokumentpakking.CreateCMSEncryptedAsice;
import no.difi.move.common.dokumentpakking.DecryptCMSDocument;
import no.difi.move.common.dokumentpakking.VerifyJWT;
import no.difi.move.common.io.pipe.Plumber;
import no.difi.move.common.io.pipe.PromiseMaker;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.security.Security;

@AutoConfiguration
@Import({Plumber.class, PromiseMaker.class})
public class DokumentpakkingAutoConfig {

    static {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    @Bean
    public CreateAsice createAsice(Plumber plumber) {
        return new CreateAsice(plumber);
    }

    @Bean
    public CreateCMSEncryptedAsice createCmsEncryptedAsice(
        PromiseMaker promiseMaker,
        CreateAsice createASiCE,
        CreateCMSDocument createCMS) {
        return new CreateCMSEncryptedAsice(promiseMaker, createASiCE, createCMS);
    }

    @Bean
    public CreateCMSDocument createCMSDocument(Plumber plumber) {
        return new CreateCMSDocument(plumber);
    }

    @Bean
    public DecryptCMSDocument decryptCMSDocument() {
        return new DecryptCMSDocument();
    }

    @Bean
    public AsicParser asicParser() {
        return new AsicParser();
    }

    @Bean
    @ConditionalOnBean(BusinessCertificateValidator.class)
    public VerifyJWT verifyJWT(BusinessCertificateValidator businessCertificateValidator) {
        return new VerifyJWT(businessCertificateValidator);
    }
}
