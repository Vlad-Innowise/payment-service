package by.innowise.internship.payments.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentRequestDto(

        @NotNull(message = "Order id can't be null")
        UUID orderId,

        @NotNull(message = "Payment amount can't be null")
        @Positive(message = "Payment amount should be positive")
        BigDecimal amount
) {
}
