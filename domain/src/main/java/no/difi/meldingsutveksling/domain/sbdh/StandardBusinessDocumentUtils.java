package no.difi.meldingsutveksling.domain.sbdh;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

public class StandardBusinessDocumentUtils {

    private StandardBusinessDocumentUtils() {
        // Utility class
    }

    public static Optional<Sender> getFirstSender(StandardBusinessDocument sbd) {
        return Optional.of(sbd.getStandardBusinessDocumentHeader())
                .flatMap(StandardBusinessDocumentHeader::getFirstSender);
    }

    public static Optional<Receiver> getFirstReceiver(StandardBusinessDocument sbd) {
        return Optional.of(sbd.getStandardBusinessDocumentHeader())
                .flatMap(StandardBusinessDocumentHeader::getFirstReceiver);
    }

    public static Optional<String> getMessageId(StandardBusinessDocument sbd) {
        return Optional.of(sbd.getStandardBusinessDocumentHeader())
                .flatMap(StandardBusinessDocumentHeaderUtils::getMessageId);
    }

    public static Optional<String> getDocumentType(StandardBusinessDocument sbd) {
        return Optional.of(sbd.getStandardBusinessDocumentHeader())
                .flatMap(StandardBusinessDocumentHeaderUtils::getDocumentType);
    }

    public static Set<Scope> getScopes(StandardBusinessDocument sbd) {
        return Optional.of(sbd.getStandardBusinessDocumentHeader())
                .map(StandardBusinessDocumentHeaderUtils::getScopes)
                .orElseGet(Collections::emptySet);
    }

    public static Optional<Scope> getScope(StandardBusinessDocument sbd, ScopeType scopeType) {
        return Optional.of(sbd.getStandardBusinessDocumentHeader())
                .flatMap(p -> StandardBusinessDocumentHeaderUtils.getScope(p, scopeType));

    }

    public static Optional<String> getType(StandardBusinessDocument sbd) {
        return Optional.ofNullable(sbd.getStandardBusinessDocumentHeader())
                .flatMap(StandardBusinessDocumentHeaderUtils::getType);
    }

    public static Optional<OffsetDateTime> getExpectedResponseDateTime(StandardBusinessDocument sbd) {
        return getScope(sbd, ScopeType.CONVERSATION_ID)
                .flatMap(p -> Optional.ofNullable(p.getScopeInformation()))
                .flatMap(p -> p.stream().findFirst())
                .flatMap(p -> Optional.ofNullable(p.getExpectedResponseDateTime()));
    }
}
