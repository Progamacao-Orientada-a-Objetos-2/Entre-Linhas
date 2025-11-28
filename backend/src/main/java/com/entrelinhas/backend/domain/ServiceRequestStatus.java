package com.entrelinhas.backend.domain;

public enum ServiceRequestStatus {
    PENDING("pending"),
    ACCEPTED("accepted"),
    REJECTED("rejected");

    private final String value;

    ServiceRequestStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public boolean isFinalState() {
        return this == ACCEPTED || this == REJECTED;
    }

    public static ServiceRequestStatus fromValue(String value) {
        for (ServiceRequestStatus status : values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown service request status: " + value);
    }
}
