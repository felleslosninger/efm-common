package no.difi.meldingsutveksling.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import no.difi.meldingsutveksling.domain.sbdh.StandardBusinessDocument;
import no.difi.meldingsutveksling.domain.sbdh.StandardBusinessDocumentHeader;

import java.io.IOException;

@SuppressWarnings("unused")
public abstract class StandardBusinessDocumentDeserializer extends JsonDeserializer<StandardBusinessDocument> {

    @Override
    public Class<?> handledType() {
        return StandardBusinessDocument.class;
    }

    abstract StandardBusinessDocumentType getStandardBusinessDocumentType(String typeName);

    @Override
    public StandardBusinessDocument deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        StandardBusinessDocument sbd = new StandardBusinessDocument();
        if ("standardBusinessDocumentHeader".equals(p.nextFieldName())) {
            StandardBusinessDocumentHeader header = readObject(p, "standardBusinessDocumentHeader", StandardBusinessDocumentHeader.class);
            StandardBusinessDocumentType type = header.getType()
                    .map(this::getStandardBusinessDocumentType)
                    .orElseThrow(() -> new IOException("Missing type!"));
            sbd.setStandardBusinessDocumentHeader(header)
                    .setAny(readObject(p, type.getFieldName(), type.getValueType()));
        } else {
            StandardBusinessDocumentType type = getStandardBusinessDocumentType(p.currentName());
            Object businessMsg = readObject(p, type.getFieldName(), type.getValueType());
            StandardBusinessDocumentHeader header = readObject(p, "standardBusinessDocumentHeader", StandardBusinessDocumentHeader.class);
            sbd.setStandardBusinessDocumentHeader(header)
                    .setAny(businessMsg);
        }
        assertToken(p, JsonToken.END_OBJECT);
        return sbd;

    }

    private <T> T readObject(JsonParser p, String fieldName, Class<T> valueType) throws IOException {
        assertFieldName(p, fieldName);
        p.nextToken();
        assertToken(p, JsonToken.START_OBJECT);
        T value = p.readValueAs(valueType);
        p.nextToken();
        return value;
    }

    private void assertFieldName(JsonParser parser, String expected) throws IOException {
        assertToken(parser, JsonToken.FIELD_NAME);
        if (!parser.getCurrentName().equals(expected)) {
            throw new IllegalArgumentException(String.format("Expected to find field named %s, but found %s", expected, parser.getCurrentName()));
        }
    }

    private void assertToken(JsonParser parser, JsonToken expected) {
        JsonToken token = parser.currentToken();
        if (token != expected) {
            throw new IllegalArgumentException(String.format("Expected token %s, but found %s", expected, token));
        }
    }
}
