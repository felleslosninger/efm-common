package no.difi.meldingsutveksling.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class NhnIdentifierTest {

    @Test
    public void getIdentifier() {
        assertThat(NhnIdentifier.parse("her-id:1234567").getIdentifier())
            .isEqualTo("her-id:1234567");
        assertThat(NhnIdentifier.parse("fastlege-for:17912099997").getIdentifier())
            .isEqualTo("fastlege-for:17912099997");
    }

    @Test
    public void getPrimaryIdentifier() {
        assertThat(NhnIdentifier.parse("her-id:1234567").getPrimaryIdentifier())
            .isEqualTo("her-id:1234567");
    }

    @Test
    public void parse() {
        assertThat(NhnIdentifier.parse("her-id:1234567").getIdentifier())
            .isEqualTo("her-id:1234567");
    }

    @Test
    public void parseQualifiedIdentifier() {
        assertThat(NhnIdentifier.parseQualifiedIdentifier("nhn-actorid::her-id:1234567"))
            .isEqualTo(NhnIdentifier.parse("her-id:1234567"));
    }

    @Test
    public void isValid() {
        assertThat(NhnIdentifier.isValid("her-id:1234567"))
            .isTrue();
        assertThat(NhnIdentifier.isValid("ABC"))
            .isFalse();
    }

    @Test
    public void isValidQualifiedIdentifier() {
        assertThat(NhnIdentifier.isValidQualifiedIdentifier("nhn-actorid::her-id:1234567"))
            .isTrue();
        assertThat(NhnIdentifier.isValidQualifiedIdentifier("iso6523-actorid-upis::her-id:1234567"))
            .isFalse();
    }

    @Test
    public void getUUID() {
        assertThatThrownBy(() -> NhnIdentifier.parse("her-id:1234567").getUUID())
            .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void testToString() {
        assertThat(NhnIdentifier.parse("her-id:1234567"))
            .hasToString("her-id:1234567");
    }
}
