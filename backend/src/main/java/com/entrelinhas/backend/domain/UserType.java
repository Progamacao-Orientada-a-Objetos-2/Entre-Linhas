package com.entrelinhas.backend.domain;

public enum UserType {
    FACCIONISTA("faccionista"),
    FACTION("faction"),
    COMPANY("company");

    private final String value;

    UserType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static UserType fromValue(String value) {
        for (UserType type : values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown user type: " + value);
    }
}
