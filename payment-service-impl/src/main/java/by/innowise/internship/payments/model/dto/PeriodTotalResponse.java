package by.innowise.internship.payments.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PeriodTotalResponse(
        LocalDateTime from,
        LocalDateTime to,
        BigDecimal total
) {
}
