package no.difi.meldingsutveksling.domain;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;

import static no.difi.meldingsutveksling.domain.sbdh.Authority.ISO6523_ACTORID_UPIS;

public interface PartnerIdentifier extends Serializable {

    static PartnerIdentifier parse(String identifier) {
        return PartnerIdentifierUtil.parse(identifier, Arrays.<Function<String, PartnerIdentifier>>asList(
                Iso6523::parse, PersonIdentifier::parse, FiksIoIdentifier::parse));
    }

    static PartnerIdentifier parseQualifiedIdentifier(String identifier) {
        return PartnerIdentifierUtil.parse(identifier, Arrays.<Function<String, PartnerIdentifier>>asList(
                Iso6523::parseQualifiedIdentifier, PersonIdentifier::parseQualifiedIdentifier, FiksIoIdentifier::parseQualifiedIdentifier));
    }

    static boolean isValid(String identifier) {
        return ((Predicate<String>) Iso6523::isValid)
                .or(PersonIdentifier::isValid)
                .or(FiksIoIdentifier::isValid)
                .test(identifier);
    }

    static boolean isValidQualifiedIdentifier(String identifier) {
        return ((Predicate<String>) Iso6523::isValidQualifiedIdentifier)
                .or(PersonIdentifier::isValidQualifiedIdentifier)
                .or(FiksIoIdentifier::isValidQualifiedIdentifier)
                .test(identifier);
    }

    String getIdentifier();

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
        return String.format("%s::%s", ISO6523_ACTORID_UPIS, getIdentifier());
    }

    default String urlEncode() {
        try {
            return URLEncoder.encode(this.toString(), StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("UTF-8 encoding not supported", e);
        }
    }
}
