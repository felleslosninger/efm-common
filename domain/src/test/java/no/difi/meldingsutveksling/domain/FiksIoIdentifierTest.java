package no.difi.meldingsutveksling.domain;

import org.junit.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


public class FiksIoIdentifierTest {

    @Test
    public void getIdentifier() {
        assertThat(FiksIoIdentifier.parse("0232c524-cb9b-4e9e-916d-318a5696184e").getIdentifier())
                .isEqualTo("0232c524-cb9b-4e9e-916d-318a5696184e");
    }

    @Test
    public void getPrimaryIdentifier() {
        assertThat(FiksIoIdentifier.parse("0232c524-cb9b-4e9e-916d-318a5696184e").getPrimaryIdentifier())
                .isEqualTo("0232c524-cb9b-4e9e-916d-318a5696184e");
    }

    @Test
    public void of() {
        assertThat(FiksIoIdentifier.of(UUID.fromString("0232c524-cb9b-4e9e-916d-318a5696184e")))
                .extracting(FiksIoIdentifier::getIdentifier)
                .isEqualTo("0232c524-cb9b-4e9e-916d-318a5696184e");
    }

    @Test
    public void parse() {
        assertThat(FiksIoIdentifier.parse("0232c524-cb9b-4e9e-916d-318a5696184e"))
                .isEqualTo(FiksIoIdentifier.of(UUID.fromString("0232c524-cb9b-4e9e-916d-318a5696184e")));
    }

    @Test
    public void parseQualifiedIdentifier() {
        assertThat(FiksIoIdentifier.parseQualifiedIdentifier("iso6523-actorid-upis::0232c524-cb9b-4e9e-916d-318a5696184e"))
                .isEqualTo(FiksIoIdentifier.of(UUID.fromString("0232c524-cb9b-4e9e-916d-318a5696184e")));
    }

    @Test
    public void isValid() {
        assertThat(FiksIoIdentifier.isValid("0232c524-cb9b-4e9e-916d-318a5696184e"))
                .isTrue();
    }

    @Test
    public void isValidQualifiedIdentifier() {
        assertThat(FiksIoIdentifier.isValidQualifiedIdentifier("iso6523-actorid-upis::0232c524-cb9b-4e9e-916d-318a5696184e"))
                .isTrue();
    }

    @Test
    public void getUUID() {
        assertThat(FiksIoIdentifier.parse("0232c524-cb9b-4e9e-916d-318a5696184e").getUUID())
                .isEqualTo(UUID.fromString("0232c524-cb9b-4e9e-916d-318a5696184e"));
    }

    @Test
    public void testToString() {
        assertThat(FiksIoIdentifier.parse("0232c524-cb9b-4e9e-916d-318a5696184e"))
                .hasToString("0232c524-cb9b-4e9e-916d-318a5696184e");
    }
}