package no.difi.meldingsutveksling.domain;

import lombok.Value;
import lombok.With;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Value
public class Iso6523 implements Serializable {

    private static final Pattern ORGANIZATION_IDENTIFIER_PATTERN = Pattern.compile("^[^\\s:][^:]{1,33}[^\\s:]$");
    private static final Pattern ORGANIZATION_PART_IDENTIFIER_PATTERN = Pattern.compile("^[^\\s:][^:]{1,33}[^\\s:]$");
    private static final Pattern SOURCE_INDICATOR = Pattern.compile("^\\d$");
    private static final Pattern ISO6523_PATTERN = Pattern.compile("^(\\d{4}):([^:]{1,35})(?::([^\\s:]{1,35}))?(?::(\\d))?$");

    @With ICD icd;
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
            return new Iso6523(ICD.parse(matcher.group(1)), matcher.group(2), matcher.group(3), matcher.group(4));
        }

        throw new IllegalArgumentException(String.format("Invalid ISO6523 value: '%s'", identifier));
    }

    public static boolean isValid(String inidentifier) {
        return ISO6523_PATTERN.matcher(inidentifier).matches();
    }

    public boolean hasOrganizationPartIdentifier() {
        return organizationPartIdentifier != null;
    }

    public boolean hasSourceIndicator() {
        return sourceIndicator != null;
    }

    public Iso6523 toMainOrganization() {
        return Iso6523.of(icd, organizationIdentifier);
    }

    public String urlEncode() {
        try {
            return URLEncoder.encode(this.toString(), StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("UTF-8 encoding not supported", e);
        }
    }

    @Override
    public String toString() {
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
}