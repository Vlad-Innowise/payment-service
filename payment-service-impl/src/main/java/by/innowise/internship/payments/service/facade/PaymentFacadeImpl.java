package by.innowise.internship.payments.service.facade;

import by.innowise.common.library.kafka.event.PaymentCreatedEvent;
import by.innowise.common.library.kafka.event.PaymentEventStatus;
import by.innowise.internship.payments.kafka.publisher.PaymentPublisher;
import by.innowise.internship.payments.model.dto.PaymentPeriod;
import by.innowise.internship.payments.model.dto.PaymentRequestDto;
import by.innowise.internship.payments.model.dto.PaymentResponseDto;
import by.innowise.internship.payments.model.dto.PeriodTotalResponse;
import by.innowise.internship.payments.model.entity.PaymentStatus;
import by.innowise.internship.payments.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentFacadeImpl implements PaymentFacade {

    private final PaymentService paymentService;
    private final PaymentPublisher paymentPublisher;

    @Override
    public PaymentResponseDto create(PaymentRequestDto createDto, Long userId) {
        PaymentResponseDto responseDto = paymentService.create(createDto, userId);
        log.info("New payment created and processed: {}", responseDto);
        PaymentCreatedEvent paymentEvent = new PaymentCreatedEvent(responseDto.userId(),
                                                                   responseDto.orderId(),
                                                                   PaymentEventStatus.valueOf(
                                                                           responseDto.status().name()));
        log.info("Mapped response dto to payment event: {}", paymentEvent);
        paymentPublisher.publishPaymentCreated(paymentEvent);
        return responseDto;
    }

    @Override
    public List<PaymentResponseDto> getAllByUser(Long userId) {
        return paymentService.getAllByUser(userId);
    }

    @Override
    public List<PaymentResponseDto> getByUserAndOrder(Long userId, UUID orderId) {
        return paymentService.getByUserAndOrder(userId, orderId);
    }

    @Override
    public List<PaymentResponseDto> getAllByUserAndStatus(Long userId, PaymentStatus status) {
        return paymentService.getAllByUserAndStatus(userId, status);
    }

    @Override
    public PeriodTotalResponse calculatePaymentTotalForPeriod(Long userId, PaymentPeriod paymentPeriod) {
        return paymentService.calculatePaymentTotalForPeriod(userId, paymentPeriod);
    }
}
