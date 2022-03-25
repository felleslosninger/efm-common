package no.difi.move.common.io;

import lombok.experimental.UtilityClass;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;

@UtilityClass
@SuppressWarnings("unused")
public class ResourceUtils {

    public byte[] toByteArray(Resource resource) {
        try (InputStream inputStream = resource.getInputStream()) {
            return StreamUtils.copyToByteArray(inputStream);
        } catch (IOException e) {
            throw new IllegalStateException(String.format("Couldn't create byte[] out of resource: %s", resource), e);
        }
    }

    public String toString(Resource resource, Charset charset) {
        try (InputStream inputStream = resource.getInputStream()) {
            return StreamUtils.copyToString(inputStream, charset);
        } catch (IOException e) {
            throw new IllegalStateException(String.format("Couldn't create String out of resource: %s", resource), e);
        }
    }

    public void deleteFileIfItExists(Resource resource) {
        if (resource.isFile() && resource.exists()) {
            try {
                Files.delete(resource.getFile().toPath());
            } catch (IOException e) {
                throw new IllegalStateException(String.format("Could not delete file: %s", resource), e);
            }
        }
    }

    public int copy(Resource resource, OutputStream outputStream) {
        try (InputStream inputStream = resource.getInputStream()) {
            return StreamUtils.copy(inputStream, outputStream);
        } catch (IOException e) {
            throw new IllegalStateException(String.format("Could not copy %s", resource), e);
        }
    }

    public int copy(InputStream inputStream, WritableResource writableResource) {
        try (OutputStream outputStream = writableResource.getOutputStream()) {
            return StreamUtils.copy(inputStream, outputStream);
        } catch (IOException e) {
            throw new IllegalStateException(String.format("Could not copy to %s", writableResource), e);
        }
    }

    public int copy(Resource resource, WritableResource writableResource) {
        try (InputStream inputStream = resource.getInputStream()) {
            return copy(inputStream, writableResource);
        } catch (IOException e) {
            throw new IllegalStateException(String.format("Could not copy %s", resource), e);
        }
    }
}
