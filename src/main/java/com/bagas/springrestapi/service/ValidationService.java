package com.bagas.springrestapi.service;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    public void dateBirthValidation(String dateBefore, String dateAfter) throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String todayString = sdf.format(new Date());

        Date earlierDate = sdf.parse(dateBefore);
        Date laterDate = sdf.parse(dateAfter);
        Date todayDate = sdf.parse(todayString);

        if (earlierDate.equals(todayDate)||earlierDate.after(todayDate)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Date birth must be in past");
        }

        if (earlierDate.after(laterDate)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Date birth must before Date hire");
        }

    }

    public void dateValidation(String dateBefore, String dateAfter) throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Date earlierDate = sdf.parse(dateBefore);
        Date laterDate = sdf.parse(dateAfter);

        if (earlierDate.after(laterDate)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"From Date must before To Date");
        }

    }
}
