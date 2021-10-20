package com.jh.webflux.validator;


import com.jh.webflux.exception.CustomResponseException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;
import java.util.stream.Collectors;

public class CustomValidator {

    public static Validator customValidator;
    static {
        LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
        localValidatorFactoryBean.afterPropertiesSet();
        customValidator =localValidatorFactoryBean;
    }

    public static <T> void validate(T target){
        Set<ConstraintViolation<T>> validate = customValidator.validate(target);

        if(!validate.isEmpty()){
            String errorMessage=validate.stream()
                    .map(tConstraintViolation -> tConstraintViolation.getMessage())
                    .collect(Collectors.joining(", "));

            throw new CustomResponseException(HttpStatus.BAD_REQUEST,errorMessage);
        }

    }
}
