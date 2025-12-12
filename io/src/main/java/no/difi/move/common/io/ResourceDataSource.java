package no.difi.move.common.io;

import jakarta.activation.DataSource;
import lombok.Value;
import org.apache.commons.io.input.AutoCloseInputStream;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


@Value
@SuppressWarnings("unused")
public class ResourceDataSource implements DataSource {

    Resource resource;

    @Override
    public InputStream getInputStream() throws IOException {
        if (resource instanceof AutoCloseable) {
            return new AutoCloseInputStream(resource.getInputStream()) {
                @Override
                public void close() throws IOException {
                    super.close();
                    ResourceUtils.deleteFileIfItExists(resource);
                }
            };
        }

        return resource.getInputStream();
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        if (resource instanceof WritableResource writableResource) {
            return writableResource.getOutputStream();
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public String getContentType() {
        return "application/octet-stream";
    }

    @Override
    public String getName() {
        return "InputStreamDataSource";
    }


}
