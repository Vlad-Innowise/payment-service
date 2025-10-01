package by.innowise.internship.payments.service.impl;

import by.innowise.internship.payments.mapper.PaymentMapper;
import by.innowise.internship.payments.model.dto.PaymentPeriod;
import by.innowise.internship.payments.model.dto.PaymentRequestDto;
import by.innowise.internship.payments.model.dto.PaymentResponseDto;
import by.innowise.internship.payments.model.dto.PeriodTotalResponse;
import by.innowise.internship.payments.model.entity.Payment;
import by.innowise.internship.payments.model.entity.PaymentStatus;
import by.innowise.internship.payments.model.projection.PaymentTotal;
import by.innowise.internship.payments.repository.PaymentRepository;
import by.innowise.internship.payments.service.PaymentProcessorService;
import by.innowise.internship.payments.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository repository;
    private final PaymentMapper mapper;
    private final PaymentProcessorService processorService;

    private static LocalDateTime getLocalDateTime(PaymentPeriod paymentPeriod) {
        return paymentPeriod.to().atTime(23, 59, 59,
                                         999_999_999);
    }

    @Transactional
    @Override
    public PaymentResponseDto create(PaymentRequestDto createDto, Long userId) {
        log.info("Mapping dto: {} to entity", createDto);
        Payment toSave = mapper.toEntity(createDto, userId);
        toSave.setTimestamp(LocalDateTime.now());
        log.info("Invoking a payment processor service to process a payment");
        toSave.setStatus(processorService.processPayment());
        log.info("Payment processing status: {}", toSave.getStatus());
        Payment saved = repository.save(toSave);
        log.info("Pre-saved entity to repository: {}", saved);
        return mapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public List<PaymentResponseDto> getAllByUser(Long userId) {
        List<Payment> allPaymentsByUser = repository.findAllByUserId(userId);
        logFoundPaymentIds(allPaymentsByUser);
        return allPaymentsByUser.stream()
                                .map(mapper::toDto)
                                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<PaymentResponseDto> getByUserAndOrder(Long userId, UUID orderId) {
        List<Payment> foundPayments = repository.findAllByUserIdAndOrderId(userId, orderId);
        logFoundPaymentIds(foundPayments);
        return foundPayments.stream()
                            .map(mapper::toDto)
                            .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<PaymentResponseDto> getAllByUserAndStatus(Long userId, PaymentStatus status) {
        List<Payment> foundPayments = repository.findAllByUserIdAndStatus(userId, status);
        logFoundPaymentIds(foundPayments);
        return foundPayments.stream()
                            .map(mapper::toDto)
                            .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public PeriodTotalResponse calculatePaymentTotalForPeriod(Long userId, PaymentPeriod paymentPeriod) {
        validatePaymentPeriodDates(paymentPeriod);

        LocalDateTime periodStart = paymentPeriod.from().atStartOfDay();
        LocalDateTime periodEnd = getLocalDateTime(paymentPeriod);
        BigDecimal paymentsTotal =
                repository.getTotalSumOfPaymentsInStatusForUserForDatePeriod(userId,
                                                                             PaymentStatus.SUCCEED,
                                                                             periodStart,
                                                                             periodEnd)
                          .map(PaymentTotal::total)
                          .orElse(BigDecimal.ZERO);

        log.info(
                "Calculated payments total amount for user: [{}] for payments in: [{}] status for the period -  from: {} to: {} is: [{}]",
                userId, PaymentStatus.SUCCEED, paymentPeriod.from(), paymentPeriod.to(), paymentsTotal);
        return new PeriodTotalResponse(periodStart, periodEnd, paymentsTotal);
    }

    private void logFoundPaymentIds(List<Payment> allPaymentsByUser) {
        Set<String> foundPayments = allPaymentsByUser.stream()
                                                     .map(Payment::getId)
                                                     .map(ObjectId::toHexString)
                                                     .collect(Collectors.toSet());
        log.info("Found payments with ids: {}", foundPayments);
    }

    private void validatePaymentPeriodDates(PaymentPeriod paymentPeriod) {
        if (paymentPeriod.from().isAfter(paymentPeriod.to())) {
            throw new IllegalArgumentException(
                    "Payment period start date: [%s] can't be after a payment period end date: [%s]".formatted(
                            paymentPeriod.from(), paymentPeriod.to()));
        }
    }
}
