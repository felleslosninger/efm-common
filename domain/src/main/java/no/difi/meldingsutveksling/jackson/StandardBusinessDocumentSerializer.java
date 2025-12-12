package no.difi.meldingsutveksling.jackson;

import no.difi.meldingsutveksling.domain.sbdh.StandardBusinessDocument;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

public class StandardBusinessDocumentSerializer extends ValueSerializer<StandardBusinessDocument> {

    @Override
    public Class<StandardBusinessDocument> handledType() {
        return StandardBusinessDocument.class;
    }

    @Override
    public void serialize(StandardBusinessDocument value, JsonGenerator gen, SerializationContext ctxt) throws JacksonException {
        gen.writeStartObject();
        gen.writePOJOProperty("standardBusinessDocumentHeader", value.getStandardBusinessDocumentHeader());
        gen.writePOJOProperty(value.getType(), value.getAny());
        gen.writeEndObject();
    }
}
