package by.innowise.internship.payments.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

public record PaymentPeriod(

        @NotNull(message = "Payment period start can't be null")
        @PastOrPresent(message = "Payment period start can't be future dated!")
        LocalDate from,

        @NotNull(message = "Payment period end can't be null")
        @PastOrPresent(message = "Payment period end can't be future dated!")
        LocalDate to

) {
}
