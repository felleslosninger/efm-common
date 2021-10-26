package no.difi.move.common.io;

import lombok.experimental.UtilityClass;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;

@UtilityClass
public class ResourceUtils {

    public byte[] toByteArray(Resource resource) {
        try (InputStream inputStream = resource.getInputStream()) {
            return IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            throw new Exception("Couldn't create byte[] out of resource!", e);
        }
    }

    private static class Exception extends RuntimeException {
        public Exception(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
