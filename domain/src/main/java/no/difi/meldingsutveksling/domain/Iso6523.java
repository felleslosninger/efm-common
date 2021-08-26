package no.difi.meldingsutveksling.domain;

import lombok.Value;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Value
public class Iso6523 implements Serializable {

    private static final Pattern ICD_PATTERN = Pattern.compile("^\\d{4}$");
    private static final Pattern ORGANIZATION_IDENTIFIER_PATTERN = Pattern.compile("^[^\\s:][^:]{1,33}[^\\s:]$");
    private static final Pattern ISO6523_PATTERN = Pattern.compile("^(\\d{4}):([^\\s:][^:]{1,33}[^\\s:])$");

    ICD icd;
    String organizationIdentifier;

    private Iso6523(ICD icd, String organizationIdentifier) {
        this.icd = icd;
        this.organizationIdentifier = organizationIdentifier;
    }

    public static Iso6523 of(ICD icd, String organizationIdentifier) {
        if (!ORGANIZATION_IDENTIFIER_PATTERN.matcher(organizationIdentifier).matches()) {
            throw new IllegalArgumentException(String.format("Invalid Organization Identifier: %s", organizationIdentifier));
        }

        return new Iso6523(icd, organizationIdentifier);
    }

    public static Iso6523 parse(String icdAndIdentifier) {
        Matcher matcher = ISO6523_PATTERN.matcher(icdAndIdentifier);

        if (matcher.matches()) {
            return new Iso6523(ICD.parse(matcher.group(1)), matcher.group(2));
        }

        throw new IllegalArgumentException(String.format("Invalid ISO6523 value: '%s'", icdAndIdentifier));
    }

    public static boolean isValid(String icdAndIdentifier) {
        return ISO6523_PATTERN.matcher(icdAndIdentifier).matches();
    }

    @Override
    public String toString() {
        return String.format("%s:%s", icd, organizationIdentifier);
    }
}
