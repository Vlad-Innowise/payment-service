package by.innowise.internship.payments.service.impl;

import by.innowise.internship.payments.mapper.PaymentMapper;
import by.innowise.internship.payments.model.dto.PaymentRequestDto;
import by.innowise.internship.payments.model.dto.PaymentResponseDto;
import by.innowise.internship.payments.model.entity.Payment;
import by.innowise.internship.payments.repository.PaymentRepository;
import by.innowise.internship.payments.service.PaymentProcessorService;
import by.innowise.internship.payments.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    private void logFoundPaymentIds(List<Payment> allPaymentsByUser) {
        Set<String> foundPayments = allPaymentsByUser.stream()
                                                     .map(Payment::getId)
                                                     .map(ObjectId::toHexString)
                                                     .collect(Collectors.toSet());
        log.info("Found payments with ids: {}", foundPayments);
    }
}
