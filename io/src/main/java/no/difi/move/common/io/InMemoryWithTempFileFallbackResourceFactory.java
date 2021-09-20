package no.difi.move.common.io;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.apache.commons.io.output.DeferredFileOutputStream;

import java.io.File;

@Value
@Builder
@RequiredArgsConstructor
public class InMemoryWithTempFileFallbackResourceFactory {

    int threshold;
    int initialBufferSize;
    File directory;

    public InMemoryWithTempFileFallbackResource getResource(String prefix, String suffix) {
        return new InMemoryWithTempFileFallbackResource(new DeferredFileOutputStream(threshold, initialBufferSize, prefix, suffix, directory));
    }
}
