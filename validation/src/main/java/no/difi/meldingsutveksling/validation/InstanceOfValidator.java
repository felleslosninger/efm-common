package no.difi.meldingsutveksling.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class InstanceOfValidator implements ConstraintValidator<InstanceOf, Object> {

    private Class<?> expectedClass;

    @Override
    public void initialize(InstanceOf constraint) {
        this.expectedClass = constraint.value();
    }

    public boolean isValid(Object object, ConstraintValidatorContext context) {
        return object == null || expectedClass.isInstance(object);
    }
}
