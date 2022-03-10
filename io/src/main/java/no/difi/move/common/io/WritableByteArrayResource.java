package no.difi.move.common.io;

import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.WritableResource;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.FastByteArrayOutputStream;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

@SuppressWarnings("unused")
public class WritableByteArrayResource extends AbstractResource implements WritableResource {

    private final FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();
    private final String description;

    public WritableByteArrayResource() {
        this(null);
    }

    public WritableByteArrayResource(@Nullable String description) {
        this.description = (description != null ? description : "");
    }

    @Override
    public boolean exists() {
        return true;
    }

    @Override
    public long contentLength() {
        return outputStream.size();
    }

    @NonNull
    @Override
    public InputStream getInputStream() {
        return outputStream.getInputStream();
    }

    @NonNull
    @Override
    public String getDescription() {
        return "Writable byte array resource [" + this.description + "]";
    }

    @NonNull
    @Override
    public boolean equals(@Nullable Object other) {
        return (this == other || (other instanceof WritableByteArrayResource &&
                Objects.equals(((WritableByteArrayResource) other).outputStream, this.outputStream)));
    }

    @Override
    public int hashCode() {
        return outputStream.hashCode();
    }

    @NonNull
    @Override
    public OutputStream getOutputStream() {
        outputStream.reset();
        return outputStream;
    }

    @NonNull
    public byte[] toByteArray() {
        return outputStream.toByteArray();
    }
}
