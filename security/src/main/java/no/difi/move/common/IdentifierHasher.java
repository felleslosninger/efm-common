package no.difi.move.common;

import lombok.experimental.UtilityClass;
import org.apache.commons.codec.digest.DigestUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.function.Predicate;
import java.util.regex.Pattern;

@UtilityClass
public class IdentifierHasher {

    private static final Predicate<String> EXACTLY_11_NUMBERS = Pattern.compile("\\d{11}").asPredicate();
    private static final MessageDigest SHA_256;

    static {
        try {
            SHA_256 = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new GetSha256MessageDigestException("Couldn't get SHA-256 message digiest", e);
        }
    }

    public static class GetSha256MessageDigestException extends RuntimeException {

        GetSha256MessageDigestException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * Checks if the identifier is a personnr (11 digits). If so, return md5 hash of given identifier.
     *
     * @param identifier identifier
     * @return md5 hash if personnr, else identifier
     */
    public static String hashIfPersonnr(String identifier) {

        if (EXACTLY_11_NUMBERS.test(identifier)) {
            return DigestUtils.sha256Hex(identifier);
        }

        return identifier;
    }
}
