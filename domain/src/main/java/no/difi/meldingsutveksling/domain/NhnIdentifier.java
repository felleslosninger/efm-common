package no.difi.meldingsutveksling.domain;


import no.idporten.validators.identifier.PersonIdentifierValidator;

import java.util.regex.Pattern;

public final class NhnIdentifier implements PartnerIdentifier {

    public static String DIALOGMELDING_TYPE = "dialogmelding";
    public static String IDENTIFIER_SEPARATOR = ":";
    public static String ZERO_HERID = "0";

    private static final Pattern ORGANIZATION_PART_IDENTIFIER_PATTERN = Pattern.compile("^[^\\s:][^:]{1,33}[^\\s:]$");

    private String identifier;
    private String herId1;
    private String herId2;

    public static NhnIdentifier parse(String identifier) {
        String[] parts = identifier.split(":");
        if (parts.length < 3) {
            throw new IllegalArgumentException("Invalid NHN identifier: " + identifier + "it should consist of minimum 3 parts");
        }
        var ident = parts[1];
        var herId1 = parts.length == 3 ? "0" :parts[2];
        var herId2 = parts.length == 4 ? parts[3] : parts[2];
        return new NhnIdentifier( ident,herId1,herId2);
    }

    private NhnIdentifier(String identifier,String herId1,String herId2) {
        this.identifier = identifier;
        this.herId1 = herId1 == null ? "0" : herId1;
        this.herId2 = herId2;
    }

    public static NhnIdentifier of(String identifier,String herId1, String herId2) {
        return new NhnIdentifier(identifier,herId1,herId2);
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String getPrimaryIdentifier() {
        if (herId1 == null) {
            return "0:" + herId2;
        } else {
            return herId1 + ":" + herId2;
        }
    }

    public boolean isFastlegeIdentifier() {
        PersonIdentifierValidator.setSyntheticPersonIdentifiersAllowed(true);
        return PersonIdentifierValidator.isValid(identifier);
    }

    public boolean isNhnPartnerIdentifier() {
       return no.idporten.validators.orgnr.OrgnrValidator.isValid(identifier);
    }

    @Override
    public boolean hasOrganizationPartIdentifier() {
        return PartnerIdentifier.super.hasOrganizationPartIdentifier();
    }
}

