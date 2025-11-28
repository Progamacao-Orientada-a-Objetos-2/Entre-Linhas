package com.entrelinhas.backend.domain;

public enum TokenType {
    EMAIL_VERIFICATION("email_verification"),
    PASSWORD_RESET("password_reset");

    private final String value;

    TokenType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static TokenType fromValue(String value) {
        for (TokenType type : values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown token type: " + value);
    }
}
