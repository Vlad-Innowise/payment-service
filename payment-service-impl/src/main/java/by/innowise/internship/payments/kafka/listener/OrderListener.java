package by.innowise.internship.payments.kafka.listener;

import by.innowise.common.library.kafka.KafkaTopics;
import by.innowise.common.library.kafka.event.OrderCreatedEvent;
import by.innowise.common.library.kafka.event.PaymentCreatedEvent;
import by.innowise.common.library.kafka.event.PaymentEventStatus;
import by.innowise.internship.payments.config.KafkaConfig;
import by.innowise.internship.payments.kafka.publisher.PaymentPublisher;
import by.innowise.internship.payments.model.dto.PaymentRequestDto;
import by.innowise.internship.payments.model.dto.PaymentResponseDto;
import by.innowise.internship.payments.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderListener {

    private final PaymentService paymentService;
    private final PaymentPublisher paymentPublisher;

    @KafkaListener(topics = KafkaTopics.ORDER_CREATED_TOPIC, groupId = KafkaConfig.KAFKA_PAYMENT_CONSUMER_GROUP)
    public void processEvent(OrderCreatedEvent event, Acknowledgment ack) {

        log.info("Received order-created event: {}", event);
        try {
            PaymentRequestDto request = new PaymentRequestDto(event.orderId(), event.amount());
            log.info("Saving and processing a new payment");
            PaymentResponseDto responseDto = paymentService.create(request, event.userId());
            log.info("New payment created and processed: {}", responseDto);
            PaymentCreatedEvent paymentEvent = new PaymentCreatedEvent(responseDto.userId(),
                                                                       responseDto.orderId(),
                                                                       PaymentEventStatus.valueOf(
                                                                               responseDto.status().name()));
            paymentPublisher.publishPaymentCreated(paymentEvent);
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Failed to process order-created-event: {}", event, e);
            throw e;
        }
    }

}
