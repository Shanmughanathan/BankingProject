package com.example.BankingProject.response;

import java.util.List;

public class ErrorResponse {

    private String message;
    private List<String> errorDetails;

    public ErrorResponse(String message, List<String> errorDetails){
        this.message = message;
        this.errorDetails=errorDetails;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getErrorDetails() {
        return errorDetails;
    }

    public void setErrorDetails(List<String> errorDetails) {
        this.errorDetails = errorDetails;
    }
}
