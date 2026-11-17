package no.difi.move.common;

import org.junit.jupiter.api.Test;

import static no.difi.move.common.IdentifierHasher.hashIfPersonnr;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for {@link IdentifierHasher}
 */
public class IdentifierHasherTest {

    @Test
    public void shouldNotReturnHashForOrgnrTest() {
        String identifier = "12345678";
        String s = hashIfPersonnr(identifier);
        assertEquals(identifier, s);
    }

    @Test
    public void shouldReturnHashForPersonnrTest() {
        String identifier = "12345678901";
        String s = hashIfPersonnr(identifier);
        assertEquals("254aa248acb47dd654ca3ea53f48c2c26d641d23d7e2e93a1ec56258df7674c4", s);
    }

    @Test
    public void shouldReturnNullForNullTest() {
        assertNull(hashIfPersonnr(null));
    }

    @Test
    public void shouldReturnHashForPrefixedPersonnrTest() {
        String identifier = "0192:12345678901";
        String s = hashIfPersonnr(identifier);
        assertFalse(s.contains("12345678901"));
        assertTrue(s.matches("[0-9a-f]{64}"));
    }

    @Test
    public void shouldNotReturnHashForPrefixedOrgnrTest() {
        String identifier = "0192:987654321";
        assertEquals(identifier, hashIfPersonnr(identifier));
    }
}
