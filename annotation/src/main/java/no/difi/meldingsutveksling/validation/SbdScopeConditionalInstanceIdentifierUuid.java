package no.difi.meldingsutveksling.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = {})
@Target({TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
public @interface SbdScopeConditionalInstanceIdentifierUuid {

    String message() default "{no.difi.meldingsutveksling.validation.UUID}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
