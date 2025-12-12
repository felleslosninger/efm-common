package no.difi.meldingsutveksling.domain;

import lombok.Value;

import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

import static no.difi.meldingsutveksling.domain.sbdh.Authority.ISO6523_ACTORID_UPIS;

@Value
public class PersonIdentifier implements PartnerIdentifier {

    private static final Pattern SSN_PATTERN = Pattern.compile("^[01234567]\\d[014589]\\d{8}$");
    private static final int[] WEIGHTS1 = {3, 7, 6, 1, 8, 9, 4, 5, 2};
    private static final int[] WEIGHTS2 = {5, 4, 3, 2, 7, 6, 5, 4, 3, 2};
    private static final int MAX_AGE_IN_DAYS = 365 * 110;

    public static PersonIdentifier parse(String identifier) {
        if (!SSN_PATTERN.matcher(identifier).matches()) {
            throw new IllegalArgumentException(String.format("Invalid SSN='%s'", identifier));
        }

        validateCheckDigit(identifier, 9, WEIGHTS1);
        validateCheckDigit(identifier, 10, WEIGHTS2);

        return new PersonIdentifier(identifier);
    }

    private static void validateCheckDigit(String identifier, int index, int[] weights) {
        int expected = PartnerIdentifierUtil.modulo11(identifier, weights);
        int actual = PartnerIdentifierUtil.getDigit(identifier, index);

        if (expected != actual) {
            throw new IllegalArgumentException(String.format("Invalid check digit. Expected %d, but was %d", expected, actual));
        }
    }

    public static PersonIdentifier parseQualifiedIdentifier(String identifier) {
        return PersonIdentifier.parse(PartnerIdentifierUtil.getIdentifier(identifier, ISO6523_ACTORID_UPIS));
    }

    public static boolean isValid(String identifier) {
        return PartnerIdentifierUtil.isValid(identifier, PersonIdentifier::parse);
    }

    public static boolean isValidQualifiedIdentifier(String identifier) {
        return PartnerIdentifierUtil.isValid(identifier, PersonIdentifier::parseQualifiedIdentifier);
    }

    public static PersonIdentifier random() {
        Gender[] genders = Gender.values();
        return random(genders[ThreadLocalRandom.current().nextInt(0, genders.length)]);
    }

    public static PersonIdentifier random(Gender gender) {
        LocalDate dateOfBirth = LocalDate.now().minusDays(ThreadLocalRandom.current().nextInt(0, MAX_AGE_IN_DAYS));
        return random(dateOfBirth, gender);
    }

    public static PersonIdentifier random(LocalDate dateOfBirth, Gender gender) {
        String identifier = null;

        while (identifier == null) {
            identifier = getRandomIdentifier(dateOfBirth, gender);
        }

        return PersonIdentifier.parse(identifier);
    }

    private static String getRandomIdentifier(LocalDate dateOfBirth, Gender gender) {
        String identifierWithoutCheckDigits = String.format("%s%03d",
                dateOfBirth.format(DateTimeFormatter.ofPattern("ddMMyy")),
                getRandomIndividualNumber(dateOfBirth, gender));

        int checkDigit1 = PartnerIdentifierUtil.modulo11(identifierWithoutCheckDigits, WEIGHTS1);

        if (checkDigit1 == 10) {
            return null;
        }

        int checkDigit2 = PartnerIdentifierUtil.modulo11(String.format("%s%d", identifierWithoutCheckDigits, checkDigit1), WEIGHTS2);

        if (checkDigit2 == 10) {
            return null;
        }

        return String.format("%s%d%d", identifierWithoutCheckDigits, checkDigit1, checkDigit2);
    }

    private static int getRandomIndividualNumber(LocalDate dateOfBirth, Gender gender) {
        int randomIndividualNumber = getRandomIndividualNumber(dateOfBirth);
        if (randomIndividualNumber % 2 == 1) --randomIndividualNumber;
        return randomIndividualNumber + ((gender == Gender.MALE) ? 1 : 0);
    }

    private static int getRandomIndividualNumber(LocalDate dateOfBirth) {
        int year = dateOfBirth.getYear();

        if (year >= 2000) {
            return ThreadLocalRandom.current().nextInt(500, 1000);
        }

        if (year < 1900) {
            return ThreadLocalRandom.current().nextInt(500, 750);
        }

        if (year >= 1940) {
            int x = ThreadLocalRandom.current().nextInt(0, 600);
            return x < 500 ? x : x + 400;
        }

        return ThreadLocalRandom.current().nextInt(0, 500);
    }

    String identifier;

    @Override
    public String getIdentifier() {
        return identifier;
    }

    public LocalDate getDateOfBirth() {
        return LocalDate.of(getYear(), getMonth(), getDayOfMonth());
    }

    public int getAge(Clock clock) {
        return (int) ChronoUnit.YEARS.between(getDateOfBirth(), LocalDate.now(clock));
    }

    private int getYear() {
        int individualNumber = Integer.parseUnsignedInt(getIndividualNumber());
        int birthYear = Integer.parseUnsignedInt(get2DigitBirthYear());

        if (individualNumber <= 499) {
            return 1900 + birthYear;
        }

        if (birthYear <= 39) {
            return 2000 + birthYear;
        }

        if (individualNumber <= 749) {
            return 1800 + birthYear;
        }

        return 1900 + birthYear;
    }

    private int getMonth() {
        return Integer.parseUnsignedInt(identifier.substring(2, 4)) % 40;
    }

    private int getDayOfMonth() {
        return Integer.parseUnsignedInt(identifier.substring(0, 2)) % 40;
    }

    private String get2DigitBirthYear() {
        return identifier.substring(4, 6);
    }

    public String getPersonNumber() {
        return identifier.substring(6);
    }

    public String getIndividualNumber() {
        return identifier.substring(6, 9);
    }

    private int getFirstDigit() {
        return getDigit(0);
    }

    private int getThirdDigit() {
        return getDigit(2);
    }

    private int getGenderDigit() {
        return getDigit(8);
    }

    private int getDigit(int index) {
        return identifier.charAt(index) - '0';
    }

    public boolean isMale() {
        return getGenderDigit() % 2 != 0;
    }

    public boolean isFemale() {
        return !isMale();
    }

    public boolean isDNumber() {
        int firstDigit = getFirstDigit();
        return firstDigit >= 4 && firstDigit <= 7;
    }

    public boolean isHNumber() {
        int thirdDigit = getThirdDigit();
        return thirdDigit >= 4 && thirdDigit <= 7;
    }

    public boolean isSynthetic() {
        int thirdDigit = getThirdDigit();
        return thirdDigit >= 8;
    }

    public Gender getGender() {
        if (isFemale()) {
            return Gender.FEMALE;
        } else {
            return Gender.MALE;
        }
    }

    @Override
    public String toString() {
        return getIdentifier();
    }
}
