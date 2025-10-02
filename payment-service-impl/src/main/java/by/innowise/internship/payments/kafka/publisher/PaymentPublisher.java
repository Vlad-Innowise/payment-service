package by.innowise.internship.payments.kafka.publisher;

import by.innowise.common.library.kafka.KafkaTopics;
import by.innowise.common.library.kafka.event.PaymentCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishPaymentCreated(PaymentCreatedEvent event) {
        try {
            log.info("Requested to send payment-created event: {}", event);
            String key = event.orderId().toString();
            kafkaTemplate.send(KafkaTopics.PAYMENT_CREATED_TOPIC, key, event);
            log.info("Payment created event was send with key: {}", key);
        } catch (Exception e) {
            log.error("Failed to publish event: {} to topic: [{}]", event, KafkaTopics.PAYMENT_CREATED_TOPIC, e);
        }

    }
}
