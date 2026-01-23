package no.difi.meldingsutveksling.jackson;

import com.fasterxml.jackson.core.json.PackageVersion;
import com.fasterxml.jackson.databind.module.SimpleModule;
import no.difi.meldingsutveksling.domain.sbdh.StandardBusinessDocument;

import java.util.function.Function;

public class StandardBusinessDocumentModule extends SimpleModule {

    public StandardBusinessDocumentModule(Function<String, StandardBusinessDocumentType> typeMapper) {
        super(StandardBusinessDocumentModule.class.getSimpleName(), PackageVersion.VERSION);

        this.addSerializer(StandardBusinessDocument.class, new StandardBusinessDocumentSerializer());
        this.addDeserializer(StandardBusinessDocument.class, new StandardBusinessDocumentDeserializer() {
            @Override
            protected StandardBusinessDocumentType getStandardBusinessDocumentType(String typeName) {
                return typeMapper.apply(typeName);
            }
        });
    }
}
