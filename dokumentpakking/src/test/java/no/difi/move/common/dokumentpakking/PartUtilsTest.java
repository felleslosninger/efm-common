package no.difi.move.common.dokumentpakking;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.FormFieldPart;
import org.springframework.http.codec.multipart.Part;
import reactor.core.publisher.Flux;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

class PartUtilsTest {

    @Test
    void toString_FormFieldPart_shouldReturnFieldValue() {
        FormFieldPart formFieldPart = Mockito.mock(FormFieldPart.class);
        when(formFieldPart.value()).thenReturn("test-value");

        String result = PartUtils.toString(formFieldPart);

        assertThat(result).isEqualTo("test-value");
    }

    @Test
    void toString_FilePart_shouldReturnContentAsString() {
        FilePart filePart = Mockito.mock(FilePart.class);
        String content = "file-content";
        DataBuffer dataBuffer = new DefaultDataBufferFactory().wrap(content.getBytes(StandardCharsets.UTF_8));
        when(filePart.content()).thenReturn(Flux.just(dataBuffer));

        String result = PartUtils.toString(filePart);

        assertThat(result).isEqualTo(content);
    }

    @Test
    void toString_UnhandledPartType_shouldThrowException() {
        Part unknownPart = Mockito.mock(Part.class);

        assertThatThrownBy(() -> PartUtils.toString(unknownPart))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Unhandled part type");
    }
}
