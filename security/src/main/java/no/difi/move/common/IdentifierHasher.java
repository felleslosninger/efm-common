package no.difi.move.common;

import com.google.common.hash.Hashing;
import lombok.experimental.UtilityClass;

import java.nio.charset.StandardCharsets;
import java.util.function.Predicate;
import java.util.regex.Pattern;

@UtilityClass
public class IdentifierHasher {

    private static final Predicate<String> EXACTLY_11_NUMBERS = Pattern.compile("\\d{11}").asPredicate();

    /**
     * Checks if the identifier contains a personnr (11 consecutive digits, also when prefixed, e.g. "0192:" or
     * an NHN prefix). If so, return hash of the whole identifier.
     *
     * @param identifier identifier, may be null
     * @return sha256 hash of identifier if it contains a personnr, else the identifier (null if null)
     */
    public static String hashIfPersonnr(String identifier) {
        if (identifier != null && EXACTLY_11_NUMBERS.test(identifier)) {
            return Hashing.sha256()
                .hashString(identifier, StandardCharsets.UTF_8)
                .toString();
        }
        return identifier;
    }

}
