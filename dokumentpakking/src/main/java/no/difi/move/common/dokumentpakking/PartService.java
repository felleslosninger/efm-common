package no.difi.move.common.dokumentpakking;

import lombok.RequiredArgsConstructor;
import no.difi.move.common.io.ResourceUtils;
import no.difi.move.common.io.pipe.Plumber;
import no.difi.move.common.io.pipe.PromiseMaker;
import org.springframework.core.io.WritableResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.FormFieldPart;
import org.springframework.http.codec.multipart.Part;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

@SuppressWarnings("unused")
@RequiredArgsConstructor
public class PartService {

    private final Plumber plumber;
    private final PromiseMaker promiseMaker;

    public void copy(Flux<DataBuffer> flux, WritableResource writableResource) {
        try (OutputStream os = writableResource.getOutputStream()) {
            flux.map(dataBuffer -> DataBufferUtils.write(flux, os))
                .blockLast();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void copy2(Flux<DataBuffer> flux, WritableResource writableResource) {
        promiseMaker.promise(reject -> {
            ResourceUtils.copy(ResourceUtils.createPipedResource(flux, plumber, reject), writableResource);
            return null;
        }).await();
    }

    public Mono<String> toString(Part part) {
        if (part instanceof FormFieldPart formField) {
            return Mono.just(formField.value());
        } else if (part instanceof FilePart filePart) {
            return DataBufferUtils.join(filePart.content())
                .map(dataBuffer -> {
                    try {
                        byte[] bytes = new byte[dataBuffer.readableByteCount()];
                        dataBuffer.read(bytes);
                        return new String(bytes, StandardCharsets.UTF_8);
                    } finally {
                        DataBufferUtils.release(dataBuffer);
                    }
                });
        } else {
            return Mono.error(new IllegalArgumentException("Unhandled part type = '%s".formatted(part.getClass().getName())));
        }
    }
}
