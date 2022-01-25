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

        assertThat(Iso6523.parse("0192:987654321:MP//dummy")).isEqualTo(Iso6523.of(ICD.NO_ORG, "987654321", "MP//dummy", null));
        assertThat(Iso6523.parse("0192:987654321:MP//dummy:1")).isEqualTo(Iso6523.of(ICD.NO_ORG, "987654321", "MP//dummy", "1"));
        assertThat(Iso6523.parse("0192:987654321:MP//Vergemålsetaten:1")).isEqualTo(Iso6523.of(ICD.NO_ORG, "987654321", "MP//Vergemålsetaten", "1"));
    }

    @Test
    public void parseQualifiedIdentifier() {
        assertThat(Iso6523.parseQualifiedIdentifier("iso6523-actorid-upis::0192:987654321")).isEqualTo(Iso6523.of(ICD.NO_ORG, "987654321"));
        assertThatThrownBy(() -> Iso6523.parseQualifiedIdentifier("0192:987654321"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid qualified identifier: '0192:987654321'");

        assertThat(Iso6523.parseQualifiedIdentifier("iso6523-actorid-upis::0192:987654321:MP//dummy:1")).isEqualTo(Iso6523.of(ICD.NO_ORG, "987654321", "MP//dummy", "1"));
        assertThat(Iso6523.parseQualifiedIdentifier("iso6523-actorid-upis::0192:987654321:MP//Vergemålsetaten:1")).isEqualTo(Iso6523.of(ICD.NO_ORG, "987654321", "MP//Vergemålsetaten", "1"));
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
        assertThat(Iso6523.parse("0192:987654321:MP//dummy:1")).hasToString("0192:987654321:MP//dummy:1");
        assertThat(Iso6523.of(ICD.NO_ORG, "987654321")).hasToString("0192:987654321");
    }

    @Test
    public void getIcd() {
        assertThat(Iso6523.parse("0192:987654321:MP//dummy:1").getIcd()).isEqualTo(ICD.NO_ORG);
    }

    @Test
    public void getOrganizationIdentifier() {
        assertThat(Iso6523.parse("0192:987654321:MP//dummy:1").getOrganizationIdentifier()).isEqualTo("987654321");
    }

    @Test
    public void organizationPartIdentifier() {
        assertThat(Iso6523.parse("0192:987654321:MP//dummy:1").getOrganizationPartIdentifier()).isEqualTo("MP//dummy");
    }

    @Test
    public void sourceIndicator() {
        assertThat(Iso6523.parse("0192:987654321:MP//dummy:1").getSourceIndicator()).isEqualTo("1");
    }

    @Test
    public void hasOrganizationPartIdentifier() {
        assertThat(Iso6523.parse("0192:987654321:MP//dummy:1").hasOrganizationPartIdentifier()).isTrue();
        assertThat(Iso6523.parse("0192:987654321").hasOrganizationPartIdentifier()).isFalse();
    }

    @Test
    public void hasSourceIndicator() {
        assertThat(Iso6523.parse("0192:987654321:MP//dummy:1").hasSourceIndicator()).isTrue();
        assertThat(Iso6523.parse("0192:987654321:MP//dummy").hasSourceIndicator()).isFalse();
        assertThat(Iso6523.parse("0192:987654321").hasSourceIndicator()).isFalse();
    }

    @Test
    public void toMainOrganization() {
        assertThat(Iso6523.parse("0192:987654321:MP//dummy:1").toMainOrganization()).isEqualTo(Iso6523.parse("0192:987654321"));
    }

    @Test
    public void urlEncoding() {
        assertThat(Iso6523.parse("0192:987654321:MP//dummy:1").urlEncode()).isEqualTo("0192%3A987654321%3AMP%2F%2Fdummy%3A1");
    }

    @Test
    public void getQualifiedIdentifier() {
        assertThat(Iso6523.parse("0192:987654321").getQualifiedIdentifier()).isEqualTo("iso6523-actorid-upis::0192:987654321");
    }
}