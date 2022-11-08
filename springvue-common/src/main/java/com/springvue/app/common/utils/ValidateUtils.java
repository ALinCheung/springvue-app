package com.springvue.app.common.utils;

import com.springvue.app.common.exception.BusinessException;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
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
            throw new BusinessException(error.getMessage());
        });
    }
}
