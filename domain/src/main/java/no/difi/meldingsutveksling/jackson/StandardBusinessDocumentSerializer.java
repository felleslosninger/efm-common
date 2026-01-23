package no.difi.meldingsutveksling.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import no.difi.meldingsutveksling.domain.EncryptedBusinessMessage;
import no.difi.meldingsutveksling.domain.sbdh.StandardBusinessDocument;

import java.io.IOException;

public class StandardBusinessDocumentSerializer extends JsonSerializer<StandardBusinessDocument> {

    @Override
    public Class<StandardBusinessDocument> handledType() {
        return StandardBusinessDocument.class;
    }

    @Override
    public void serialize(StandardBusinessDocument value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeObjectField(StandardBusinessDocumentDeserializer.STANDARD_BUSINESS_DOCUMENT_HEADER, value.getStandardBusinessDocumentHeader());
        if (value.getAny() instanceof EncryptedBusinessMessage msg) {
            gen.writeObjectField(StandardBusinessDocumentDeserializer.ENCRYPTED_MESSAGE, msg);
        }
        else {
            gen.writeObjectField(value.getType(), value.getAny());
        }

        gen.writeEndObject();
    }
}
