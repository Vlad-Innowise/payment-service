package by.innowise.internship.payments.model.entity;

public enum PaymentStatus {

    SUCCEED,
    FAILED;

    public static PaymentStatus fromString(String value) {
        for (PaymentStatus status : PaymentStatus.values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown status provided: %s".formatted(value));
    }

}
