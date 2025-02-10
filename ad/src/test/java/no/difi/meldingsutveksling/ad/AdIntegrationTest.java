package no.difi.meldingsutveksling.ad;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.difi.meldingsutveksling.ad.dto.AdUser;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.TrustSelfSignedStrategy;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.ssl.SSLContexts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.StreamUtils;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@Slf4j
@WireMockTest(httpsEnabled = true)
@ExtendWith(SpringExtension.class)
class AdIntegrationTest {

    static {
        //for localhost testing only
        javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier((hostname, sslSession) -> hostname.equals("localhost"));
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }

                    public void checkClientTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
        };

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("Failed to install trust manager", e);
        }
    }

    @Test
    void test(WireMockRuntimeInfo runtimeInfo) {
        AdIntegration adIntegration = getAdIntegration(runtimeInfo);

        stubFor(get(urlMatching("/common/userrealm/.+"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json; charset=UTF-8")
                        .withBody(new String(getBody("/common/userrealm/all.json"))
                                .replaceAll("localhost:\\?", "localhost:" + runtimeInfo.getHttpsPort()))));


        stubFor(post(urlEqualTo("/common/oauth2/token"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json; charset=UTF-8")
                        .withBody(getBody("/common/oauth2/token.json"))));

        stubFor(get(urlEqualTo("/v1.0/me/memberOf"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json; charset=UTF-8")
                        .withBody(getBody("/v1.0/me/memberOf.json"))));

        stubFor(get(urlEqualTo("/adfs/services/trust/mex")).willReturn(
                aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml; charset=UTF-8")
                        .withBody(getBody("/adfs/services/trust/mex.xml"))
        ));

        stubFor(get("/adfs/services/trust/2005/usernamemixed").willReturn(
                aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml; charset=UTF-8")
                        .withBody(getBody("/adfs/services/trust/2005/usernamemixed.xml"))));

        stubFor(get(urlEqualTo("/adfs/services/trust/mex")).willReturn(
                aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml; charset=UTF-8")
                        .withBody(new String(getBody("/adfs/services/trust/mex2.xml"))
                                .replaceAll("localhost:\\?", "localhost:" + runtimeInfo.getHttpsPort()))));

        stubFor(post(urlEqualTo("/adfs/services/trust/13/usernamemixed")).willReturn(
                aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml; charset=UTF-8")
                        .withBody(getBody("/adfs/services/trust/13/usernamemixed.xml"))));

        AdUser response = adIntegration.authorizeUser("kons-gih@difi.no", "passwd");
        assertThat(response).isNotNull();
        assertThat(response.getAccess()).hasSize(4);
    }

    @SneakyThrows
    private byte[] getBody(String name) {
        return StreamUtils.copyToByteArray(getClass().getResourceAsStream("/mock" + name));
    }

    private AdIntegration getAdIntegration(WireMockRuntimeInfo runtimeInfo) {
        return new AdIntegration(new AdIntegrationInput() {
            @Override
            public int getConnectTimeout() {
                return 10000;
            }

            @Override
            public int getReadTimeout() {
                return 5000;
            }

            @Override
            public String getAuthUrl() {
                return "https://localhost:" + runtimeInfo.getHttpsPort() + "/common/";
            }

            @Override
            public String getGraphUrl() {
                return "https://localhost:" + runtimeInfo.getHttpsPort() + "/v1.0/me/memberOf";
            }

            @Override
            public String getClientId() {
                return "abda573c-1b59-4811-aa7e-0286bad97508";
            }

            @Override
            public String getGraphResource() {
                return "https://graph.microsoft.com";
            }

            @Override
            public String getUsernamePrefix() {
                return "@difi.no";
            }

            @Override
            @SneakyThrows
            public Optional<HttpClient> httpClient() {
                SSLContext context = SSLContexts.custom()
                        .loadTrustMaterial(TrustSelfSignedStrategy.INSTANCE)
                        .build();

                Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                        .register("http", PlainConnectionSocketFactory.INSTANCE)
                        .register("https", new SSLConnectionSocketFactory(context, NoopHostnameVerifier.INSTANCE))
                        .build();

                PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(registry);

                return Optional.of(HttpClients.custom()
                        .setConnectionManager(connectionManager)
                        .build());
            }
        });
    }
}
