package no.difi.meldingsutveksling.domain;

import lombok.Value;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Value
public class Iso6523 implements Serializable {

    private static final Pattern ORGANIZATION_IDENTIFIER_PATTERN = Pattern.compile("^[^\\s:][^:]{1,33}[^\\s:]$");
    private static final Pattern ISO6523_PATTERN = Pattern.compile("^(\\d{4}):([^:]{1,35})(?::([^\\s:]{1,35}))?(?::([^:]?))?$");

    ICD icd;
    String organizationIdentifier;
    String organizationPartIdentifier;
    String sourceIndicator;

    private Iso6523(ICD icd, String organizationIdentifier, String organizationPartIdentifier, String sourceIndicator) {
        this.icd = icd;
        this.organizationIdentifier = organizationIdentifier;
        this.organizationPartIdentifier = organizationPartIdentifier;
        this.sourceIndicator = sourceIndicator;
    }

    public static Iso6523 of(ICD icd, String organizationIdentifier) {
        return of(icd, organizationIdentifier, null, null);
    }

    public static Iso6523 of(ICD icd, String organizationIdentifier, String organizationPartIdentifier, String sourceIndicator) {
        if (!ORGANIZATION_IDENTIFIER_PATTERN.matcher(organizationIdentifier).matches()) {
            throw new IllegalArgumentException(String.format("Invalid Organization Identifier: %s", organizationIdentifier));
        }

        return new Iso6523(icd, organizationIdentifier, organizationPartIdentifier, sourceIndicator);
    }

    public static Iso6523 parse(String in) {
        Matcher matcher = ISO6523_PATTERN.matcher(in);

        if (matcher.matches()) {
            return new Iso6523(ICD.parse(matcher.group(1)), matcher.group(2), matcher.group(3), matcher.group(4));
        }

        throw new IllegalArgumentException(String.format("Invalid ISO6523 value: '%s'", in));
    }

    public static boolean isValid(String icdAndIdentifier) {
        return ISO6523_PATTERN.matcher(icdAndIdentifier).matches();
    }

    @Override
    public String toString() {
        return String.format("%s:%s", icd, organizationIdentifier);
    }
}
