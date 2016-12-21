package no.difi.move.common.oauth;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.util.Base64;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.cert.CertificateEncodingException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class NimbusDSJwtTokenClient {

    private static final Logger log = LoggerFactory.getLogger(NimbusDSJwtTokenClient.class);

    private OauthConfig props;

    public NimbusDSJwtTokenClient(OauthConfig props) {
        this.props = props;
    }

    public JwtTokenResponse fetchToken() {
        RestTemplate restTemplate = new RestTemplate();

        LinkedMultiValueMap<String, String> attrMap = new LinkedMultiValueMap<>();
        attrMap.add("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer");
        attrMap.add("assertion", generateJWT());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(attrMap, headers);

        FormHttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();
        restTemplate.getMessageConverters().add(formHttpMessageConverter);

        URI accessTokenUri;
        try {
            accessTokenUri = props.getUrl().toURI();
        } catch (URISyntaxException e) {
            log.error("Error converting property to URI", e);
            throw new RuntimeException(e);
        }

        ResponseEntity<JwtTokenResponse> response = restTemplate.exchange(accessTokenUri, HttpMethod.POST,
                httpEntity, JwtTokenResponse.class);
        log.info("Response: {}", response.toString());

        return response.getBody();
    }

    public String generateJWT() {
        KeystoreHelper nokkel = new KeystoreHelper(props.getKeystore());

        List<Base64> certChain = new ArrayList<>();
        try {
            certChain.add(Base64.encode(nokkel.getX509Certificate().getEncoded()));
        } catch (CertificateEncodingException e) {
            log.error("Could not get encoded certificate", e);
            throw new RuntimeException(e);
        }

        JWSHeader jwsHeader = new JWSHeader.Builder(JWSAlgorithm.RS256).x509CertChain(certChain).build();
        String scopes = "";
        if (props.getScopes() != null) {
            scopes = props.getScopes().stream().reduce((a, b) -> a + " " + b).orElse("");
        }

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .audience(props.getAudience())
                .issuer(props.getClientId())
                .claim("scope", scopes)
                .jwtID(UUID.randomUUID().toString())
                .issueTime(Date.from(Instant.now()))
                .expirationTime(Date.from(Instant.now().plusSeconds(120)))
                .build();

        JWSSigner signer = new RSASSASigner(nokkel.loadPrivateKey());
        SignedJWT signedJWT = new SignedJWT(jwsHeader, claims);
        try {
            signedJWT.sign(signer);
        } catch (JOSEException e) {
            log.error("Error occured during signing of JWT", e);
        }

        String serializedJwt = signedJWT.serialize();
        log.info("SerializedJWT: {}", serializedJwt);

        return serializedJwt;
    }

}
