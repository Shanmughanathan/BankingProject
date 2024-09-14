package com.example.BankingProject.response;

public class SuccessFulResponse {

    private String message;
    private String userName;
    private String customerId;
    private boolean status;

    public SuccessFulResponse(String message, String userName, String customerId, boolean status) {
        this.message = message;
        this.userName = userName;
        this.customerId = customerId;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
