package by.innowise.internship.payments.model.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Document(collection = "payments")
public class Payment extends BaseEntity {

    @EqualsAndHashCode.Include
    @Id
    private ObjectId id;

    @Field(name = "order_id", targetType = FieldType.BINARY)
    private UUID orderId;

    @Field("user_id")
    private Long userId;

    private PaymentStatus status;

    private LocalDateTime timestamp;

    @Field(value = "payment_amount", targetType = FieldType.DECIMAL128)
    private BigDecimal amount;

}
