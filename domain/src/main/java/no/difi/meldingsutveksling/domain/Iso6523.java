package no.difi.meldingsutveksling.domain;

import lombok.Value;
import lombok.With;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static no.difi.meldingsutveksling.domain.sbdh.Authority.ISO6523_ACTORID_UPIS;

@Value
public class Iso6523 implements PartnerIdentifier, OrganizationIdentifier {

    private static final Pattern NORWEGIAN_ORGANIZATION_IDENTIFIER_PATTERN = Pattern.compile("^\\d{9}$");
    /// Only ASCII letters, digits, '.', '_' and '-'. Rejects whitespace, control characters,
    /// markup and separators such as ';' and '/' (MOVE-5376).
    private static final Pattern ORGANIZATION_IDENTIFIER_PATTERN = Pattern.compile("^[A-Za-z0-9._-]{1,35}$");
    /// Part identifiers are free text such as "MP//Vergemålsetaten", so '/' and Unicode letters are allowed.
    /// Whitespace, control/format characters, ':' and characters used in markup or as separators are not.
    private static final Pattern ORGANIZATION_PART_IDENTIFIER_PATTERN = Pattern.compile("^[^\\s\\p{Z}\\p{Cc}\\p{Cf}:<>\"'`;&\\\\]{1,35}$");
    private static final Pattern SOURCE_INDICATOR = Pattern.compile("^\\d$");
    private static final Pattern ISO6523_PATTERN = Pattern.compile("^(?<icd>\\d{4}):(?<organizationIdentifier>[^:]{1,35})(?::(?<organizationPartIdentifier>[^:]{1,35}))?(?::(?<sourceIndicator>\\d))?$");
    private static final int[] NORWEGIAN_ORGANIZATION_IDENTIFIER_WEIGHTS = {3, 2, 7, 6, 5, 4, 3, 2};

    @With
    ICD icd;
    @With
    String organizationIdentifier;
    @With
    String organizationPartIdentifier;
    @With
    String sourceIndicator;

    private Iso6523(ICD icd, String organizationIdentifier, String organizationPartIdentifier, String sourceIndicator) {
        validate(icd, organizationIdentifier, organizationPartIdentifier, sourceIndicator);
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
        return new Iso6523(icd, organizationIdentifier, organizationPartIdentifier, sourceIndicator);
    }

    /// Called from the constructor, so that it also applies to [#parse(String)] and the `with`-methods.
    private static void validate(ICD icd, String organizationIdentifier, String organizationPartIdentifier, String sourceIndicator) {
        if (icd == null) {
            throw new IllegalArgumentException("ICD is required");
        }

        if (organizationIdentifier == null || !ORGANIZATION_IDENTIFIER_PATTERN.matcher(organizationIdentifier).matches()) {
            throw new IllegalArgumentException(String.format("Invalid Organization Identifier: %s", PartnerIdentifierUtil.sanitize(organizationIdentifier)));
        }

        if (icd == ICD.NO_ORG && !NORWEGIAN_ORGANIZATION_IDENTIFIER_PATTERN.matcher(organizationIdentifier).matches()) {
            throw new IllegalArgumentException(String.format("Invalid Norwegian Organization Identifier: %s. Expected 9 digits", organizationIdentifier));
        }

        if (organizationPartIdentifier != null && !ORGANIZATION_PART_IDENTIFIER_PATTERN.matcher(organizationPartIdentifier).matches()) {
            throw new IllegalArgumentException(String.format("Invalid Organization Part Identifier: %s", PartnerIdentifierUtil.sanitize(organizationPartIdentifier)));
        }

        if (sourceIndicator != null && !SOURCE_INDICATOR.matcher(sourceIndicator).matches()) {
            throw new IllegalArgumentException(String.format("Invalid Source Indicator: %s", PartnerIdentifierUtil.sanitize(sourceIndicator)));
        }

        if (sourceIndicator != null && organizationPartIdentifier == null) {
            throw new IllegalArgumentException("Source Indicator requires that the part identifier is specified");
        }
    }

    public static Iso6523 parse(String identifier) {
        Matcher matcher = ISO6523_PATTERN.matcher(identifier);
        if (matcher.matches()) {
            var organizationPartIdentifier = matcher.group("organizationPartIdentifier");
            var sourceIndicator = matcher.group("sourceIndicator");
            if ((organizationPartIdentifier + ":" + sourceIndicator).equals("0:0")) {
                throw new IllegalArgumentException("NHN format");
            }
            return new Iso6523(
                ICD.parse(matcher.group("icd")),
                matcher.group("organizationIdentifier"),
                organizationPartIdentifier,
                sourceIndicator);
        }

        Matcher simpleMatcher = NORWEGIAN_ORGANIZATION_IDENTIFIER_PATTERN.matcher(identifier);

        if (simpleMatcher.matches()) {
            return Iso6523.of(ICD.NO_ORG, identifier);
        }

        throw new IllegalArgumentException(String.format("Invalid ISO6523 value: '%s'", PartnerIdentifierUtil.sanitize(identifier)));
    }

    /// Like [#parse(String)], but for ICD 0192 the organization number must also have a valid modulus 11 check digit.
    /// Use this when validating new input. [#parse(String)] skips the check digit so that existing data can still be read.
    public static Iso6523 parseStrict(String identifier) {
        Iso6523 iso6523 = parse(identifier);
        if (iso6523.getIcd() == ICD.NO_ORG && !hasValidNorwegianCheckDigit(iso6523.getOrganizationIdentifier())) {
            throw new IllegalArgumentException(String.format("Invalid check digit in Norwegian Organization Identifier: %s", iso6523.getOrganizationIdentifier()));
        }
        return iso6523;
    }

    public static boolean isValidStrict(String identifier) {
        return PartnerIdentifierUtil.isValid(identifier, Iso6523::parseStrict);
    }

    private static boolean hasValidNorwegianCheckDigit(String organizationIdentifier) {
        int expected = PartnerIdentifierUtil.modulo11(organizationIdentifier, NORWEGIAN_ORGANIZATION_IDENTIFIER_WEIGHTS);
        return expected == PartnerIdentifierUtil.getDigit(organizationIdentifier, 8);
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
