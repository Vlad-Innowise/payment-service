package by.innowise.internship.payments.controller;

import by.innowise.internship.payments.model.dto.PaymentRequestDto;
import by.innowise.internship.payments.model.dto.PaymentResponseDto;
import by.innowise.internship.payments.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/payments/user/{userId}")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponseDto> createPayment(@PathVariable Long userId,
                                                            @RequestBody @Valid PaymentRequestDto newPayment) {
        log.info("Requested to process a new payment for user: {}: {}", newPayment, userId);
        PaymentResponseDto created = paymentService.create(newPayment, userId);
        log.info("Payment request was processed. Sending response to the client: {}", created);
        return ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponseDto>> getAllPaymentsByUser(@PathVariable Long userId) {
        log.info("Requested to get all payments for user: {}", userId);
        List<PaymentResponseDto> allPayments = paymentService.getAllByUser(userId);
        log.info("Got payment list of size: {}", allPayments.size());
        return ResponseEntity.ok(allPayments);
    }
}
