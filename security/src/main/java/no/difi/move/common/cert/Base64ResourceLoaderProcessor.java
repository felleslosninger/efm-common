package no.difi.move.common.cert;

import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.*;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class Base64ResourceLoaderProcessor implements ResourceLoaderAware, ProtocolResolver {

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        if (resourceLoader instanceof DefaultResourceLoader) {
            DefaultResourceLoader defaultResourceLoader = (DefaultResourceLoader) resourceLoader;
            defaultResourceLoader.addProtocolResolver(this);
        }
    }

    @Override
    public Resource resolve(String location, ResourceLoader resourceLoader) {
        if (location.startsWith("base64:")) {
            String base64String = location.substring(location.indexOf(':') + 1);
            byte[] base64Bytes = Base64.getDecoder().decode(base64String);
            return new ByteArrayResource(base64Bytes);
        }
        return null;
    }

}
