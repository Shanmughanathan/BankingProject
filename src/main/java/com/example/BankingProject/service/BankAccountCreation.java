package com.example.BankingProject.service;

import com.example.BankingProject.config.MessagingConfig;
import com.example.BankingProject.dto.BankAccountCreationDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Random;

import static com.example.BankingProject.constants.BankingConstants.MAX_RANDOM_INT;
import static com.example.BankingProject.constants.BankingConstants.MIN_RANDOM_INT;

@Service
public class BankAccountCreation {

    @Autowired
    private MessagingService messagingService;
    private final MessagingConfig messagingConfig;
    private static final Logger logger = LoggerFactory.getLogger(BankAccountCreation.class);
    @Value("${spring.datasource.url}")
    private String dataBaseUrl;
    @Value("${value.query.to.insertValue}")
    private String insertValueQuery;
    @Value("${value.query.toCheck.user.alreadyExist}")
    private String queryToCheckUserExistOrNot;

    public BankAccountCreation(MessagingService messagingService, MessagingConfig messagingConfig) {
        this.messagingService = messagingService;
        this.messagingConfig = messagingConfig;
    }

    public String createAccount(BankAccountCreationDto bankAccountCreationDto) {
        String finalResponse = "";
        Connection connection = null;
        try {
            boolean isMatch = checkWhetherPassWordAndConfirmPassWordAreMatch(bankAccountCreationDto);
            if (isMatch) {
                logger.info("Starting of an application");
                connection = connectToDB(connection);
                if (connection != null) {
                    int customerId = setRandomInt();
                    boolean isUserExist = signUp(connection, bankAccountCreationDto, customerId);
                    if (isUserExist) {
                        logger.info("Given mobile number and email address has been already used.");
                        finalResponse = "Given Mobile number or Email Address has been already used by another account.\nPlease try to " +
                                "create an account with other credentials";
                    } else {
                        String accountCreationMessage = " Your bank account has been created." +
                                "\n Thank you for banking with us. Your customer Id is : " + customerId+":"+bankAccountCreationDto.getUserName();
                        if (messagingConfig.isEnableMessaging()) {
                            boolean isMessageSent = sendMessageToTheCustomer(bankAccountCreationDto.getPhoneNumber(), accountCreationMessage);
                            if (isMessageSent) {
                                finalResponse = " Your bank account has been created." +
                                        "\n Thank you for banking with us. Your customer Id is : " + customerId +":"+bankAccountCreationDto.getUserName() + "\nYour Customer Id " +
                                        "is sent to your registered mobile number and email address. Please Login With Your Credentials.";
                            } else {
                                finalResponse = " Your bank account has been created." +
                                        "\n Thank you for banking with us. Your customer Id is : " + customerId +":"+bankAccountCreationDto.getUserName()+ "\n But we couldn't " +
                                        "able to sent the " + "customer Id to your registered mobile number.\n Please visit nearby branch " +
                                        "to resolve this issue.";
                            }
                        } else {
                            finalResponse = " Your bank account has been created." +
                                    "\n Thank you for banking with us. Your customer Id is : " + customerId + ":"+bankAccountCreationDto.getUserName();
                        }
                    }

                } else {
                    logger.error("DB Failed");
                }
            } else {
                finalResponse = "PassWord Mismatch. Given Password and ConfirmPassword are not matched. Please try again";
                logger.error("PassWord mismatch for the user {}", bankAccountCreationDto.getUserName());
            }

        } catch (Exception ex) {
            logger.error("Account Creation Failed", ex);
        }

        return finalResponse;
    }

    private boolean sendMessageToTheCustomer(String phoneNumber, String accountCreationMessage) {
        boolean isSmsSend = false;
        try {
            messagingService.sendSMS(phoneNumber, accountCreationMessage);
            isSmsSend = true;
        } catch (Exception ex) {
            logger.error("SMS failed to send", ex);
        }
        return isSmsSend;
    }

    private boolean signUp(Connection connection, BankAccountCreationDto bankAccountCreationDto, int customerId) {
        boolean isAccountAlreadyExist = false;

        try {
            isAccountAlreadyExist = checkWhetherTheUserHasExistingAccountOrNot(connection,bankAccountCreationDto);
            PreparedStatement preparedStatement = connection.prepareStatement(insertValueQuery);
            if (!isAccountAlreadyExist) {
                preparedStatement.setInt(1, customerId);
                preparedStatement.setString(2, bankAccountCreationDto.getUserName());
                preparedStatement.setString(3, bankAccountCreationDto.getFirstName());
                preparedStatement.setString(4, bankAccountCreationDto.getLastName());
                preparedStatement.setString(5, bankAccountCreationDto.getEmail());
                preparedStatement.setString(6, bankAccountCreationDto.getPhoneNumber());
                preparedStatement.setString(7, bankAccountCreationDto.getAddress());
                preparedStatement.setString(8, bankAccountCreationDto.getCity());
                preparedStatement.setString(9, bankAccountCreationDto.getState());
                preparedStatement.setString(10, bankAccountCreationDto.getZipCode());
                preparedStatement.setString(11, bankAccountCreationDto.getPassWord());
                preparedStatement.setString(12, bankAccountCreationDto.getConfirmPassWord());
                preparedStatement.setString(13,bankAccountCreationDto.getCountryCode());
                preparedStatement.executeUpdate();
                logger.info("Account Created Successfully for the UserName {}", bankAccountCreationDto.getUserName());
            }
        } catch (Exception ex) {
            logger.error("Error occurs, when try to insert a value in to DB", ex);
        }
        return isAccountAlreadyExist;

    }

    private boolean checkWhetherTheUserHasExistingAccountOrNot(Connection connection,BankAccountCreationDto bankAccountCreationDto) {
        boolean accountExist = false;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(queryToCheckUserExistOrNot);
            preparedStatement.setString(1, bankAccountCreationDto.getEmail());
            preparedStatement.setString(2, bankAccountCreationDto.getPhoneNumber());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int existingUserCount = resultSet.getInt(1);
                if(existingUserCount >0)
                    accountExist = true;
            }
        } catch (Exception ex) {
            logger.error("Account creation failed", ex);
        }

        return accountExist;
    }

    private boolean checkWhetherPassWordAndConfirmPassWordAreMatch(BankAccountCreationDto bankAccountCreationDto) {
        return bankAccountCreationDto.getPassWord().equals(bankAccountCreationDto.getConfirmPassWord());
    }

    private int setRandomInt() {
        Random random = new Random();
        return random.nextInt((MAX_RANDOM_INT - MIN_RANDOM_INT) + 1) + MIN_RANDOM_INT;
    }

    public Connection connectToDB(Connection connection) {
        try {
            connection = DriverManager.getConnection(dataBaseUrl);
            logger.info("DB Connected Successfully");

        } catch (Exception ex) {
            logger.error("DB Connection Failed", ex);
        }
        return connection;

    }

}
