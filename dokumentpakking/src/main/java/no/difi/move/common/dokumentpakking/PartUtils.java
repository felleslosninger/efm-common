package no.difi.move.common.dokumentpakking;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.FormFieldPart;
import org.springframework.http.codec.multipart.Part;
import reactor.core.publisher.Flux;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

@SuppressWarnings("unused")
@RequiredArgsConstructor
public class PartUtils {

    public static String toString(Part part) {
        if (part instanceof FormFieldPart formField) {
            return formField.value();
        } else if (part instanceof FilePart filePart) {
            Flux<DataBuffer> flux = part.content();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            DataBufferUtils.write(flux, outputStream)
                .subscribe(DataBufferUtils.releaseConsumer());
            return outputStream.toString(StandardCharsets.UTF_8);
        } else {
            throw new IllegalArgumentException("Unhandled part type = '%s".formatted(part.getClass().getName()));
        }
    }
}
