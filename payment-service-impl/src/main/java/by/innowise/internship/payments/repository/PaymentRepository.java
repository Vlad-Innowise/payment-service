package by.innowise.internship.payments.repository;

import by.innowise.internship.payments.model.entity.Payment;
import by.innowise.internship.payments.model.entity.PaymentStatus;
import by.innowise.internship.payments.model.projection.PaymentTotal;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends MongoRepository<Payment, ObjectId> {

    List<Payment> findAllByUserIdAndOrderId(Long userId, UUID orderId);

    List<Payment> findAllByUserId(Long userId);

    List<Payment> findAllByUserIdAndStatus(Long userId, PaymentStatus status);

    @Aggregation(pipeline = {
            "{ $match : { 'user_id' : ?0, 'status' : ?1, 'timestamp' : { $gte : ?2, $lte : ?3 } } }",
            "{ $group : { _id : null, total : { $sum : '$payment_amount' } } }",
            "{ $project : { '_id' : 0, 'total' : 1} }"
    })
    Optional<PaymentTotal> getTotalSumOfPaymentsInStatusForUserForDatePeriod(Long userId,
                                                                             PaymentStatus status,
                                                                             LocalDateTime from,
                                                                             LocalDateTime to);

}
