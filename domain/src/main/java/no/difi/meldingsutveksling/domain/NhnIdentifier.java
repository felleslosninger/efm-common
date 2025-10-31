package no.difi.meldingsutveksling.domain;


import lombok.Value;
import lombok.With;
import no.idporten.validators.identifier.PersonIdentifierValidator;

import java.util.regex.Pattern;

@Value
public final class NhnIdentifier implements PartnerIdentifier {

    public static String DIALOGMELDING_TYPE = "dialogmelding";
    public static String IDENTIFIER_SEPARATOR = ":";
    public static String ZERO_HERID = "0";

    @With
    String identifier;
    @With String herId1;
    @With String herId2;

    public static NhnIdentifier parse(String identifier) {
        String[] parts = identifier.split(IDENTIFIER_SEPARATOR);
        if (parts.length < 3) {
            throw new IllegalArgumentException("Invalid NHN identifier: " + identifier + "it should consist of minimum 3 parts");
        }
        var ident = parts[1];
        var herId1 = parts.length == 3 ? ZERO_HERID :parts[2];
        var herId2 = parts.length == 4 ? parts[3] : parts[2];
        return new NhnIdentifier( ident,herId1,herId2);
    }

    private NhnIdentifier(String identifier,String herId1,String herId2) {
        this.identifier = identifier;
        this.herId1 = herId1 == null ? ZERO_HERID : herId1;
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
            return ZERO_HERID + IDENTIFIER_SEPARATOR + herId2;
        } else {
            return herId1 + IDENTIFIER_SEPARATOR + herId2;
        }
    }

    public String getHerId1() {
        return herId1;
    }

    public String getHerId2() {
        return herId2;
    }

    public boolean isFastlegeIdentifier() {
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

