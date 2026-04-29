package no.difi.meldingsutveksling.domain;


import lombok.Value;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static no.difi.meldingsutveksling.domain.sbdh.Authority.NHN_ACTORID;

@Value
public class NhnIdentifier implements PartnerIdentifier {

    private static final String HER_ID_PREFIX = "her-id";
    private static final String FASTLEGE_FOR_PREFIX = "fastlege-for";
    private static final Pattern NHN_PATTERN = Pattern.compile("^(?<prefix>(her-id|fastlege-for)):(?<id>[^:]{1,11})$");

    Type type;
    Integer herId;
    PersonIdentifier fastlegeFor;

    private NhnIdentifier(Type type, Integer herId, PersonIdentifier fastlegeFor) {
        this.type = type;
        this.herId = herId;
        this.fastlegeFor = fastlegeFor;
    }

    public static NhnIdentifier herId(int herId) {
        if (herId <= 0) {
            throw new IllegalArgumentException("HER-id have to be positive!");
        }
        return new NhnIdentifier(Type.HER_ID, herId, null);
    }

    public static NhnIdentifier fastlegeFor(PersonIdentifier personIdentifier) {
        return new NhnIdentifier(Type.FASTLEGE_FOR, null, personIdentifier);
    }

    public static NhnIdentifier parse(String identifier) {
        Matcher matcher = NHN_PATTERN.matcher(identifier);

        if (!matcher.matches()) {
            throw new IllegalArgumentException(String.format("Invalid NHN identifier: %s", identifier));
        }

        String prefix = matcher.group("prefix");
        String id = matcher.group("id");

        if (FASTLEGE_FOR_PREFIX.equals(prefix)) {
            return NhnIdentifier.fastlegeFor(PersonIdentifier.parse(id));
        }

        return NhnIdentifier.herId(Integer.parseInt(id));
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

    public String getId() {
        return type == Type.HER_ID ? herId.toString() : fastlegeFor.getIdentifier();
    }

    @Override
    public String getIdentifier() {
        return type.prefix() + ":" + getId();
    }

    @Override
    public String getDatabaseValue() {
        return getIdentifier();
    }

    @Override
    public String getAuthority() {
        return NHN_ACTORID;
    }

    @Override
    public String toString() {
        return getIdentifier();
    }

    public enum Type {
        HER_ID("her-id"),
        FASTLEGE_FOR("fastlege-for");

        private final String prefix;

        Type(String prefix) {
            this.prefix = prefix;
        }

        public String prefix() {
            return prefix;
        }
    }
}

