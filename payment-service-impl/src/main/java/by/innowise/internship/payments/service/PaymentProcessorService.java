package by.innowise.internship.payments.service;

import by.innowise.internship.payments.model.entity.PaymentStatus;

public interface PaymentProcessorService {

    PaymentStatus processPayment();

}
