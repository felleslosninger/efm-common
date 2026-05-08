package no.difi.move.common.io;

import no.difi.move.common.io.pipe.Pipe;
import no.difi.move.common.io.pipe.Plumber;
import no.difi.move.common.io.pipe.Reject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import reactor.core.publisher.Flux;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ResourceUtilsTest {

    @Test
    void toByteArray_shouldReturnByteArray() throws IOException {
        Resource resource = mock(Resource.class);
        byte[] content = "test data".getBytes();
        when(resource.getInputStream()).thenReturn(new ByteArrayInputStream(content));

        byte[] result = ResourceUtils.toByteArray(resource);

        assertThat(result).isEqualTo(content);
    }

    @Test
    void toByteArray_shouldThrowIllegalStateExceptionOnIOException() throws IOException {
        Resource resource = mock(Resource.class);
        when(resource.getInputStream()).thenThrow(new IOException("test error"));

        assertThatThrownBy(() -> ResourceUtils.toByteArray(resource))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Couldn't create byte[] out of resource");
    }

    @Test
    void toString_shouldReturnString() throws IOException {
        Resource resource = mock(Resource.class);
        String content = "test string";
        when(resource.getInputStream()).thenReturn(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));

        String result = ResourceUtils.toString(resource, StandardCharsets.UTF_8);

        assertThat(result).isEqualTo(content);
    }

    @Test
    void toString_shouldThrowIllegalStateExceptionOnIOException() throws IOException {
        Resource resource = mock(Resource.class);
        when(resource.getInputStream()).thenThrow(new IOException("test error"));

        assertThatThrownBy(() -> ResourceUtils.toString(resource, StandardCharsets.UTF_8))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Couldn't create String out of resource");
    }

    @Test
    void deleteFileIfItExists_shouldDeleteFile(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("test.txt");
        Files.writeString(file, "content");
        Resource resource = mock(Resource.class);
        when(resource.isFile()).thenReturn(true);
        when(resource.exists()).thenReturn(true);
        when(resource.getFile()).thenReturn(file.toFile());

        ResourceUtils.deleteFileIfItExists(resource);

        assertThat(file).doesNotExist();
    }

    @Test
    void deleteFileIfItExists_shouldDoNothingIfResourceNotFile() throws IOException {
        Resource resource = mock(Resource.class);
        when(resource.isFile()).thenReturn(false);

        ResourceUtils.deleteFileIfItExists(resource);

        verify(resource, never()).getFile();
    }

    @Test
    void copy_ResourceToOutputStream_shouldCopyContent() throws IOException {
        Resource resource = mock(Resource.class);
        byte[] content = "copy this".getBytes();
        when(resource.getInputStream()).thenReturn(new ByteArrayInputStream(content));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        int bytesCopied = ResourceUtils.copy(resource, outputStream);

        assertThat(bytesCopied).isEqualTo(content.length);
        assertThat(outputStream.toByteArray()).isEqualTo(content);
    }

    @Test
    void copy_InputStreamToWritableResource_shouldCopyContent() throws IOException {
        byte[] content = "copy that".getBytes();
        InputStream inputStream = new ByteArrayInputStream(content);
        WritableResource writableResource = mock(WritableResource.class);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(writableResource.getOutputStream()).thenReturn(outputStream);

        int bytesCopied = ResourceUtils.copy(inputStream, writableResource);

        assertThat(bytesCopied).isEqualTo(content.length);
        assertThat(outputStream.toByteArray()).isEqualTo(content);
    }

    @Test
    void copy_ResourceToWritableResource_shouldCopyContent() throws IOException {
        Resource resource = mock(Resource.class);
        byte[] content = "copy everything".getBytes();
        when(resource.getInputStream()).thenReturn(new ByteArrayInputStream(content));
        WritableResource writableResource = mock(WritableResource.class);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(writableResource.getOutputStream()).thenReturn(outputStream);

        int bytesCopied = ResourceUtils.copy(resource, writableResource);

        assertThat(bytesCopied).isEqualTo(content.length);
        assertThat(outputStream.toByteArray()).isEqualTo(content);
    }

    @Test
    void copy_FluxToWritableResource_shouldCopyContent() throws IOException {
        byte[] content = "flux data".getBytes();
        DataBuffer dataBuffer = new DefaultDataBufferFactory().wrap(content);
        Flux<DataBuffer> flux = Flux.just(dataBuffer);
        WritableResource writableResource = mock(WritableResource.class);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(writableResource.getOutputStream()).thenReturn(outputStream);

        ResourceUtils.copy(flux, writableResource);

        assertThat(outputStream.toByteArray()).isEqualTo(content);
    }

    @Test
    void copy_FluxToWritableResource_withLargePayload_shouldCopyContent() throws IOException {
        int size = 1024 * 1024; // 1 MB
        byte[] content = new byte[size];
        for (int i = 0; i < size; i++) {
            content[i] = (byte) (i % 256);
        }

        DefaultDataBufferFactory factory = new DefaultDataBufferFactory();
        int bufferSize = 8192;
        Flux<DataBuffer> flux = Flux.range(0, (size + bufferSize - 1) / bufferSize)
            .map(i -> {
                int start = i * bufferSize;
                int length = Math.min(bufferSize, size - start);
                byte[] chunk = new byte[length];
                System.arraycopy(content, start, chunk, 0, length);
                return factory.wrap(chunk);
            });

        WritableResource writableResource = mock(WritableResource.class);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(writableResource.getOutputStream()).thenReturn(outputStream);

        ResourceUtils.copy(flux, writableResource);

        assertThat(outputStream.toByteArray()).isEqualTo(content);
    }

    @Test
    void createPipedResource_shouldReturnPipeResource() {
        Flux<DataBuffer> flux = Flux.empty();
        Plumber plumber = mock(Plumber.class);
        Reject reject = mock(Reject.class);
        Pipe pipe = mock(Pipe.class);
        when(plumber.pipe(anyString(), any(), any())).thenReturn(pipe);

        Resource result = ResourceUtils.createPipedResource(flux, plumber, reject);

        assertThat(result).isNotNull();
        verify(plumber).pipe(eq("Converting Flux<DataBuffer> to InputStream"), any(), eq(reject));
    }
}
