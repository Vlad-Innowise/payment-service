package by.innowise.internship.payments.service;

import by.innowise.internship.payments.model.dto.PaymentRequestDto;
import by.innowise.internship.payments.model.dto.PaymentResponseDto;

public interface PaymentService {

    PaymentResponseDto create(PaymentRequestDto createDto, Long userId);

}
