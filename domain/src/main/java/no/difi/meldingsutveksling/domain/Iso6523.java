package no.difi.meldingsutveksling.domain;

import lombok.Value;
import lombok.With;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static no.difi.meldingsutveksling.domain.sbdh.Authority.ISO6523_ACTORID_UPIS;

@Value
public class Iso6523 implements PartnerIdentifier, OrganizationIdentifier {

    private static final Pattern ORGANIZATION_IDENTIFIER_PATTERN = Pattern.compile("^[^\\s:][^:]{1,33}[^\\s:]$");
    private static final Pattern ORGANIZATION_PART_IDENTIFIER_PATTERN = Pattern.compile("^[^\\s:][^:]{1,33}[^\\s:]$");
    private static final Pattern SOURCE_INDICATOR = Pattern.compile("^\\d$");
    private static final Pattern ISO6523_PATTERN = Pattern.compile("^(?<icd>\\d{4}):(?<organizationIdentifier>[^:]{1,35})(?::(?<organizationPartIdentifier>[^\\s:]{1,35}))?(?::(?<sourceIndicator>\\d))?$");

    @With ICD icd;
    @With String organizationIdentifier;
    @With String organizationPartIdentifier;
    @With String sourceIndicator;

    private Iso6523(ICD icd, String organizationIdentifier, String organizationPartIdentifier, String sourceIndicator) {
        this.icd = icd;
        this.organizationIdentifier = organizationIdentifier;
        this.organizationPartIdentifier = organizationPartIdentifier;
        this.sourceIndicator = sourceIndicator;
    }

    public static Iso6523 of(ICD icd, String organizationIdentifier) {
        return of(icd, organizationIdentifier, null, null);
    }

    public static Iso6523 of(ICD icd, String organizationIdentifier, String organizationPartIdentifier) {
        return of(icd, organizationIdentifier, organizationPartIdentifier, null);
    }

    public static Iso6523 of(ICD icd, String organizationIdentifier, String organizationPartIdentifier, String sourceIndicator) {
        if (!ORGANIZATION_IDENTIFIER_PATTERN.matcher(organizationIdentifier).matches()) {
            throw new IllegalArgumentException(String.format("Invalid Organization Identifier: %s", organizationIdentifier));
        }

        if (organizationPartIdentifier != null && !ORGANIZATION_PART_IDENTIFIER_PATTERN.matcher(organizationPartIdentifier).matches()) {
            throw new IllegalArgumentException(String.format("Invalid Organization Part Identifier: %s", organizationPartIdentifier));
        }

        if (sourceIndicator != null && !SOURCE_INDICATOR.matcher(sourceIndicator).matches()) {
            throw new IllegalArgumentException(String.format("Invalid Source Indicator: %s", sourceIndicator));
        }

        if (sourceIndicator != null && organizationPartIdentifier == null) {
            throw new IllegalArgumentException("Source Indicator requires that the part identifier is specified");
        }

        return new Iso6523(icd, organizationIdentifier, organizationPartIdentifier, sourceIndicator);
    }

    public static Iso6523 parse(String identifier) {
        Matcher matcher = ISO6523_PATTERN.matcher(identifier);
        if (matcher.matches()) {
            return new Iso6523(
                    ICD.parse(matcher.group("icd")),
                    matcher.group("organizationIdentifier"),
                    matcher.group("organizationPartIdentifier"),
                    matcher.group("sourceIndicator"));
        }

        throw new IllegalArgumentException(String.format("Invalid ISO6523 value: '%s'", identifier));
    }

    public static Iso6523 parseQualifiedIdentifier(String identifier) {
        return Iso6523.parse(PartnerIdentifierUtil.getIdentifier(identifier, ISO6523_ACTORID_UPIS));
    }

    public static boolean isValid(String identifier) {
        return PartnerIdentifierUtil.isValid(identifier, Iso6523::parse);
    }

    public static boolean isValidQualifiedIdentifier(String identifier) {
        return PartnerIdentifierUtil.isValid(identifier, Iso6523::parseQualifiedIdentifier);
    }

    @Override
    public String getPrimaryIdentifier() {
        return getOrganizationIdentifier();
    }

    @Override
    public boolean hasOrganizationPartIdentifier() {
        return organizationPartIdentifier != null;
    }

    @Override
    public boolean hasSourceIndicator() {
        return sourceIndicator != null;
    }

    @Override
    public String getQualifiedIdentifier() {
        return PartnerIdentifier.super.getQualifiedIdentifier();
    }

    public Iso6523 toMainOrganization() {
        return Iso6523.of(icd, organizationIdentifier);
    }

    @Override
    public String getIdentifier() {
        StringBuilder sb = new StringBuilder();
        sb.append(icd).append(':').append(organizationIdentifier);
        if (hasOrganizationPartIdentifier()) {
            sb.append(':').append(organizationPartIdentifier);
            if (hasSourceIndicator()) {
                sb.append(':').append(sourceIndicator);
            }
        }
        return sb.toString();
    }


    @Override
    public String toString() {
        return getIdentifier();
    }
}