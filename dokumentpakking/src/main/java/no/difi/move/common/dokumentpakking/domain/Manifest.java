package no.difi.move.common.dokumentpakking.domain;


import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.util.MimeType;

@Value
@Builder
public class Manifest implements AsicEAttachable {

    @NonNull Resource resource;
    @Builder.Default
    @NonNull MimeType mimeType = MediaType.APPLICATION_XML;

    @Override
    public String getFilename() {
        return "manifest.xml";
    }

    public static class ManifestBuilder {
        private MimeType mimeType;

        public ManifestBuilder mimeType(MimeType mimeType) {
            this.mimeType = mimeType;
            return this;
        }

        public ManifestBuilder mimeType(String mimeType) {
            this.mimeType = mimeType != null ? MimeType.valueOf(mimeType) : null;
            return this;
        }
    }
}
