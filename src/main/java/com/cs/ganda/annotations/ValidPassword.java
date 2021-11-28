package com.cs.ganda.annotations;


import com.cs.ganda.validators.PasswordValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordValidator.class)
@Documented
public @interface ValidPassword {
    String message() default "Le mot passe doit contenir au moins 6 caratères une majuscue et deux chiffres";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
