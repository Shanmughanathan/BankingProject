package com.example.BankingProject.mapper;

import com.example.BankingProject.annotations.ValidCountryCode;
import com.example.BankingProject.annotations.ValidZipCode;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class CountryCodeValidator implements ConstraintValidator<ValidCountryCode, String> {
    @Override
    public void initialize(ValidCountryCode constraintAnnotation) {
    }

    @Override
    public boolean isValid(String countryCode, ConstraintValidatorContext context) {
        return countryCode != null && countryCode.matches("\\+91");
    }
}
