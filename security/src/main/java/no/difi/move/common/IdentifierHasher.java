package no.difi.move.common;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;

import java.util.function.Predicate;
import java.util.regex.Pattern;

public class IdentifierHasher {

    private static final Predicate<String> exactly_11_numbers = Pattern.compile(String.format("\\d{%d}", 11)).asPredicate();

    private IdentifierHasher() {
    }

    /**
     * Checks if the identifier is a personnr (11 digits). If so, return md5 hash of given identifier.
     * @param identifier identifier
     * @return md5 hash if personnr, else identifier
     */
    public static String hashIfPersonnr(String identifier) {

        if (exactly_11_numbers.test(identifier)) {
            return Hashing.md5().hashString(identifier, Charsets.UTF_8).toString();
        }

        return identifier;
    }
}
