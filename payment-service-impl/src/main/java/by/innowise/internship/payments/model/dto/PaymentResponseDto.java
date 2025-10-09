package by.innowise.internship.payments.model.dto;

import by.innowise.internship.payments.model.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentResponseDto(

        String id,
        UUID orderId,
        Long userId,
        PaymentStatus status,
        LocalDateTime timestamp,
        BigDecimal amount
) {
}
