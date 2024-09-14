package com.example.BankingProject.mapper;

import com.example.BankingProject.dto.BankingDto;
import org.springframework.stereotype.Component;

@Component
public class IdentifierUtil {

    public String identifier(BankingDto bankingDto) {
        String identifier = "";
        String identifierType = "";
        if (bankingDto.getUserName() != null) {
            identifier = bankingDto.getUserName();
            identifierType = "userName";
        } else if (bankingDto.getCustomerId() != 0) {
            identifier = String.valueOf(bankingDto.getCustomerId());
            identifierType = "customerId";
        } else if (bankingDto.getPhoneNumber() != null) {
            identifier = bankingDto.getPhoneNumber();
            identifierType = "phoneNumber";
        } else if (bankingDto.getEmail() != null) {
            identifier = bankingDto.getEmail();
            identifierType = "email";
        }

        if (identifier == null) {
            return "No identifier provided.";
        }
        return identifier + " ; " + identifierType;
    }
}
