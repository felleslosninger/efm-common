package no.difi.meldingsutveksling.domain;

import no.difi.meldingsutveksling.domain.sbdh.Authority;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

import static no.difi.meldingsutveksling.domain.sbdh.Authority.ISO6523_ACTORID_UPIS;

public sealed interface PartnerIdentifier extends Serializable, Comparable<PartnerIdentifier> permits FiksIoIdentifier, Iso6523, NhnIdentifier, PersonIdentifier {

    static PartnerIdentifier of(String identifier, String authority) {
        return switch (authority) {
            case Authority.NHN_ACTORID -> NhnIdentifier.parse(identifier);
            case ISO6523_ACTORID_UPIS -> PartnerIdentifierUtil.parse(identifier, List.of(
                Iso6523::parse, PersonIdentifier::parse, FiksIoIdentifier::parse));
            default -> throw new IllegalArgumentException("Unknown authority = %s".formatted(authority));
        };
    }

    static PartnerIdentifier parseDatabaseValue(String dbData) {
        return PartnerIdentifierUtil.parse(dbData, List.of(
            Iso6523::parse, PersonIdentifier::parse, FiksIoIdentifier::parse, NhnIdentifier::parse));
    }

    static PartnerIdentifier parse(String identifier) {
        return PartnerIdentifierUtil.parse(identifier, List.of(
            Iso6523::parse, PersonIdentifier::parse, FiksIoIdentifier::parse, NhnIdentifier::parse));
    }

    static PartnerIdentifier parseQualifiedIdentifier(String identifier) {
        return PartnerIdentifierUtil.parse(identifier, List.of(
            Iso6523::parseQualifiedIdentifier,
            PersonIdentifier::parseQualifiedIdentifier,
            FiksIoIdentifier::parseQualifiedIdentifier,
            NhnIdentifier::parseQualifiedIdentifier));
    }

    static boolean isValid(String identifier) {
        return ((Predicate<String>) Iso6523::isValid)
            .or(PersonIdentifier::isValid)
            .or(FiksIoIdentifier::isValid)
            .or(NhnIdentifier::isValid)
            .test(identifier);
    }

    static boolean isValidQualifiedIdentifier(String identifier) {
        return ((Predicate<String>) Iso6523::isValidQualifiedIdentifier)
            .or(PersonIdentifier::isValidQualifiedIdentifier)
            .or(FiksIoIdentifier::isValidQualifiedIdentifier)
            .or(NhnIdentifier::isValidQualifiedIdentifier)
            .test(identifier);
    }

    String getIdentifier();

    /// For backward compatibility this returns identifier.
    /// Then we assume that the authority is iso6523-actorid-upis.
    /// For other authorities this value should be equal to the qualified identifier
    default String getDatabaseValue() {
        return getIdentifier();
    }

    default String getPrimaryIdentifier() {
        return getIdentifier();
    }

    default boolean hasOrganizationPartIdentifier() {
        return false;
    }

    default boolean hasSourceIndicator() {
        return false;
    }

    default ICD getIcd() {
        throw new UnsupportedOperationException();
    }

    default String getOrganizationIdentifier() {
        throw new UnsupportedOperationException();
    }

    default String getOrganizationPartIdentifier() {
        throw new UnsupportedOperationException();
    }

    default String getSourceIndicator() {
        throw new UnsupportedOperationException();
    }

    default UUID getUUID() {
        throw new UnsupportedOperationException();
    }

    default String getAuthority() {
        return ISO6523_ACTORID_UPIS;
    }

    default String getQualifiedIdentifier() {
        return String.format("%s::%s", getAuthority(), getIdentifier());
    }

    default String urlEncode() {
        return URLEncoder.encode(this.toString(), StandardCharsets.UTF_8);
    }

    default <T extends PartnerIdentifier> T cast(Class<T> clazz) {
        if (clazz.isInstance(this)) {
            return clazz.cast(this);
        }

        throw new ClassCastException(String.format("Could not cast class %s with identifier = '%s' to class %s",
            this.getClass().getSimpleName(), this, clazz.getSimpleName()));
    }

    default <T extends PartnerIdentifier> Optional<T> as(Class<T> clazz) {
        return clazz.isInstance(this) ? Optional.of(clazz.cast(this)) : Optional.empty();
    }

    default int compareTo(PartnerIdentifier o) {
        return getIdentifier().compareTo(o.getIdentifier());
    }
}
