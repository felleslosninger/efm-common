package no.difi.move.common.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.config.client.ConfigClientProperties;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ProtocolResolver;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.*;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Nikolai Luthman <nikolai dot luthman at inmeta dot no>
 */
public class SpringCloudProtocolResolver implements ApplicationContextInitializer<ConfigurableApplicationContext>, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(SpringCloudProtocolResolver.class);

    public static final String PREFIX = "cloud:";

    @Override
    public void initialize(final ConfigurableApplicationContext c) {
        logger.info("Initialize with Spring Cloud Config Binary resource resolver.");
        c.addProtocolResolver(new ProtocolResolver() {

            Map<String, ByteArrayResource> maps = new HashMap<>();

            @Override
            public Resource resolve(String location, ResourceLoader rl) {

                if (!location.startsWith(PREFIX)) {
                    return null;
                }

                if (!maps.containsKey(location)) {
                    fetchRemote(location);
                }

                return maps.get(location);
            }

            private void fetchRemote(String location) {
                ConfigClientProperties bean = c.getBean(ConfigClientProperties.class);
                String label = "master";
                if (StringUtils.hasText(bean.getLabel())) {
                    label = bean.getLabel();
                }
                Object[] args = new String[]{bean.getName(), label, location.substring(PREFIX.length())};
                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_OCTET_STREAM_VALUE);
                final HttpEntity<Void> entity = new HttpEntity<>(null, headers);

                String uri = bean.getUri()[0];

                logger.info("Fetching resource {} from cloud: {}/{}/default/{}/{}", location, uri, bean.getName(), label, location.substring(PREFIX.length()));

                try {
                    ResponseEntity<byte[]> exchange = new RestTemplate().exchange(uri + "/{name}/default/{label}/{resource}", HttpMethod.GET, entity, byte[].class, args);
                    maps.put(location, new ByteArrayResource(exchange.getBody()));
                } catch (RestClientException rce) {
                    logger.error("Error loading cloud resource: " + location, rce);
                }
            }
        });
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

}
