package com.example.BankingProject.config;


import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class MessagingConfig {

    @Value("${indicator.enable.messaging.option}")
    private boolean enableMessaging;

    @Value("${value.account.sid}")
    private String accountSid;

    @Value("${value.auth.token}")
    private String authToken;

    @Value("${value.twilio.phoneNumber}")
    private String phoneNumber;



}
