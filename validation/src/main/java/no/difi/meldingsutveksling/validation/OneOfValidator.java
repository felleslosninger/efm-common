package no.difi.meldingsutveksling.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class OneOfValidator implements ConstraintValidator<OneOf, String> {

    private Set<String> acceptedValues;

    @Override
    public void initialize(OneOf constraint) {
        this.acceptedValues = new HashSet<>(Arrays.asList(constraint.value()));
    }

    public boolean isValid(String s, ConstraintValidatorContext context) {
        return s == null || acceptedValues.contains(s);
    }
}
