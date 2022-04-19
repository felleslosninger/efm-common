package no.difi.move.common.io;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.WritableResource;
import org.springframework.lang.NonNull;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

@SuppressWarnings("unused")
@RequiredArgsConstructor
public class OutputStreamResource extends AbstractResource implements WritableResource {

    private final OutputStream outputStream;

    @NonNull
    @Override
    public OutputStream getOutputStream() {
        return outputStream;
    }

    @NonNull
    @Override
    public String getDescription() {
        return "OutputStreamResource";
    }

    @NonNull
    @Override
    public InputStream getInputStream() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OutputStreamResource)) return false;
        if (!super.equals(o)) return false;
        OutputStreamResource that = (OutputStreamResource) o;
        return outputStream.equals(that.outputStream);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), outputStream);
    }
}
