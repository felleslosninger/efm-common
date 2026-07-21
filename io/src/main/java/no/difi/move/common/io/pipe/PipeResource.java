package no.difi.move.common.io.pipe;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.core.io.AbstractResource;

import java.io.InputStream;

@SuppressWarnings("unused")
public class PipeResource extends AbstractResource {

    private final Pipe pipe;
    private final String description;
    private boolean read = false;

    public PipeResource(Pipe pipe, @Nullable String description) {
        this.pipe = pipe;
        this.description = (description != null ? description : "");
    }

    @NonNull
    @Override
    public InputStream getInputStream() {
        if (this.read) {
            throw new IllegalStateException("InputStream has already been read - " +
                "do not use InputStreamResource if a stream needs to be read multiple times");
        }
        this.read = true;
        return pipe.outlet();
    }

    @NonNull
    @Override
    public String getDescription() {
        return "PipeResource resource [" + this.description + "]";
    }

    @Override
    public boolean equals(@Nullable Object other) {
        return (this == other || (other instanceof PipeResource resource &&
            (resource.pipe.equals(this.pipe))));
    }

    @Override
    public int hashCode() {
        return this.pipe.hashCode();
    }
}
