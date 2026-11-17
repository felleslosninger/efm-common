package no.difi.meldingsutveksling.logging;

import net.logstash.logback.marker.LogstashMarker;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class MarkerFactoryTest {

    private static final String PERSONNR = "12345678901";
    private static final String PERSONNR_HASH = "254aa248acb47dd654ca3ea53f48c2c26d641d23d7e2e93a1ec56258df7674c4";
    private static final String ORGNR = "987654321";

    @Test
    void senderMarkerHashesPersonnr() {
        LogstashMarker marker = MarkerFactory.senderMarker(PERSONNR);

        assertFalse(marker.toString().contains(PERSONNR));
        assertEquals("sender_org_number=" + PERSONNR_HASH, marker.toString());
    }

    @Test
    void senderMarkerKeepsOrgnrInPlainText() {
        assertEquals("sender_org_number=" + ORGNR, MarkerFactory.senderMarker(ORGNR).toString());
    }

    @Test
    void senderMarkerAcceptsNull() {
        assertDoesNotThrow(() -> MarkerFactory.senderMarker(null));
    }

    @Test
    void receiverMarkerAcceptsNull() {
        assertDoesNotThrow(() -> MarkerFactory.receiverMarker(null));
    }

    @Test
    void receiverMarkerHashesPersonnr() {
        assertEquals("receiver_org_number=" + PERSONNR_HASH, MarkerFactory.receiverMarker(PERSONNR).toString());
    }
}
