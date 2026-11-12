package no.difi.meldingsutveksling.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/// MOVE-5376: Iso6523 must reject identifiers containing markup, control characters and invalid organization numbers.
class Iso6523ValidationTest {

    @ParameterizedTest
    @ValueSource(strings = {
        "0192:<img src=x onerror=alert(1)>",
        "0192:abc",
        "0192:996668649;x",
        "0192:12345678",
        "0192:1234567890",
        "0192:99666864a",
        "0192:+99666864",
    })
    void norwegianOrganizationIdentifierMustBeNineDigits(String identifier) {
        assertThat(Iso6523.isValid(identifier)).isFalse();
        assertThat(Iso6523.isValidStrict(identifier)).isFalse();
        assertThatThrownBy(() -> Iso6523.parse(identifier)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void norwegianOrganizationIdentifierWithInvalidCheckDigitIsRejectedByStrictValidation() {
        assertThat(Iso6523.isValidStrict("0192:123456789")).isFalse();
        assertThat(Iso6523.isValidStrict("123456789")).isFalse();
        assertThatThrownBy(() -> Iso6523.parseStrict("0192:123456789"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Invalid check digit in Norwegian Organization Identifier: 123456789");
    }

    @Test
    void parseDoesNotCheckTheCheckDigitSoExistingDataCanStillBeRead() {
        assertThat(Iso6523.isValid("0192:123456789")).isTrue();
        assertThat(Iso6523.isValid("0192:987654321")).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "0192:996668649",
        "996668649",
        "0192:991825827",
        "0192:974760673",
        "0192:996668649:MP//Vergemålsetaten",
        "0192:996668649:MP//Vergemålsetaten:1",
    })
    void validNorwegianOrganizationIdentifierIsAccepted(String identifier) {
        assertThat(Iso6523.isValid(identifier)).isTrue();
        assertThat(Iso6523.isValidStrict(identifier)).isTrue();
        assertThat(Iso6523.parseStrict(identifier).getIcd()).isEqualTo(ICD.NO_ORG);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "0007:19571003-6970 001",
        "0007:abc def",
        "0007:abc\tdef",
        "0007:abc\r\ndef",
        "0007:abc\u0000",
        "0007:abc def",
        "0007:abc​def",
        "0007:<abc>",
        "0007:abc\"def",
        "0007:abc;def",
        "0007:abc/def",
        "0007:abcæøå",
        "0088:7080000000001;x",
        "0192:996668649 ",
        "0192: 996668649",
        "0192:996668649\n",
    })
    void organizationIdentifierWithIllegalCharactersIsRejectedForAnyIcd(String identifier) {
        assertThat(Iso6523.isValid(identifier)).isFalse();
        assertThat(Iso6523.isValidStrict(identifier)).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "0192:996668649:<script>",
        "0192:996668649:MP//a b",
        "0192:996668649:MP\r\nx",
        "0192:996668649:a;b",
        "0192:996668649:a\"b",
        "0192:996668649:a‎b",
    })
    void organizationPartIdentifierWithIllegalCharactersIsRejected(String identifier) {
        assertThat(Iso6523.isValid(identifier)).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "0007:000000-0000",
        "0007:5560360793",
        "0088:7080000000001",
        "0096:DK12345678",
        "9930:DE123456789",
    })
    void validIdentifierForOtherIcdIsStillAccepted(String identifier) {
        assertThat(Iso6523.isValid(identifier)).isTrue();
        assertThat(Iso6523.isValidStrict(identifier)).isTrue();
        assertThat(Iso6523.parse(identifier).getIdentifier()).isEqualTo(identifier);
    }

    @Test
    void ofAndWithMethodsAreValidatedToo() {
        assertThatThrownBy(() -> Iso6523.of(ICD.NO_ORG, "<img>"))
            .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Iso6523.of(ICD.NO_ORG, "abc"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Invalid Norwegian Organization Identifier: abc. Expected 9 digits");
        assertThatThrownBy(() -> Iso6523.of(ICD.NO_ORG, null))
            .isInstanceOf(IllegalArgumentException.class);

        Iso6523 valid = Iso6523.of(ICD.NO_ORG, "996668649");
        assertThatThrownBy(() -> valid.withOrganizationIdentifier("996668649;x"))
            .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> valid.withOrganizationPartIdentifier("<script>"))
            .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> valid.withIcd(null))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void exceptionMessagesDoNotContainLineBreaks() {
        assertThatThrownBy(() -> Iso6523.parse("0007:abc\r\nFAKE LOG LINE"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Invalid Organization Identifier: abc??FAKE LOG LINE");
        assertThatThrownBy(() -> PartnerIdentifier.parse("x\r\nFAKE LOG LINE"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Unable to parse identifier = 'x??FAKE LOG LINE'");
    }
}
