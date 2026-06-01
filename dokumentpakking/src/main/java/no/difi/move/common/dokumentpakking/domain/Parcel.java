package no.difi.move.common.dokumentpakking.domain;

import lombok.Builder;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Value
@Builder
@SuppressWarnings("unused")
public class Parcel {

    Document mainDocument;
    List<Document> attachments;

    public Stream<AsicEAttachable> getDocuments() {
        List<AsicEAttachable> files = new ArrayList<>();
        files.add(mainDocument);
        files.addAll(attachments);
        Optional.ofNullable(mainDocument)
            .flatMap(p -> Optional.ofNullable(p.getMetadataDocument()))
            .ifPresent(files::add);
        return files.stream();
    }
}
