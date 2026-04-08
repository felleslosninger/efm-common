package no.difi.meldingsutveksling.domain;


import lombok.Value;

import java.util.regex.Pattern;

import static no.difi.meldingsutveksling.domain.sbdh.Authority.NHN_ACTORID;

@Value
public class NhnIdentifier implements PartnerIdentifier {

    private static final Pattern HERID_PATTERN = Pattern.compile("^\\d+$");

    String identifier;

    public static NhnIdentifier parse(String identifier) {
        if (!HERID_PATTERN.matcher(identifier).matches()) {
            throw new IllegalArgumentException(String.format("Invalid HerId: %s", identifier));
        }

        return new NhnIdentifier(identifier);
    }

    public static NhnIdentifier parseQualifiedIdentifier(String identifier) {
        return NhnIdentifier.parse(PartnerIdentifierUtil.getIdentifier(identifier, NHN_ACTORID));
    }

    public static boolean isValid(String identifier) {
        return PartnerIdentifierUtil.isValid(identifier, NhnIdentifier::parse);
    }

    public static boolean isValidQualifiedIdentifier(String identifier) {
        return PartnerIdentifierUtil.isValid(identifier, NhnIdentifier::parseQualifiedIdentifier);
    }

    private NhnIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String getDatabaseValue() {
        return getQualifiedIdentifier();
    }

    @Override
    public String getAuthority() {
        return NHN_ACTORID;
    }

    @Override
    public String toString() {
        return getIdentifier();
    }
}

