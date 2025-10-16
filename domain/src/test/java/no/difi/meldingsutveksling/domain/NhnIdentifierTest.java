package no.difi.meldingsutveksling.domain;

import no.idporten.validators.identifier.PersonIdentifierValidator;

import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NhnIdentifierTest {

    static {
        PersonIdentifierValidator.setSyntheticPersonIdentifiersAllowed(true);
    }


    @Test
    public void whenParsingIdentifierWithThreeParts_thenCreatesExpectedNhnIdentifier() {
        String input = "nhn:12345:6789";

        NhnIdentifier id = NhnIdentifier.parse(input);

        assertEquals("12345", id.getIdentifier());
        assertEquals("0:6789", id.getPrimaryIdentifier());
    }

    @Test
    public void whenParsingIdentifierWithFourParts_thenCreatesExpectedNhnIdentifier() {

        String input = "nhn:98765:123:456";

        NhnIdentifier id = NhnIdentifier.parse(input);

        assertEquals("98765", id.getIdentifier());
        assertEquals("123:456", id.getPrimaryIdentifier());
    }

    @Test
    public void whenParsingIdentifierWithTooFewParts_thenThrowsIllegalArgumentException() {

        String input = "nhn:onlytwo";

        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> NhnIdentifier.parse(input)
        );

        assertTrue(ex.getMessage().contains("Invalid NHN identifier"));
    }

    @Test
    public void whenUsingFactoryMethod_thenCreatesInstanceWithGivenValues() {

        NhnIdentifier id = NhnIdentifier.of("org123", "her1", "her2");

        assertEquals("org123", id.getIdentifier());
        assertEquals("her1:her2", id.getPrimaryIdentifier());
    }

    @Test
    public void whenHerId1IsNull_thenPrimaryIdentifierStartsWithZero() {

        NhnIdentifier id = NhnIdentifier.of("org123", null, "her2");

        assertEquals("0:her2", id.getPrimaryIdentifier());
    }

    @Test
    void whenIdentifierIsValidPersonNumber_thenIsFastlegeIdentifierReturnsTrue() {
        String validSynteticPersonNumber = "21905297101";

        NhnIdentifier id = NhnIdentifier.of(validSynteticPersonNumber, "1", "2");

        boolean result = id.isFastlegeIdentifier();

        assertTrue(result);

    }

    @Test
    void whenIdentifierIsInvalidPersonNumber_thenIsFastlegeIdentifierReturnsFalse() {

        String invalidPersonNumber = "345345";

        NhnIdentifier id = NhnIdentifier.of(invalidPersonNumber, "1", "2");

        boolean result = id.isFastlegeIdentifier();
        assertFalse(result);

    }

    @Test
    void whenIdentifierIsValidOrganizationNumber_thenIsNhnPartnerIdentifierReturnsTrue() {
        String validOrgnumber = "918320547";

        NhnIdentifier id = NhnIdentifier.of(validOrgnumber, "h1", "h2");

        boolean result = id.isNhnPartnerIdentifier();


        assertTrue(result);
    }


    @Test
    void whenIdentifierIsInvalidOrganizationNumber_thenIsNhnPartnerIdentifierReturnsFalse() {
        String validOrgnumber = "91832054722";

        NhnIdentifier id = NhnIdentifier.of(validOrgnumber, "h1", "h2");

        boolean result = id.isNhnPartnerIdentifier();

        assertFalse(result);
    }

    @Test
    void whenCheckingHasOrganizationPartIdentifier_thenReturnsDefaultFalse() {

        NhnIdentifier id = NhnIdentifier.of("123", "a", "b");

        assertFalse(id.hasOrganizationPartIdentifier());
    }


}
