package no.difi.meldingsutveksling.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import no.difi.meldingsutveksling.domain.EncryptedBusinessMessage;
import no.difi.meldingsutveksling.domain.sbdh.StandardBusinessDocument;
import no.difi.meldingsutveksling.domain.sbdh.StandardBusinessDocumentHeader;

import java.io.IOException;

@SuppressWarnings("unused")
public abstract class StandardBusinessDocumentDeserializer extends JsonDeserializer<StandardBusinessDocument> {

    public static final String STANDARD_BUSINESS_DOCUMENT_HEADER = "standardBusinessDocumentHeader";
    public static final String ENCRYPTED_MESSAGE = "encryptedMessage";

    @Override
    public Class<?> handledType() {
        return StandardBusinessDocument.class;
    }

    protected abstract StandardBusinessDocumentType getStandardBusinessDocumentType(String typeName);

    @Override
    public StandardBusinessDocument deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        StandardBusinessDocument sbd = new StandardBusinessDocument();
        if (STANDARD_BUSINESS_DOCUMENT_HEADER.equals(p.nextFieldName())) {
            StandardBusinessDocumentHeader header = readObject(p, STANDARD_BUSINESS_DOCUMENT_HEADER, StandardBusinessDocumentHeader.class);
            StandardBusinessDocumentType type = header.getType()
                    .map(this::getStandardBusinessDocumentType)
                    .orElseThrow(() -> new IOException("Missing type!"));

            String nextField = p.currentName();

            if (ENCRYPTED_MESSAGE.equals(nextField) && !type.supportsEncryption()) {
                throw new IllegalArgumentException("Document type does not support business message encryption.");
            }

            String fieldName = ENCRYPTED_MESSAGE.equals(nextField) ? ENCRYPTED_MESSAGE : type.getFieldName();
            Class<?> valueType = ENCRYPTED_MESSAGE.equals(nextField) ? EncryptedBusinessMessage.class : type.getValueType();


            sbd.setStandardBusinessDocumentHeader(header)
                    .setAny( readObject(p, fieldName, valueType));
        }
        else {
            Object businessMsg;
            var messageField = p.currentName();
            StandardBusinessDocumentType typeFromField = null;

            if (ENCRYPTED_MESSAGE.equals(messageField)) {
                businessMsg = readObject(p, ENCRYPTED_MESSAGE, EncryptedBusinessMessage.class);
            }
            else{
                typeFromField = getStandardBusinessDocumentType(messageField);
                businessMsg = readObject(p, typeFromField.getFieldName(), typeFromField.getValueType());
            }

            StandardBusinessDocumentHeader header = readObject(p, STANDARD_BUSINESS_DOCUMENT_HEADER, StandardBusinessDocumentHeader.class);
            StandardBusinessDocumentType typeFromHeader = getStandardBusinessDocumentType(header.getType().orElseThrow(() -> new IllegalArgumentException("Missing type in standardBusinessDocumentHeader.")));
            if (ENCRYPTED_MESSAGE.equals(messageField) && !typeFromHeader.supportsEncryption()) {
                throw new IllegalArgumentException("Document type does not support business message encryption.");
            }

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

    private boolean isEncryptedMessage(JsonParser parser) throws IOException {
        assertToken(parser, JsonToken.FIELD_NAME);
        if (parser.getCurrentName().equals(ENCRYPTED_MESSAGE)) {
            return true;
        }
        return false;
    }

    private void assertFieldName(JsonParser parser, String expected) throws IOException {
        assertToken(parser, JsonToken.FIELD_NAME);
        var currentName = parser.getCurrentName();
        if (!currentName.equals(expected)) {
            if (currentName.equals(ENCRYPTED_MESSAGE)) {
                throw new IllegalArgumentException("Document type does not support business message encryption.");
            }
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
