package by.innowise.internship.payments.service;

import by.innowise.internship.payments.model.dto.PaymentPeriod;
import by.innowise.internship.payments.model.dto.PaymentRequestDto;
import by.innowise.internship.payments.model.dto.PaymentResponseDto;
import by.innowise.internship.payments.model.dto.PeriodTotalResponse;
import by.innowise.internship.payments.model.entity.PaymentStatus;

import java.util.List;
import java.util.UUID;

public interface PaymentService {

    PaymentResponseDto create(PaymentRequestDto createDto, Long userId);

    List<PaymentResponseDto> getAllByUser(Long userId);

    List<PaymentResponseDto> getByUserAndOrder(Long userId, UUID orderId);

    List<PaymentResponseDto> getAllByUserAndStatus(Long userId, PaymentStatus status);

    PeriodTotalResponse calculatePaymentTotalForPeriod(Long userId, PaymentPeriod paymentPeriod);
}
