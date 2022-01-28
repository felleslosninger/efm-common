package no.difi.meldingsutveksling.domain;

import lombok.Value;

import java.util.UUID;

import static no.difi.meldingsutveksling.domain.sbdh.Authority.ISO6523_ACTORID_UPIS;

@Value
public class FiksIoIdentifier implements PartnerIdentifier {

    UUID identifier;

    public static FiksIoIdentifier of(UUID identifier) {
        return new FiksIoIdentifier(identifier);
    }

    public static FiksIoIdentifier parse(String identifier) {
        return new FiksIoIdentifier(UUID.fromString(identifier));
    }

    public static FiksIoIdentifier parseQualifiedIdentifier(String identifier) {
        return FiksIoIdentifier.parse(PartnerIdentifierUtil.getIdentifier(identifier, ISO6523_ACTORID_UPIS));
    }

    public static boolean isValid(String identifier) {
        return PartnerIdentifierUtil.isValid(identifier, FiksIoIdentifier::parse);
    }

    public static boolean isValidQualifiedIdentifier(String identifier) {
        return PartnerIdentifierUtil.isValid(identifier, FiksIoIdentifier::parseQualifiedIdentifier);
    }

    private FiksIoIdentifier(UUID identifier) {
        this.identifier = identifier;
    }

    @Override
    public UUID getUUID() {
        return identifier;
    }

    @Override
    public String getIdentifier() {
        return identifier.toString();
    }

    @Override
    public String toString() {
        return getIdentifier();
    }
}
