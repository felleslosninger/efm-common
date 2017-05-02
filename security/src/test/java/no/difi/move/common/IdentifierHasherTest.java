package no.difi.move.common;

import org.junit.Test;

import static no.difi.move.common.IdentifierHasher.hashIfPersonnr;
import static org.junit.Assert.assertEquals;

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
        assertEquals("bfd81ee3ed27ad31c95ca75e21365973", s);
    }

}
