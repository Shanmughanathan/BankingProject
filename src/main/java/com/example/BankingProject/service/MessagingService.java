package com.example.BankingProject.service;

import com.example.BankingProject.config.MessagingConfig;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessagingService {

    @Autowired
    private MessagingConfig messagingConfig;

    private static final Logger logger = LoggerFactory.getLogger(MessagingService.class);

    public MessagingService(MessagingConfig messagingConfig) {
        this.messagingConfig = messagingConfig;
    }

    @PostConstruct
    public void init() {
        Twilio.init(messagingConfig.getAccountSid(), messagingConfig.getAuthToken());
    }

    public void sendSMS(String receiverPhoneNumber,String message){

        Message.creator(
                new PhoneNumber(receiverPhoneNumber),
                new PhoneNumber(messagingConfig.getPhoneNumber()),
                message
        ).create();
    }
}
