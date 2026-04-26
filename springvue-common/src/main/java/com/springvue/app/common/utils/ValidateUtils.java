package com.springvue.app.common.utils;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;

/**
 * 校验工具类
 */
public class ValidateUtils {

    /**
     * 实体校验
     * @param t
     * @param <T>
     */
    public static <T> void entity(T t) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(t);
        constraintViolations.forEach(error -> {
            throw new IllegalArgumentException(error.getMessage());
        });
    }
}
