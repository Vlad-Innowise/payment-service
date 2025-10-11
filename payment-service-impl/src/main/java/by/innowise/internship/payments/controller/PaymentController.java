package by.innowise.internship.payments.controller;

import by.innowise.internship.payments.model.dto.PaymentPeriod;
import by.innowise.internship.payments.model.dto.PaymentRequestDto;
import by.innowise.internship.payments.model.dto.PaymentResponseDto;
import by.innowise.internship.payments.model.dto.PeriodTotalResponse;
import by.innowise.internship.payments.model.entity.PaymentStatus;
import by.innowise.internship.payments.service.facade.PaymentFacade;
import by.innowise.internship.security.dto.UserHolder;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
@RequestMapping("/payments")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PaymentController {

    private final PaymentFacade paymentFacade;

    @PostMapping("/new")
    public ResponseEntity<PaymentResponseDto> createPayment(@RequestBody @Valid PaymentRequestDto newPayment,
                                                            @AuthenticationPrincipal UserHolder userHolder) {
        Long userId = userHolder.crossServiceUserId();
        log.info("Requested to process a new payment {}: for user: {}", newPayment, userId);
        PaymentResponseDto created = paymentFacade.create(newPayment, userId);
        log.info("Payment request was processed. Sending response to the client: {}", created);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponseDto> getPaymentsById(@PathVariable ObjectId paymentId,
                                                              @AuthenticationPrincipal UserHolder userHolder) {
        Long userId = userHolder.crossServiceUserId();
        log.info("User: [{}] initiated request to get payment by id: [{}]", userId, paymentId);
        PaymentResponseDto paymentResponse = paymentFacade.getByUserAndPaymentId(userId, paymentId);
        log.info("Payment response received and sending to a client: {}", paymentResponse);
        return ResponseEntity.ok(paymentResponse);
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponseDto>> getAllPaymentsByUser(@AuthenticationPrincipal
                                                                         UserHolder userHolder) {
        Long userId = userHolder.crossServiceUserId();
        log.info("Requested to get all payments for user: {}", userId);
        List<PaymentResponseDto> allPayments = paymentFacade.getAllByUser(userId);
        log.info("Got payment list of size: {}", allPayments.size());
        return ResponseEntity.ok(allPayments);
    }

    @GetMapping("/by-order/{orderId}")
    public ResponseEntity<List<PaymentResponseDto>> getByUserAndOrder(@PathVariable
                                                                      @NotNull(message = "Order id can't be null")
                                                                      UUID orderId,
                                                                      @AuthenticationPrincipal
                                                                      UserHolder userHolder) {
        Long userId = userHolder.crossServiceUserId();
        log.info("Requested to get all payments for order: {}, user: {}", orderId, userId);
        List<PaymentResponseDto> allPayments = paymentFacade.getByUserAndOrder(userId, orderId);
        log.info("Got payment list for order: {} of size: {}", orderId, allPayments.size());
        return ResponseEntity.ok(allPayments);
    }

    @GetMapping("/by-status")
    public ResponseEntity<List<PaymentResponseDto>> getByUserAndStatus(@RequestParam PaymentStatus status,
                                                                       @AuthenticationPrincipal UserHolder userHolder) {
        Long userId = userHolder.crossServiceUserId();
        log.info("Requested to get all payments for user: {} by status: {}", userId, status);
        List<PaymentResponseDto> allPaymentsByStatus = paymentFacade.getAllByUserAndStatus(userId, status);
        log.info("Received all payments: {} with status: {}", allPaymentsByStatus.size(), status);
        return ResponseEntity.ok(allPaymentsByStatus);
    }

    @GetMapping("/total-by-period")
    public ResponseEntity<PeriodTotalResponse> getByUserAndStatus(@RequestBody @Valid PaymentPeriod paymentPeriod,
                                                                  @AuthenticationPrincipal UserHolder userHolder) {
        Long userId = userHolder.crossServiceUserId();
        log.info("Requested to calculate total payment amount for finished payment for the period from: {} to: {}",
                 paymentPeriod.from(), paymentPeriod.to());
        return ResponseEntity.ok(paymentFacade.calculatePaymentTotalForPeriod(userId, paymentPeriod));
    }
}
