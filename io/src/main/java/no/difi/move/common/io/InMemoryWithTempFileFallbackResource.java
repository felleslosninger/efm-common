package no.difi.move.common.io;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.output.DeferredFileOutputStream;
import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

@RequiredArgsConstructor
@Slf4j
public class InMemoryWithTempFileFallbackResource extends AbstractResource implements WritableResource, AutoCloseable, Resource {

    private final DeferredFileOutputStream deferredFileOutputStream;

    @NonNull
    @Override
    public OutputStream getOutputStream() {
        return deferredFileOutputStream;
    }

    @NonNull
    @Override
    public File getFile() throws IOException {
        return deferredFileOutputStream.isInMemory() ? super.getFile() : deferredFileOutputStream.getFile();
    }

    @NonNull
    @Override
    public String getDescription() {
        if (deferredFileOutputStream.isInMemory()) {
            return "byte [" + deferredFileOutputStream.getByteCount() + "]";
        }

        return "file [" + deferredFileOutputStream.getFile().getAbsolutePath() + "]";
    }

    @NonNull
    @Override
    public InputStream getInputStream() throws IOException {
        if (deferredFileOutputStream.isInMemory()) {
            return new ByteArrayInputStream(deferredFileOutputStream.getData());
        }

        return Files.newInputStream(deferredFileOutputStream.getFile().toPath(), StandardOpenOption.READ);
    }

    @Override
    public boolean isFile() {
        return !deferredFileOutputStream.isInMemory();
    }

    @Override
    public long contentLength() {
        return deferredFileOutputStream.getByteCount();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InMemoryWithTempFileFallbackResource)) return false;
        if (!super.equals(o)) return false;
        InMemoryWithTempFileFallbackResource that = (InMemoryWithTempFileFallbackResource) o;
        return deferredFileOutputStream.equals(that.deferredFileOutputStream);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), deferredFileOutputStream);
    }

    @Override
    public void close() {
        try {
            deferredFileOutputStream.close();
        } catch (IOException e) {
            log.debug("Could not close deferred file output stream", e);
        }

        try {
            ResourceUtils.deleteFileIfItExists(this);
        } catch (IllegalStateException e) {
            log.warn(String.format("Unable to delete file with name=%s", deferredFileOutputStream.getFile().getName()), e);
        }
    }
}
