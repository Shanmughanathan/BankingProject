package com.example.BankingProject.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.example.BankingProject.constants.BankingConstants.MAX_LOGIN_ATTEMPT;

@Service
public class Banking {

    @Autowired
    private LogInAttemptService logInAttemptService;

    @Autowired
    private BankAccountCreation bankAccountCreation;
    private static final Logger logger = LoggerFactory.getLogger(Banking.class);

    @Value("${spring.datasource.url}")
    private String dataBaseUrl;
    @Value("${value.query.toCheck.userIsBlocked.byUserName}")
    private String queryToCheckUserIsBlockedByUserName;
    @Value("${value.query.toCheck.userIsBlocked.byCustomerId}")
    private String queryToCheckUserIsBlockedByCustomerId;
    @Value("${value.query.toCheck.userIsBlocked.byPhnNum}")
    private String queryToCheckUserIsBlockedByPhnNum;
    @Value("${value.query.toCheck.userIsBlocked.byEmail}")
    private String queryToCheckUserIsBlockedByEmail;
    @Value("${value.query.to.block.userByUserName}")
    private String queryToBlockUserByUserName;
    @Value("${value.query.to.block.userByCustomerId}")
    private String queryToBlockUserByCustomerId;
    @Value("${value.query.to.block.userByPhnNum}")
    private String queryToBlockUserByPhnNum;
    @Value("${value.query.to.block.userByEmail}")
    private String queryToBlockUserByEmail;
    @Value("${value.query.toGet.passWord.byUserName}")
    private String queryToGetPassWordByUserName;
    @Value("${value.query.toGet.passWord.byCustomerId}")
    private String queryToGetPassWordByCustomerId;
    @Value("${value.query.toGet.passWord.byPhnNum}")
    private String queryToGetPassWordByPhnNum;
    @Value("${value.query.toGet.passWord.byEmail}")
    private String queryToGetPassWordByEmail;


    public Banking(LogInAttemptService logInAttemptService) {
        this.logInAttemptService = logInAttemptService;
    }

    public String logOn(String identifier, String passWord, String identifierType) {
        String finalResponse = "";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            logger.info("Starting an application..");
            logger.info("Trying to login to the application..");
            connection = bankAccountCreation.connectToDB(connection);
            boolean isUserBlocked = checkIfTheUserIsAlreadyBlockedOrNot(connection,identifierType, identifier);
            if (logInAttemptService.isBlocked(identifier) || (isUserBlocked)) {
                logger.warn("User {} account is blocked", identifier);
                blockedTheUser(connection,identifier, identifierType);
                return "Your account is blocked due to maximum number of attempt, " +
                        "So please contact the Bank Customer Support or Visit nearby branch.";
            }
            if (connection != null) {
                boolean isValidCredentials = false;
                String query = getSqlQueryToGetThePassWord(identifierType);
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, identifier);
                resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {

                    isValidCredentials = checkTheCredentials(resultSet, passWord);
                }

                if (isValidCredentials) {
                    logInAttemptService.logInSucceeded(identifier);
                    logger.info("User {} Logged in Successfully", identifier);
                    finalResponse = "Logged in Successfully";
                } else {
                    logInAttemptService.logInFailed(identifier);
                    int remainingAttempts = MAX_LOGIN_ATTEMPT - logInAttemptService.getNumberOfAttempts(identifier);
                    logger.warn("User {} provided invalid credentials", identifier);
                    finalResponse = "Invalid " + identifierType + " or Password.\nRemaining Attempts: " + remainingAttempts;

                }
            }

        } catch (SQLException ex) {
            logger.error("Database error occurred, login failed!", ex);
            finalResponse = "An error occurred during login. Please try again later.";
        } catch (Exception ex) {
            logger.error("Exception occurred, login failed!", ex);
            finalResponse = "An error occurred during login. Please try again later.";
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException ex) {
                logger.error("Failed to close database resources", ex);
            }
        }
        return finalResponse;
    }


    private boolean checkIfTheUserIsAlreadyBlockedOrNot(Connection connection,String identifierType, String identifier) {
        boolean isUserBlocked = false;
        try {
            if (connection != null) {
                String query = getSqlQueryToCheckUserIsBlockedOrNot(identifierType);
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, identifier);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    isUserBlocked = isUserAlreadyBlocked(resultSet);
                }

            }
        } catch (Exception ex) {
            logger.error("SQL Error", ex);
        }
        return isUserBlocked;
    }

    private boolean isUserAlreadyBlocked(ResultSet resultSet) throws SQLException {
        return resultSet.getString("is_blocked").equals("true");
    }

    private void blockedTheUser(Connection connection,String identifier, String identifierType) {
        try {
            if (connection != null) {
                String query = getSqlQueryToBlockTheUser(identifierType);
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, "true");
                preparedStatement.setString(2, identifier);
                preparedStatement.executeUpdate();
            }
        } catch (Exception ex) {
            logger.error("SQL Error", ex);
        }
    }

    private boolean checkTheCredentials(ResultSet resultSet, String password) throws SQLException {

        return resultSet.getString("PassWord").equals(password);

    }

    private String getSqlQueryToGetThePassWord(String identifierType) {
        return switch (identifierType) {
            case "userName" -> queryToGetPassWordByUserName;
            case "customerId" -> queryToGetPassWordByCustomerId;
            case "phoneNumber" -> queryToGetPassWordByPhnNum;
            case "email" -> queryToGetPassWordByEmail;
            default -> throw new IllegalArgumentException("Unknown identifier type");
        };
    }

    private String getSqlQueryToCheckUserIsBlockedOrNot(String identifierType) {
        return switch (identifierType) {
            case "userName" -> queryToCheckUserIsBlockedByUserName;
            case "customerId" -> queryToCheckUserIsBlockedByCustomerId;
            case "phoneNumber" -> queryToCheckUserIsBlockedByPhnNum;
            case "email" -> queryToCheckUserIsBlockedByEmail;
            default -> throw new IllegalArgumentException("Unknown identifier type");
        };
    }

    private String getSqlQueryToBlockTheUser(String identifierType) {
        return switch (identifierType) {
            case "userName" -> queryToBlockUserByUserName;
            case "customerId" -> queryToBlockUserByCustomerId;
            case "phoneNumber" -> queryToBlockUserByPhnNum;
            case "email" -> queryToBlockUserByEmail;
            default -> throw new IllegalArgumentException("Unknown identifier type");
        };
    }
}
