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
     * Checks if the identifier is a personnr (11 digits). If so, return hash of given identifier.
     *
     * @param identifier identifier
     * @return sha256 hash of identifier if personnr, else the identifier
     */
    public static String hashIfPersonnr(String identifier) {
        if (EXACTLY_11_NUMBERS.test(identifier)) {
            return Hashing.sha256()
                .hashString(identifier, StandardCharsets.UTF_8)
                .toString();
        }
        return identifier;
    }

}
