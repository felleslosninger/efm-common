package no.difi.meldingsutveksling.domain;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.SneakyThrows;
import no.difi.meldingsutveksling.domain.sbdh.StandardBusinessDocument;
import no.difi.meldingsutveksling.jackson.StandardBusinessDocumentModule;
import no.difi.meldingsutveksling.jackson.StandardBusinessDocumentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class StandardBusinessDocumentDeserializerTest {

    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {

        mapper = new ObjectMapper();
        mapper.registerModule(new StandardBusinessDocumentModule(TestDocumentType::fromType));
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Getter
    enum TestDocumentType implements StandardBusinessDocumentType {

        BUSINESS_MESSAGE("businessMessage", DummyMessage.class);

        private final String fieldName;
        private final Class<?> valueType;

        TestDocumentType(String fieldName, Class<?> valueType) {
            this.fieldName = fieldName;
            this.valueType = valueType;
        }

        public static TestDocumentType fromType(String type) {
            return Arrays.stream(TestDocumentType.values()).filter(p -> p.getFieldName().equalsIgnoreCase(type))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Unknown TestDocumentType = %s. Expecting one of %s",
                    type,
                    Arrays.stream(values()).map(TestDocumentType::getFieldName).collect(Collectors.joining(",")))));
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

        var deserialisedSbd = mapper.readValue(testSbd, StandardBusinessDocument.class);
        assertNotNull(deserialisedSbd);
        assertEquals(StandardBusinessDocument.class, deserialisedSbd.getClass());
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

        var deserialisedSbd = mapper.readValue(testSbd, StandardBusinessDocument.class);
        assertNotNull(deserialisedSbd);
        assertEquals(StandardBusinessDocument.class, deserialisedSbd.getClass());
        assertNotNull(deserialisedSbd.getBusinessMessage(DummyMessage.class));
    }

    static class DummyMessage {
        public String value;
    }
}
