package com.example.BankingProject.mapper;

import com.example.BankingProject.annotations.ValidZipCode;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ZipCodeValidator implements ConstraintValidator<ValidZipCode, String> {

    @Override
    public void initialize(ValidZipCode constraintAnnotation) {
    }

    @Override
    public boolean isValid(String zipCode, ConstraintValidatorContext context) {
        return zipCode != null && zipCode.matches("\\d{6}");
    }
}
