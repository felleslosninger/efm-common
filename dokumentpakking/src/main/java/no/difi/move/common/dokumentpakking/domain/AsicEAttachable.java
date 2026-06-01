package no.difi.move.common.dokumentpakking.domain;

import org.springframework.core.io.Resource;
import org.springframework.util.MimeType;

public interface AsicEAttachable {
    String getFilename();

    Resource getResource();

    MimeType getMimeType();
}
