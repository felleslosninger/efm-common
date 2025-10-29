package no.difi.meldingsutveksling.domain;

import no.difi.meldingsutveksling.domain.sbdh.BusinessScope;
import no.difi.meldingsutveksling.domain.sbdh.DocumentIdentification;
import no.difi.meldingsutveksling.domain.sbdh.Partner;
import no.difi.meldingsutveksling.domain.sbdh.PartnerIdentification;
import no.difi.meldingsutveksling.domain.sbdh.Scope;
import no.difi.meldingsutveksling.domain.sbdh.ScopeType;
import no.difi.meldingsutveksling.domain.sbdh.StandardBusinessDocumentHeader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class StandardBusinessDocumentHeaderTest {

    private StandardBusinessDocumentHeader header;

    @BeforeEach
    void setUp() {
        header = new StandardBusinessDocumentHeader();

        DocumentIdentification docId = new DocumentIdentification();
        docId.setInstanceIdentifier("123");
        docId.setStandard("no.dialogmelding.v1");
        header.setDocumentIdentification(docId);

        BusinessScope businessScope = new BusinessScope();
        header.setBusinessScope(businessScope);
    }

    @Test
    void whenDocumentTypeIsDialogmelding_thenSenderIdentifierReturnsNhnIdentifier() {
        Scope her1 = new Scope().setType(ScopeType.SENDER_HERID1.name()).setInstanceIdentifier("1001");
        Scope her2 = new Scope().setType(ScopeType.SENDER_HERID2.name()).setInstanceIdentifier("2002");
        header.getBusinessScope().addScopes(her1, her2);

        PartnerIdentification pid = new PartnerIdentification()
            .setAuthority("iso6523-actorid-upis")
            .setValue("nhn:123456789");
        Partner partner = new Partner().setIdentifier(pid);
        header.setSender(Set.of(partner));

        PartnerIdentifier identifier = header.getSenderIdentifier();

        assertNotNull(identifier);
        assertInstanceOf(NhnIdentifier.class, identifier);
        NhnIdentifier nhn = (NhnIdentifier) identifier;
        assertEquals("123456789", nhn.getIdentifier());
        assertEquals("1001:2002", nhn.getPrimaryIdentifier());
    }

    @Test
    void whenDocumentTypeIsDialogmelding_andScopesMissing_thenSenderIdentifierUsesZeroDefaults() {

        PartnerIdentification pid = new PartnerIdentification()
            .setAuthority("iso6523-actorid-upis")
            .setValue("nhn:987654321");
        Partner partner = new Partner().setIdentifier(pid);
        header.setSender(Set.of(partner));

        PartnerIdentifier identifier = header.getSenderIdentifier();

        assertNotNull(identifier);
        assertInstanceOf(NhnIdentifier.class, identifier);
        assertEquals("987654321", identifier.getIdentifier());
        assertEquals("0:0", identifier.getPrimaryIdentifier());
    }

    @Test
    void whenDocumentTypeIsDialogmelding_andScopesMissing_thenIllegalArgumentException() {

        PartnerIdentification pid = new PartnerIdentification()
            .setAuthority("iso6523-actorid-upis")
            .setValue("nhn:987654321");
        Partner partner = new Partner().setIdentifier(pid);
        header.setSender(Set.of(partner));
        header.setReceiver(Set.of(partner));

        try {
            PartnerIdentifier identifier = header.getReceiverIdentifier();
            Assertions.fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            Assertions.assertEquals("Dialogmelding requires Receiver HerdId level 2 to be present",e.getMessage());
        }
    }

    @Test
    void whenDocumentTypeIsNotDialogmelding_thenSenderIdentifierUsesGenericParser() {
        header.getDocumentIdentification().setStandard("no.standardmessage.v1");

        PartnerIdentification pid = new PartnerIdentification()
            .setAuthority("iso6523-actorid-upis")
            .setValue("0192:987654321");
        Partner partner = new Partner().setIdentifier(pid);
        header.setSender(Set.of(partner));

        PartnerIdentifier identifier = header.getSenderIdentifier();
        assertNotEquals(NhnIdentifier.class, identifier.getClass());

    }

    @Test
    void whenDocumentTypeIsDialogmelding_thenReceiverIdentifierReturnsNhnIdentifier() {

        Scope her1 = new Scope().setType(ScopeType.RECEIVER_HERID1.name()).setInstanceIdentifier("3003");
        Scope her2 = new Scope().setType(ScopeType.RECEIVER_HERID2.name()).setInstanceIdentifier("4004");
        header.getBusinessScope().addScopes(her1, her2);

        PartnerIdentification pid = new PartnerIdentification()
            .setAuthority("iso6523-actorid-upis")
            .setValue("nhn:556677889");
        Partner partner = new Partner().setIdentifier(pid);
        header.setReceiver(Set.of(partner));

        PartnerIdentifier identifier = header.getReceiverIdentifier();

        assertNotNull(identifier);
        assertInstanceOf(NhnIdentifier.class, identifier);
        NhnIdentifier nhn = (NhnIdentifier) identifier;
        assertEquals("556677889", nhn.getIdentifier());
        assertEquals("3003:4004", nhn.getPrimaryIdentifier());
    }

    @Test
    void whenDocumentTypeIsNotDialogmelding_thenReceiverIdentifierUsesGenericParser() {

        header.getDocumentIdentification().setStandard("no.genericmessage.v2");

        PartnerIdentification pid = new PartnerIdentification()
            .setAuthority("iso6523-actorid-upis")
            .setValue("0192:123456789");
        Partner partner = new Partner().setIdentifier(pid);
        header.setReceiver(Set.of(partner));

        PartnerIdentifier identifier = header.getReceiverIdentifier();

        assertNotNull(identifier);
        assertEquals("0192:123456789", identifier.getIdentifier());
    }
}
