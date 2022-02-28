package no.difi.meldingsutveksling.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import no.difi.meldingsutveksling.domain.sbdh.StandardBusinessDocument;
import no.difi.meldingsutveksling.domain.sbdh.StandardBusinessDocumentHeader;

import java.io.IOException;
import java.util.Optional;

@SuppressWarnings("unused")
public abstract class StandardBusinessDocumentDeserializer extends JsonDeserializer<StandardBusinessDocument> {

    @Override
    public Class<?> handledType() {
        return StandardBusinessDocument.class;
    }

    abstract Optional<StandardBusinessDocumentType> getStandardBusinessDocumentType(String typeName);

    @Override
    public StandardBusinessDocument deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        StandardBusinessDocumentHeader header = readObject(p, "standardBusinessDocumentHeader", StandardBusinessDocumentHeader.class);
        StandardBusinessDocumentType type = header.getType()
                .flatMap(this::getStandardBusinessDocumentType)
                .orElseThrow(() -> new IOException("Missing type!"));
        StandardBusinessDocument standardBusinessDocument = new StandardBusinessDocument()
                .setStandardBusinessDocumentHeader(header)
                .setAny(readObject(p, type.getFieldName(), type.getValueType()));
        assertToken(p, JsonToken.END_OBJECT);
        return standardBusinessDocument;
    }

    private <T> T readObject(JsonParser p, String fieldName, Class<T> valueType) throws IOException {
        assertFieldName(p, fieldName);
        assertToken(p, JsonToken.START_OBJECT);
        return p.readValueAs(valueType);
    }

    private void assertFieldName(JsonParser parser, String expected) throws IOException {
        assertToken(parser, JsonToken.FIELD_NAME);
        if (!parser.getCurrentName().equals(expected)) {
            throw new IllegalArgumentException(String.format("Expected to find field named %s, but found %s", expected, parser.getCurrentName()));
        }
    }

    private void assertToken(JsonParser parser, JsonToken expected) throws IOException {
        JsonToken token = parser.nextToken();
        if (token != expected) {
            throw new IllegalArgumentException(String.format("Expected token %s, but found %s", expected, token));
        }
    }
}
