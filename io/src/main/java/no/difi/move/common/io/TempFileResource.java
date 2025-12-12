package no.difi.move.common.io;

import lombok.SneakyThrows;
import org.jspecify.annotations.NonNull;
import org.springframework.core.io.FileSystemResource;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;

@SuppressWarnings("unused")
public class TempFileResource extends FileSystemResource {

    public TempFileResource(String prefix) throws IOException {
        this(prefix, null, null);
    }

    public TempFileResource(String prefix, String suffix) throws IOException {
        this(prefix, suffix, null);
    }

    public TempFileResource(String prefix, String suffix,
                            File directory) throws IOException {
        super(File.createTempFile(prefix, suffix, directory));
    }

    @NonNull
    @Override
    public ReadableByteChannel readableChannel() throws IOException {
        try (final ReadableByteChannel readableChannel = super.readableChannel()) {
            return new ReadableByteChannel() {
                public boolean isOpen() {
                    return readableChannel.isOpen();
                }

                public void close() throws IOException {
                    TempFileResource.this.closeThenDeleteFile(readableChannel);
                }

                public int read(ByteBuffer dst) throws IOException {
                    return readableChannel.read(dst);
                }
            };
        }
    }

    @NonNull
    @Override
    public InputStream getInputStream() throws IOException {
        return new FilterInputStream(super.getInputStream()) {
            @Override
            public void close() throws IOException {
                TempFileResource.this.closeThenDeleteFile(this.in);
            }
        };
    }

    private void closeThenDeleteFile(Closeable closeable) throws IOException {
        try {
            closeable.close();
        } finally {
            this.deleteFile();
        }

    }

    @SneakyThrows
    private void deleteFile() {
        Files.delete(this.getFile().toPath());
    }
}
