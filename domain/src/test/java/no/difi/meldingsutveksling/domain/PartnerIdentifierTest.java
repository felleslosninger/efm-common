package no.difi.meldingsutveksling.domain;

import org.junit.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class PartnerIdentifierTest {

    @Test
    public void parse() {
        assertThat(PartnerIdentifier.parse("0192:987654321")).isEqualTo(Iso6523.of(ICD.NO_ORG, "987654321"));
        assertThat(PartnerIdentifier.parse("08089409382")).isEqualTo(PersonIdentifier.parse("08089409382"));
        assertThat(PartnerIdentifier.parse("0232c524-cb9b-4e9e-916d-318a5696184e")).isEqualTo(FiksIoIdentifier.parse("0232c524-cb9b-4e9e-916d-318a5696184e"));
        assertThatThrownBy(() -> PartnerIdentifier.parse("A"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unable to parse identifier = 'A'")
                .hasSuppressedException(new IllegalArgumentException("Invalid ISO6523 value: 'A'"))
                .hasSuppressedException(new IllegalArgumentException("Invalid SSN='A'"))
                .hasSuppressedException(new IllegalArgumentException("Invalid UUID string: A"));
    }

    @Test
    public void parseQualifiedIdentifier() {
        assertThat(PartnerIdentifier.parseQualifiedIdentifier("iso6523-actorid-upis::0192:987654321")).isEqualTo(Iso6523.of(ICD.NO_ORG, "987654321"));
        assertThat(PartnerIdentifier.parseQualifiedIdentifier("iso6523-actorid-upis::08089409382")).isEqualTo(PersonIdentifier.parse("08089409382"));
        assertThat(PartnerIdentifier.parseQualifiedIdentifier("iso6523-actorid-upis::0232c524-cb9b-4e9e-916d-318a5696184e")).isEqualTo(FiksIoIdentifier.parse("0232c524-cb9b-4e9e-916d-318a5696184e"));
        assertThatThrownBy(() -> PartnerIdentifier.parseQualifiedIdentifier("iso6523-actorid-upis::A"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unable to parse identifier = 'iso6523-actorid-upis::A'")
                .hasSuppressedException(new IllegalArgumentException("Invalid ISO6523 value: 'A'"))
                .hasSuppressedException(new IllegalArgumentException("Invalid SSN='A'"))
                .hasSuppressedException(new IllegalArgumentException("Invalid UUID string: A"));
    }

    @Test
    public void isValid() {
        assertThat(PartnerIdentifier.isValid("0192:987654321")).isTrue();
        assertThat(PartnerIdentifier.isValid("08089409382")).isTrue();
        assertThat(PartnerIdentifier.isValid("0232c524-cb9b-4e9e-916d-318a5696184e")).isTrue();
        assertThat(PartnerIdentifier.isValid("A")).isFalse();
    }

    @Test
    public void isValidQualifiedIdentifier() {
        assertThat(PartnerIdentifier.isValidQualifiedIdentifier("iso6523-actorid-upis::0192:987654321")).isTrue();
        assertThat(PartnerIdentifier.isValidQualifiedIdentifier("iso6523-actorid-upis::08089409382")).isTrue();
        assertThat(PartnerIdentifier.isValidQualifiedIdentifier("iso6523-actorid-upis::0232c524-cb9b-4e9e-916d-318a5696184e")).isTrue();
        assertThat(PartnerIdentifier.isValidQualifiedIdentifier("iso6523-actorid-upis::A")).isFalse();
    }

    @Test
    public void cast() {
        assertThat(PartnerIdentifier.parse("0192:987654321").cast(Iso6523.class)).isEqualTo(Iso6523.parse("0192:987654321"));
        assertThatThrownBy(() -> PartnerIdentifier.parse("0192:987654321").cast(PersonIdentifier.class))
                .isInstanceOf(ClassCastException.class)
                .hasMessage("Could not cast class Iso6523 with identifier = '0192:987654321' to class PersonIdentifier")
                .hasNoCause();
    }

    @Test
    public void as() {
        assertThat(PartnerIdentifier.parse("0192:987654321").as(Iso6523.class)).hasValue(Iso6523.parse("0192:987654321"));
        assertThat(PartnerIdentifier.parse("0192:987654321").as(PersonIdentifier.class)).isEmpty();
    }

}