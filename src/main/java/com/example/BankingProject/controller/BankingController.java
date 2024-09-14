package com.example.BankingProject.controller;

import com.example.BankingProject.dto.BankAccountCreationDto;
import com.example.BankingProject.dto.BankingDto;
import com.example.BankingProject.mapper.IdentifierUtil;
import com.example.BankingProject.response.SuccessFulResponse;
import com.example.BankingProject.service.BankAccountCreation;
import com.example.BankingProject.service.Banking;
import com.example.BankingProject.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@Validated
public class BankingController {

    private final Banking bankingService;
    private final ProfileService profileService;
    private final BankAccountCreation bankAccountCreation;
    private final IdentifierUtil identifierUtil;

    public BankingController(Banking bankingService, ProfileService profileService, BankAccountCreation bankAccountCreation,
                             IdentifierUtil identifierUtil) {
        this.bankingService = bankingService;
        this.profileService = profileService;
        this.bankAccountCreation = bankAccountCreation;
        this.identifierUtil = identifierUtil;
    }
    @CrossOrigin(origins = "http://localhost:63342")
    @PostMapping("/signUp")
    public SuccessFulResponse signUp(@Valid @RequestBody BankAccountCreationDto accountCreationDto) {
        String message = bankAccountCreation.createAccount(accountCreationDto);
        String customerId = extractTheMessage(message);
        String userName = null;
        boolean accountCreationStatus = false;
        if (customerId != null) {
            accountCreationStatus = Boolean.parseBoolean(customerId.split("<>")[1].split("<STATUS>")[1].trim());
            message = customerId.split("<>")[1].split("<STATUS>")[0].trim();
            userName = customerId.split("<>")[0].split(":")[1].trim();
            customerId = customerId.split("<>")[0].split(":")[0].trim();
        }
        return new SuccessFulResponse(message, userName,customerId, accountCreationStatus);
    }
    @CrossOrigin(origins = "http://localhost:63342")
    @PostMapping("/logIn")
    public SuccessFulResponse logIn(@Valid @RequestBody BankingDto bankingDto) {
        String identification = identifierUtil.identifier(bankingDto);
        String identifier = identification.split(" ; ")[0];
        String identifierType = identification.split(" ; ")[1];
        String message = bankingService.logOn(identifier, bankingDto.getPassword(), identifierType);
        boolean loginStatus = message.contains("Logged in Successfully");
        return new SuccessFulResponse(message, identifier,null, loginStatus);
    }

    @GetMapping("/viewProfile/{id}")
    public ResponseEntity<BankAccountCreationDto> viewProfile(@PathVariable int id) {
        Optional<BankAccountCreationDto> profile = profileService.getProfileById(id);
        return profile.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    private String extractTheMessage(String responseMessage) {

        String prefix = "Your customer Id is :";
        if (responseMessage.contains(prefix)) {
            String accountCreationStatus = "true";
            int index = responseMessage.indexOf(prefix);
            String accountMessage = responseMessage.substring(0,index);
            return responseMessage.substring(index + prefix.length()).trim().split(" ")[0] +
                    "<>" + accountMessage + "<STATUS>"+accountCreationStatus;
        }
        return null;
    }
}

