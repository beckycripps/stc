package org.example.userapi.service;

public class ValidationResult {

    private boolean isValid;
    private String errorMessages;

    public ValidationResult(boolean isValid, String errorMessages) {
        this.isValid = isValid;
        this.errorMessages = errorMessages;
    }

    public ValidationResult() {
        this.isValid = false;
        this.errorMessages = "";
    }

    public boolean isValid() {
        return isValid;
    }

    public String getErrorMessages() {
        return errorMessages;
    }

    public boolean setValid(boolean b) {
        this.isValid = b;
        return isValid;
    }
}
