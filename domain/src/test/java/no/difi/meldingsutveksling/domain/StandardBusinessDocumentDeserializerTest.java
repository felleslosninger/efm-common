package no.difi.meldingsutveksling.domain;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.Getter;
import lombok.SneakyThrows;
import no.difi.meldingsutveksling.domain.sbdh.StandardBusinessDocument;
import no.difi.meldingsutveksling.jackson.StandardBusinessDocumentDeserializer;
import no.difi.meldingsutveksling.jackson.StandardBusinessDocumentSerializer;
import no.difi.meldingsutveksling.jackson.StandardBusinessDocumentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;


class StandardBusinessDocumentDeserializerTest {

    private final TestDeserializer deserializer = new TestDeserializer();

    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {

        SimpleModule module = new SimpleModule();
        module.addDeserializer(StandardBusinessDocument.class, deserializer);
        module.addSerializer(StandardBusinessDocument.class, new StandardBusinessDocumentSerializer());
        mapper = new ObjectMapper();
        mapper.registerModule(module);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);


    }

    @Getter
    enum TestDocumentType implements StandardBusinessDocumentType {

        BUSINESS_MESSAGE("businessMessage",DummyMessage.class),
         ENCRYPTED_BUSINESS_MESSAGE("businessMessageWithEncryption",EncryptedBusinessMessage.class){
            @Override
            public boolean supportsEncryption() {
                 return true;
             }
         };


        private final String fieldName;
        private final Class<?> valueType;

         TestDocumentType(String fieldName, Class<?> valueType) {
             this.fieldName = fieldName;
             this.valueType = valueType;
         }

     }

    @Getter
    private static class TestDeserializer extends StandardBusinessDocumentDeserializer {
        private StandardBusinessDocumentType lastRequestedType;

        @Override
        protected StandardBusinessDocumentType getStandardBusinessDocumentType(String typeName) {
            lastRequestedType = Arrays.stream(TestDocumentType.values()).filter(testDocumentType -> testDocumentType.getFieldName().equals(typeName)).findFirst().orElseThrow(IllegalStateException::new);
            return lastRequestedType;
        }
    }

    @Test
    @SneakyThrows
    void whenSbdIsValid_then_deserialize() {
        var testSbd = """
            {
              "standardBusinessDocumentHeader": {
                "documentIdentification": {
                    "type": "businessMessage"
                }
              },
              "businessMessage": { "value": "hello" }
            }
            """;

        var deserialisedSbd = mapper.readValue(testSbd,StandardBusinessDocument.class);
        assertNotNull(deserialisedSbd);
        assertEquals(StandardBusinessDocument.class, deserialisedSbd.getClass());
        assertEquals(deserialisedSbd.getType(),deserializer.lastRequestedType.getFieldName());
        assertNotNull(deserialisedSbd.getBusinessMessage(DummyMessage.class));
    }

    @Test
    @SneakyThrows
    void whenOrderOfElementsIsReversedButValid_thenDeserialize() {
        var testSbd = """
            {
              "businessMessage": { "value": "hello" },
              "standardBusinessDocumentHeader": {
                "documentIdentification": {
                    "type": "businessMessage"
                }
              }
            }
            """;

        var deserialisedSbd = mapper.readValue(testSbd,StandardBusinessDocument.class);
        assertNotNull(deserialisedSbd);
        assertEquals(StandardBusinessDocument.class, deserialisedSbd.getClass());
        assertEquals(deserialisedSbd.getType(),deserializer.lastRequestedType.getFieldName());
        assertNotNull(deserialisedSbd.getBusinessMessage(DummyMessage.class));
    }


    @Test
    @SneakyThrows
    void whenSbdIsEncrypted_then_deserializedAsEncryptedMessage() {
        var testSbd = """
            {
              "standardBusinessDocumentHeader": {
                "documentIdentification": {
                    "type": "businessMessageWithEncryption"
                }
              },
              "encryptedMessage": {
                "base64DerEncryptionCertificate":"this is encryption certificate",
                "message": "This is encrypted message" }
            }
            """;

        var deserialisedSbd = mapper.readValue(testSbd,StandardBusinessDocument.class);
        assertNotNull(deserialisedSbd);
        assertEquals(StandardBusinessDocument.class, deserialisedSbd.getClass());
        assertEquals(deserialisedSbd.getType(),deserializer.lastRequestedType.getFieldName());
        assertNotNull(deserialisedSbd.getBusinessMessage(EncryptedBusinessMessage.class));
    }

    @Test
    @SneakyThrows
    void whenSbdIsEncrypted_and_DocumentTypeDoesNotSupportEncryption_then_IllegalArgumentException() {
        var testSbd = """
    {
      "standardBusinessDocumentHeader": {
        "documentIdentification": {
          "type": "businessMessage"
        }
      },
      "encryptedMessage": {
        "base64DerEncryptionCertificate":"this is encryption certificate",
        "message": "This is encrypted message"
      }
    }
    """;

        var exception = assertThrows(IllegalArgumentException.class, () -> mapper.readValue(testSbd,StandardBusinessDocument.class));
        assertEquals("Document type does not support business message encryption.", exception.getMessage());

    }

    @Test
    @SneakyThrows
    void whenSbdIsEncrypted_and_DocumentTypeDoesNotSupportEncryption_Reversed_then_IllegalArgumentException() {
        var testSbd = """
    {
      "encryptedMessage": {
        "base64DerEncryptionCertificate":"this is encryption certificate",
        "message": "This is encrypted message"
      },
      "standardBusinessDocumentHeader": {
        "documentIdentification": {
          "type": "businessMessage"
        }
      }
    }
    """;

        var exception = assertThrows(IllegalArgumentException.class, () -> mapper.readValue(testSbd,StandardBusinessDocument.class));
        assertEquals("Document type does not support business message encryption.", exception.getMessage());

    }



    static class DummyMessage {
        public String value;
    }

}
