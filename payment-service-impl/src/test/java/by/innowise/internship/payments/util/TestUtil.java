package by.innowise.internship.payments.util;

import by.innowise.internship.payments.model.dto.PaymentResponseDto;
import by.innowise.internship.payments.model.entity.Payment;
import by.innowise.internship.payments.model.entity.PaymentStatus;
import lombok.experimental.UtilityClass;
import org.bson.types.ObjectId;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@UtilityClass
public class TestUtil {

    public Payment getSuccessPayment(ObjectId id, UUID orderId, Long userId, LocalDate date, BigDecimal amount) {
        LocalDateTime now = LocalDateTime.now();
        return Payment.builder()
                      .id(id)
                      .orderId(orderId)
                      .userId(userId)
                      .status(PaymentStatus.SUCCEED)
                      .timestamp(date.atTime(14, 0))
                      .amount(amount)
                      .createdAt(now)
                      .updatedAt(now)
                      .version(0L)
                      .build();
    }

    public Payment getFailedPayment(ObjectId id, UUID orderId, Long userId, LocalDate date, BigDecimal amount) {
        LocalDateTime now = LocalDateTime.now();
        return Payment.builder()
                      .id(id)
                      .orderId(orderId)
                      .userId(userId)
                      .status(PaymentStatus.FAILED)
                      .timestamp(date.atTime(19, 0))
                      .amount(amount)
                      .createdAt(now)
                      .updatedAt(now)
                      .version(0L)
                      .build();
    }

    public PaymentResponseDto mapToPaymentResponse(Payment e) {
        return new PaymentResponseDto(e.getId().toHexString(),
                                      e.getOrderId(),
                                      e.getUserId(),
                                      e.getStatus(),
                                      e.getTimestamp(),
                                      e.getAmount());
    }

    public LocalDateTime getLocalDateTimeEndOfTheDay(LocalDate date){
        return date.atTime(23, 59, 59, 999_999_999);
    }

}
