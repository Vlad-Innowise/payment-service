package by.innowise.internship.payments.controller;

import by.innowise.internship.payments.model.dto.PaymentPeriod;
import by.innowise.internship.payments.model.dto.PaymentRequestDto;
import by.innowise.internship.payments.model.dto.PaymentResponseDto;
import by.innowise.internship.payments.model.dto.PeriodTotalResponse;
import by.innowise.internship.payments.model.entity.PaymentStatus;
import by.innowise.internship.payments.service.PaymentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

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

    @GetMapping("/by-order/{orderId}")
    public ResponseEntity<List<PaymentResponseDto>> getByUserAndOrder(@PathVariable
                                                                      Long userId,
                                                                      @PathVariable
                                                                      @NotNull(message = "Order id can't be null")
                                                                      UUID orderId) {
        log.info("Requested to get all payments for order: {}, user: {}", orderId, userId);
        List<PaymentResponseDto> allPayments = paymentService.getByUserAndOrder(userId, orderId);
        log.info("Got payment list for order: {} of size: {}", orderId, allPayments.size());
        return ResponseEntity.ok(allPayments);
    }

    @GetMapping("/by-status")
    public ResponseEntity<List<PaymentResponseDto>> getByUserAndStatus(@PathVariable Long userId,
                                                                       @RequestParam
                                                                       PaymentStatus status) {
        log.info("Requested to get all payments for user: {} by status: {}", userId, status);
        List<PaymentResponseDto> allPaymentsByStatus = paymentService.getAllByUserAndStatus(userId, status);
        log.info("Received all payments: {} with status: {}", allPaymentsByStatus.size(), status);
        return ResponseEntity.ok(allPaymentsByStatus);
    }

    @GetMapping("/total-by-period")
    public ResponseEntity<PeriodTotalResponse> getByUserAndStatus(@PathVariable Long userId,
                                                                  @RequestBody @Valid PaymentPeriod paymentPeriod) {
        log.info("Requested to calculate total payment amount for finished payment for the period from: {} to: {}",
                 paymentPeriod.from(), paymentPeriod.to());
        return ResponseEntity.ok(paymentService.calculatePaymentTotalForPeriod(userId, paymentPeriod));
    }
}
