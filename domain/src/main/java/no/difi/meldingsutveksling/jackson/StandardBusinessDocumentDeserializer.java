package no.difi.meldingsutveksling.jackson;

import no.difi.meldingsutveksling.domain.sbdh.StandardBusinessDocument;
import no.difi.meldingsutveksling.domain.sbdh.StandardBusinessDocumentHeader;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

@SuppressWarnings("unused")
public abstract class StandardBusinessDocumentDeserializer extends ValueDeserializer<StandardBusinessDocument> {

    public static final String STANDARD_BUSINESS_DOCUMENT_HEADER = "standardBusinessDocumentHeader";

    @Override
    public Class<?> handledType() {
        return StandardBusinessDocument.class;
    }

    abstract StandardBusinessDocumentType getStandardBusinessDocumentType(String typeName);

    @Override
    public StandardBusinessDocument deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException {
        StandardBusinessDocument sbd = new StandardBusinessDocument();
        if (STANDARD_BUSINESS_DOCUMENT_HEADER.equals(p.nextName())) {
            StandardBusinessDocumentHeader header = readObject(p, STANDARD_BUSINESS_DOCUMENT_HEADER, StandardBusinessDocumentHeader.class);
            StandardBusinessDocumentType type = header.getType()
                .map(this::getStandardBusinessDocumentType)
                .orElseThrow(() -> DatabindException.from(p, "Missing type!"));
            sbd.setStandardBusinessDocumentHeader(header)
                .setAny(readObject(p, type.getFieldName(), type.getValueType()));
        } else {
            StandardBusinessDocumentType type = getStandardBusinessDocumentType(p.currentName());
            Object businessMsg = readObject(p, type.getFieldName(), type.getValueType());
            StandardBusinessDocumentHeader header = readObject(p, STANDARD_BUSINESS_DOCUMENT_HEADER, StandardBusinessDocumentHeader.class);
            sbd.setStandardBusinessDocumentHeader(header)
                .setAny(businessMsg);
        }
        assertToken(p, JsonToken.END_OBJECT);
        return sbd;

    }

    private <T> T readObject(JsonParser p, String fieldName, Class<T> valueType) {
        assertFieldName(p, fieldName);
        p.nextToken();
        assertToken(p, JsonToken.START_OBJECT);
        T value = p.readValueAs(valueType);
        p.nextToken();
        return value;
    }

    private void assertFieldName(JsonParser parser, String expected) {
        assertToken(parser, JsonToken.PROPERTY_NAME);
        if (!parser.currentName().equals(expected)) {
            throw new IllegalArgumentException(String.format("Expected to find field named %s, but found %s", expected, parser.currentName()));
        }
    }

    private void assertToken(JsonParser parser, JsonToken expected) {
        JsonToken token = parser.currentToken();
        if (token != expected) {
            throw new IllegalArgumentException(String.format("Expected token %s, but found %s", expected, token));
        }
    }
}
