package no.difi.meldingsutveksling.domain;

import org.junit.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;


public class Iso6523Test {

    @Test
    public void parse() {
        assertThat(Iso6523.parse("0192:987654321")).isEqualTo(Iso6523.of(ICD.NO_ORG, "987654321"));
        assertThatThrownBy(() -> Iso6523.parse("0192 :987654321"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid ISO6523 value: '0192 :987654321'");
    }

    @Test
    public void isValid() {
        assertThat(Iso6523.isValid("0192:987654321")).isTrue();
        assertThat(Iso6523.isValid("0192:fiken")).isTrue();
        assertThat(Iso6523.isValid("9908:CSAMED-01")).isTrue();
        assertThat(Iso6523.isValid("0007:000000-0000")).isTrue();
        assertThat(Iso6523.isValid("0007:19571003-6970 001")).isTrue();

        assertThat(Iso6523.isValid("0192A987654321")).isFalse();
    }

    @Test
    public void testToString() {
        assertThat(Iso6523.of(ICD.NO_ORG, "987654321")).hasToString("0192:987654321");
    }
}