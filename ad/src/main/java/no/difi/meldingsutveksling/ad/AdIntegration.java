package no.difi.meldingsutveksling.ad;

import com.microsoft.aad.adal4j.AuthenticationContext;
import com.microsoft.aad.adal4j.AuthenticationResult;
import lombok.extern.slf4j.Slf4j;
import no.difi.meldingsutveksling.ad.dto.AdGroupResponse;
import no.difi.meldingsutveksling.ad.dto.AdUser;
import no.difi.meldingsutveksling.ad.dto.SecurityGroup;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.client.RestTemplate;

import javax.naming.ServiceUnavailableException;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
public class AdIntegration {

    private final AdIntegrationInput input;
    private final RestTemplate restTemplate;

    public AdIntegration(AdIntegrationInput input) {
        this.input = input;
        this.restTemplate = new RestTemplate(getClientHttpRequestFactory(input));
    }

    public AdUser authorizeUser(String uname, String password) {
        log.debug("authorizeUser ({},*****} called", uname);

        uname = patchUsername(uname);

        List<SecurityGroup> userGroups;
        String name;
        try {
            AuthenticationResult result = getAccessTokenFromUserCredentials(uname, password);
            userGroups = getSecurityGroups(result.getAccessToken());
            name = result.getUserInfo().getGivenName() + " " + result.getUserInfo().getFamilyName();
        } catch (Exception e) {
            throw new BadCredentialsException("Failed to authenticate user " + uname, e);
        }
        if (userGroups == null)
            return null;

        return new AdUser()
                .setAccess(userGroups)
                .setName(name);
    }

    private String patchUsername(String uname) {
        if (!uname.endsWith(input.getUsernamePrefix())) {
            uname = uname + input.getUsernamePrefix();
            log.debug("username patched to {}", uname);
        }
        return uname;
    }

    private List<SecurityGroup> getSecurityGroups(final String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);

        HttpEntity<AdGroupResponse> entity = new HttpEntity<>(headers);
        ResponseEntity<AdGroupResponse> exchange = restTemplate.exchange(input.getGraphUrl(), HttpMethod.GET, entity, AdGroupResponse.class);

        AdGroupResponse groups = exchange.getBody();
        return Optional.ofNullable(groups).map(AdGroupResponse::getValue).orElseGet(Collections::emptyList);
    }

    private AuthenticationResult getAccessTokenFromUserCredentials(String username, String password) throws MalformedURLException, ServiceUnavailableException, ExecutionException {
        AuthenticationContext context;
        AuthenticationResult result = null;
        ExecutorService service = null;
        try {
            service = Executors.newFixedThreadPool(1);
            context = new AuthenticationContext(input.getAuthUrl(), false, service);
            Future<AuthenticationResult> future = context.acquireToken(
                    input.getGraphResource(), input.getClientId(), username, password, null);
            result = future.get();
        } catch (InterruptedException e) {
            log.warn("Interrupted!", e);
            Thread.currentThread().interrupt();
        } finally {
            if (service != null)
                service.shutdown();
        }

        if (result == null) {
            throw new ServiceUnavailableException("authentication result was null");
        }
        return result;
    }

    private ClientHttpRequestFactory getClientHttpRequestFactory(AdIntegrationInput input) {
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        clientHttpRequestFactory.setConnectTimeout(input.getConnectTimeout());

        // todo this no longer has any effect and is depricated
        // clientHttpRequestFactory.setReadTimeout(input.getReadTimeout());
        input.httpClient().ifPresent(clientHttpRequestFactory::setHttpClient);
        return clientHttpRequestFactory;
    }
}
