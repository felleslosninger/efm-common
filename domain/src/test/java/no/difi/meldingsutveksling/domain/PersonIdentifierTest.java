package no.difi.meldingsutveksling.domain;

import org.junit.Test;

import java.lang.annotation.Repeatable;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class PersonIdentifierTest {

    @Test
    public void parse() {
        assertThat(PersonIdentifier.parse("08089409382"))
                .extracting(PersonIdentifier::getIdentifier)
                .isEqualTo("08089409382");
        assertThat(PersonIdentifier.parse("17912099997"))
                .extracting(PersonIdentifier::getIdentifier)
                .isEqualTo("17912099997");
        assertThat(PersonIdentifier.parse("57912075186"))
                .extracting(PersonIdentifier::getIdentifier)
                .isEqualTo("57912075186");
        assertThatThrownBy(() -> PersonIdentifier.parse("08089409381"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid check digit. Expected 2, but was 1");
    }

    @Test
    public void parseQualifiedIdentifier() {
        assertThat(PersonIdentifier.parseQualifiedIdentifier("iso6523-actorid-upis::08089409382"))
                .extracting(PersonIdentifier::getIdentifier)
                .isEqualTo("08089409382");
        assertThatThrownBy(() -> PersonIdentifier.parseQualifiedIdentifier("iso6523-actorid-upis::08089409381"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid check digit. Expected 2, but was 1");

        assertThatThrownBy(() -> PersonIdentifier.parseQualifiedIdentifier("08089409381"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid qualified identifier = '08089409381'. Expected <authoriyy>::<identifier>");
    }

    @Test
    public void isValid() {
        assertThat(PersonIdentifier.isValid("08089409382")).isTrue();
        assertThat(PersonIdentifier.isValid("08089409381")).isFalse();
        assertThat(PersonIdentifier.isValid("987654321")).isFalse();
    }

    @Test
    public void isValidQualifiedIdentifier() {
        assertThat(PersonIdentifier.isValidQualifiedIdentifier("iso6523-actorid-upis::08089409382")).isTrue();
        assertThat(PersonIdentifier.isValidQualifiedIdentifier("iso6523-actorid-upis::08089409381")).isFalse();
        assertThat(PersonIdentifier.isValidQualifiedIdentifier("iso6523-actorid-upis::987654321")).isFalse();
    }

    @Test
    public void random() {
        assertThat(PersonIdentifier.isValid(PersonIdentifier.random().getIdentifier())).isTrue();
        assertThat(PersonIdentifier.random(Gender.MALE).getGender()).isEqualTo(Gender.MALE);
        assertThat(PersonIdentifier.random(Gender.FEMALE).getGender()).isEqualTo(Gender.FEMALE);
        assertThat(PersonIdentifier.random(LocalDate.parse("1975-03-20"), Gender.MALE))
                .satisfies(personIdentifier -> assertThat(personIdentifier.getDateOfBirth()).isEqualTo("1975-03-20"))
                .satisfies(personIdentifier -> assertThat(personIdentifier.getGender()).isEqualTo(Gender.MALE));
    }

    @Test
    public void testToString() {
        assertThat(PersonIdentifier.parse("08089409382")).hasToString("08089409382");
    }

    @Test
    public void getDateOfBirth() {
        assertThat(PersonIdentifier.parse("08089409382").getDateOfBirth()).isEqualTo("1994-08-08");
        assertThat(PersonIdentifier.parse("17912099997").getDateOfBirth()).isEqualTo("2020-11-17");
        assertThat(PersonIdentifier.parse("57912075186").getDateOfBirth()).isEqualTo("2020-11-17");
    }

    @Test
    public void getAge() {
        Clock clock = Clock.fixed(Instant.parse("2022-01-27T09:07:01Z"), ZoneId.of("Europe/Oslo"));
        assertThat(PersonIdentifier.parse("08089409382").getAge(clock)).isEqualTo(27);
        assertThat(PersonIdentifier.parse("17912099997").getAge(clock)).isEqualTo(1);
        assertThat(PersonIdentifier.parse("57912075186").getAge(clock)).isEqualTo(1);
    }

    @Test
    public void getIndividualNumber() {
        assertThat(PersonIdentifier.parse("08089409382").getIndividualNumber()).isEqualTo("093");
        assertThat(PersonIdentifier.parse("17912099997").getIndividualNumber()).isEqualTo("999");
        assertThat(PersonIdentifier.parse("57912075186").getIndividualNumber()).isEqualTo("751");
    }

    @Test
    public void getPersonNumber() {
        assertThat(PersonIdentifier.parse("08089409382").getPersonNumber()).isEqualTo("09382");
        assertThat(PersonIdentifier.parse("17912099997").getPersonNumber()).isEqualTo("99997");
        assertThat(PersonIdentifier.parse("57912075186").getPersonNumber()).isEqualTo("75186");
    }

    @Test
    public void getGender() {
        assertThat(PersonIdentifier.parse("08089409382").getGender()).isEqualTo(Gender.MALE);
        assertThat(PersonIdentifier.parse("08089408831").getGender()).isEqualTo(Gender.FEMALE);
        assertThat(PersonIdentifier.parse("57912075186").getGender()).isEqualTo(Gender.MALE);
    }

    @Test
    public void isMale() {
        assertThat(PersonIdentifier.parse("08089409382").isMale()).isTrue();
        assertThat(PersonIdentifier.parse("08089408831").isMale()).isFalse();
        assertThat(PersonIdentifier.parse("57912075186").isMale()).isTrue();
    }

    @Test
    public void isFemale() {
        assertThat(PersonIdentifier.parse("08089409382").isFemale()).isFalse();
        assertThat(PersonIdentifier.parse("08089408831").isFemale()).isTrue();
        assertThat(PersonIdentifier.parse("57912075186").isFemale()).isFalse();
    }

    @Test
    public void isDNumber() {
        assertThat(PersonIdentifier.parse("08089409382").isDNumber()).isFalse();
        assertThat(PersonIdentifier.parse("08089408831").isDNumber()).isFalse();
        assertThat(PersonIdentifier.parse("57912075186").isDNumber()).isTrue();
        assertThat(PersonIdentifier.parse("17912099997").isDNumber()).isFalse();
    }

    @Test
    public void isHNumber() {
        assertThat(PersonIdentifier.parse("08089409382").isHNumber()).isFalse();
        assertThat(PersonIdentifier.parse("08089408831").isHNumber()).isFalse();
        assertThat(PersonIdentifier.parse("57912075186").isHNumber()).isFalse();
        assertThat(PersonIdentifier.parse("17912099997").isHNumber()).isFalse();
    }

    @Test
    public void isSynthetic() {
        assertThat(PersonIdentifier.parse("08089409382").isSynthetic()).isFalse();
        assertThat(PersonIdentifier.parse("08089408831").isSynthetic()).isFalse();
        assertThat(PersonIdentifier.parse("57912075186").isSynthetic()).isTrue();
        assertThat(PersonIdentifier.parse("17912099997").isSynthetic()).isTrue();
    }

    @Test
    public void getIcd() {
        assertThatThrownBy(() -> PersonIdentifier.parse("08089409382").getIcd())
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void getOrganizationPartIdentifier() {
        assertThatThrownBy(() -> PersonIdentifier.parse("08089409382").getOrganizationPartIdentifier())
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void getSourceIndicator() {
        assertThatThrownBy(() -> PersonIdentifier.parse("08089409382").getSourceIndicator())
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void hasOrganizationPartIdentifier() {
        assertThat(PersonIdentifier.parse("08089409382").hasOrganizationPartIdentifier()).isFalse();
    }

    @Test
    public void hasSourceIndicator() {
        assertThat(PersonIdentifier.parse("08089409382").hasSourceIndicator()).isFalse();
    }

    @Test
    public void urlEncoding() {
        assertThat(PersonIdentifier.parse("08089409382").urlEncode()).isEqualTo("08089409382");
    }

    @Test
    public void getQualifiedIdentifier() {
        assertThat(PersonIdentifier.parse("08089409382").getQualifiedIdentifier()).isEqualTo("iso6523-actorid-upis::08089409382");
    }

    @Test
    public void getAuthority() {
        assertThat(PersonIdentifier.parse("08089409382").getAuthority()).isEqualTo("iso6523-actorid-upis");
    }
}