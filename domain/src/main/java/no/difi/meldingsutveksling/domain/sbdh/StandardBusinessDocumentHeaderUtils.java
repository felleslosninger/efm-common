package no.difi.meldingsutveksling.domain.sbdh;


import java.util.Collections;
import java.util.Optional;
import java.util.Set;

@SuppressWarnings("unused")
public class StandardBusinessDocumentHeaderUtils {

    private StandardBusinessDocumentHeaderUtils() {
        // Utility class
    }

    public static Optional<String> getMessageId(StandardBusinessDocumentHeader sbdh) {
        return Optional.ofNullable(sbdh.getDocumentIdentification())
                .flatMap(p -> Optional.ofNullable(p.getInstanceIdentifier()));
    }

    public static Optional<String> getDocumentType(StandardBusinessDocumentHeader sbdh) {
        return Optional.ofNullable(sbdh.getDocumentIdentification())
                .flatMap(p -> Optional.ofNullable(p.getStandard()));
    }

    public static Set<Scope> getScopes(StandardBusinessDocumentHeader sbdh) {
        return Optional.ofNullable(sbdh.getBusinessScope())
                .flatMap(p -> Optional.ofNullable(p.getScope()))
                .orElseGet(Collections::emptySet);
    }

    public static Optional<Scope> getScope(StandardBusinessDocumentHeader sbdh, ScopeType scopeType) {
        return getScopes(sbdh)
                .stream()
                .filter(scope -> scopeType.toString().equals(scope.getType()) || scopeType.name().equals(scope.getType()))
                .findAny();
    }

    public static void addScope(StandardBusinessDocumentHeader sbdh, Scope scope) {
        Optional.ofNullable(sbdh.getBusinessScope()).ifPresent(p -> p.addScopes(scope));
    }

    public static Optional<String> getType(StandardBusinessDocumentHeader sbdh) {
        return Optional.ofNullable(sbdh.getDocumentIdentification())
                .flatMap(p -> Optional.ofNullable(p.getType()));
    }

    public static Optional<PartnerIdentification> getFirstSenderIdentifier(StandardBusinessDocumentHeader sbdh) {
        return sbdh.getFirstSender()
                .flatMap(p -> Optional.ofNullable(p.getIdentifier()));
    }

    public static Optional<PartnerIdentification> getFirstReceiverIdentifier(StandardBusinessDocumentHeader sbdh) {
        return sbdh.getFirstReceiver()
                .flatMap(p -> Optional.ofNullable(p.getIdentifier()));
    }
}
