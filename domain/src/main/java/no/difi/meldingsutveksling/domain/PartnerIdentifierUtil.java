package no.difi.meldingsutveksling.domain;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PartnerIdentifierUtil {

    private static final Pattern QUALIFIED_IDENTIFIER_PATTERN = Pattern.compile("^(?<authority>.+)::(?<identifier>.*)");

    private PartnerIdentifierUtil() {
        // Utility class
    }

    static int modulo11(String s, int[] weights) {
        int k = 11 - summarize(s, weights) % 11;
        return k == 11 ? 0 : k;
    }

    static int summarize(String s, int[] weights) {
        int sum = 0;

        for (int i = 0; i < weights.length; ++i) {
            sum += weights[i] * getDigit(s, i);
        }

        return sum;
    }

    static int getDigit(String s, int index) {
        return s.charAt(index) - '0';
    }

    static String getIdentifier(String qualifiedIdentifier, String expectedAuthority) {
        Matcher matcher = QUALIFIED_IDENTIFIER_PATTERN.matcher(qualifiedIdentifier);

        if (!matcher.matches()) {
            throw new IllegalArgumentException(String.format("Invalid qualified identifier = '%s'. Expected <authoriyy>::<identifier>", qualifiedIdentifier));
        }

        String authority = matcher.group("authority");
        String identifier = matcher.group("identifier");

        if (authority.equals(expectedAuthority)) {
            return identifier;
        }

        throw new IllegalArgumentException(String.format("Invalid authority='%s', expected '%s'", authority, expectedAuthority));
    }

    static boolean isValid(String identifier, Consumer<String> parser) {
        try {
            parser.accept(identifier);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    static <T> T parse(String identifier, Collection<Function<String, T>> parsers) {
        List<IllegalArgumentException> exceptionList = new ArrayList<>();

        for (Function<String, T> parser : parsers) {
            try {
                return parser.apply(identifier);
            } catch (IllegalArgumentException e) {
                exceptionList.add(e);
            }
        }

        IllegalArgumentException exception = new IllegalArgumentException(String.format("Unable to parse identifier = '%s'", identifier));
        exceptionList.forEach(exception::addSuppressed);
        throw exception;
    }
}
