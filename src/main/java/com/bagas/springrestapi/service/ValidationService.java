package com.bagas.springrestapi.service;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ValidationService {

    @Autowired
    private Validator validator;

    public void validate(Object request){
        Set<ConstraintViolation<Object>> constraintValidations = validator.validate(request);
        if (constraintValidations.size() !=0){
            throw new ConstraintViolationException(constraintValidations);
        }
    }
}
