package no.difi.meldingsutveksling.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class NhnIdentifierTest {

    @Test
    public void getIdentifier() {
        assertThat(NhnIdentifier.parse("1234567").getIdentifier())
            .isEqualTo("1234567");
    }

    @Test
    public void getPrimaryIdentifier() {
        assertThat(NhnIdentifier.parse("1234567").getPrimaryIdentifier())
            .isEqualTo("1234567");
    }

    @Test
    public void parse() {
        assertThat(NhnIdentifier.parse("1234567").getIdentifier())
            .isEqualTo("1234567");
    }

    @Test
    public void parseQualifiedIdentifier() {
        assertThat(NhnIdentifier.parseQualifiedIdentifier("nhn-actorid::1234567"))
            .isEqualTo(NhnIdentifier.parse("1234567"));
    }

    @Test
    public void isValid() {
        assertThat(NhnIdentifier.isValid("1234567"))
            .isTrue();
        assertThat(NhnIdentifier.isValid("ABC"))
            .isFalse();
    }

    @Test
    public void isValidQualifiedIdentifier() {
        assertThat(NhnIdentifier.isValidQualifiedIdentifier("nhn-actorid::1234567"))
            .isTrue();
        assertThat(NhnIdentifier.isValidQualifiedIdentifier("iso6523-actorid-upis::1234567"))
            .isFalse();
    }

    @Test
    public void getUUID() {
        assertThatThrownBy(() -> NhnIdentifier.parse("1234567").getUUID())
            .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void testToString() {
        assertThat(NhnIdentifier.parse("1234567"))
            .hasToString("1234567");
    }
}
