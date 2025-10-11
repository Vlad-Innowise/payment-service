package by.innowise.internship.payments.service.impl;

import by.innowise.internship.payments.model.entity.PaymentStatus;
import by.innowise.internship.payments.service.PaymentProcessorService;
import by.innowise.internship.payments.service.RandomIntService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentProcessorServiceImpl implements PaymentProcessorService {

    private final RandomIntService randomIntService;

    @Override
    public PaymentStatus processPayment() {
        return isPaymentSucceed() ? PaymentStatus.SUCCEED : PaymentStatus.FAILED;
    }

    private boolean isPaymentSucceed() {
        return randomIntService.getRandomInt() % 2 == 0;
    }

}
