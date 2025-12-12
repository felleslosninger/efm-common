package no.difi.meldingsutveksling.jackson;

import no.difi.meldingsutveksling.domain.sbdh.StandardBusinessDocument;
import tools.jackson.core.json.PackageVersion;
import tools.jackson.databind.module.SimpleModule;

import java.util.function.Function;

@SuppressWarnings("unused")
public class StandardBusinessDocumentModule extends SimpleModule {

    public StandardBusinessDocumentModule(Function<String, StandardBusinessDocumentType> typeMapper) {
        super(StandardBusinessDocumentModule.class.getSimpleName(), PackageVersion.VERSION);

        this.addSerializer(StandardBusinessDocument.class, new StandardBusinessDocumentSerializer());
        this.addDeserializer(StandardBusinessDocument.class, new StandardBusinessDocumentDeserializer() {
            @Override
            StandardBusinessDocumentType getStandardBusinessDocumentType(String typeName) {
                return typeMapper.apply(typeName);
            }
        });
    }
}
