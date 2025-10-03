package by.innowise.internship.payments.service.impl;

import by.innowise.internship.payments.mapper.PaymentMapper;
import by.innowise.internship.payments.model.dto.PaymentPeriod;
import by.innowise.internship.payments.model.dto.PaymentResponseDto;
import by.innowise.internship.payments.model.dto.PeriodTotalResponse;
import by.innowise.internship.payments.model.entity.Payment;
import by.innowise.internship.payments.model.entity.PaymentStatus;
import by.innowise.internship.payments.model.projection.PaymentTotal;
import by.innowise.internship.payments.repository.PaymentRepository;
import by.innowise.internship.payments.service.PaymentProcessorService;
import by.innowise.internship.payments.util.TestUtil;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    private static final Long USER_ID = 1L;
    private static final UUID ORDER_ONE_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID ORDER_TWO_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");

    @Mock
    private PaymentRepository repository;

    @Mock
    private PaymentMapper mapper;

    @Mock
    private PaymentProcessorService paymentProcessorService;

    @InjectMocks
    private PaymentServiceImpl paymentService;


    @Test
    void shouldReturnTwoPaymentsForDifferentOrdersByUserId() {

        ObjectId paymentOneId = ObjectId.get();
        ObjectId paymentTwoId = ObjectId.get();

        Payment paymentOne = TestUtil.getSuccessPayment(paymentOneId, ORDER_ONE_ID, USER_ID,
                                                        LocalDate.of(2025, 9, 28),
                                                        BigDecimal.valueOf(1200));

        Payment paymentTwo = TestUtil.getSuccessPayment(paymentTwoId, ORDER_TWO_ID, USER_ID,
                                                        LocalDate.of(2025, 9, 29),
                                                        BigDecimal.valueOf(2000));

        doReturn(List.of(paymentOne, paymentTwo))
                .when(repository).findAllByUserId(anyLong());
        mockMapperToResponseDto();

        List<PaymentResponseDto> actualResult = paymentService.getAllByUser(USER_ID);

        assertAll(
                () -> assertThat(actualResult)
                        .extracting(PaymentResponseDto::id)
                        .containsExactlyInAnyOrder(
                                paymentOneId.toHexString(),
                                paymentTwoId.toHexString()),

                () -> assertThat(actualResult)
                        .filteredOn(paymentResp -> paymentResp.orderId().equals(paymentOne.getOrderId()))
                        .allSatisfy(resp -> {
                            assertThat(resp.id()).isEqualTo(paymentOneId.toHexString());
                            assertThat(resp.amount()).isEqualTo(paymentOne.getAmount());
                            assertThat(resp.timestamp()).isEqualTo(paymentOne.getTimestamp());
                            assertThat(resp.status()).isEqualTo(paymentOne.getStatus());
                        }),

                () -> assertThat(actualResult)
                        .filteredOn(paymentResp -> paymentResp.orderId().equals(paymentTwo.getOrderId()))
                        .allSatisfy(resp -> {
                            assertThat(resp.id()).isEqualTo(paymentTwoId.toHexString());
                            assertThat(resp.amount()).isEqualTo(paymentTwo.getAmount());
                            assertThat(resp.timestamp()).isEqualTo(paymentTwo.getTimestamp());
                            assertThat(resp.status()).isEqualTo(paymentTwo.getStatus());
                        })
        );

        verify(repository).findAllByUserId(anyLong());
        verify(mapper, times(2)).toDto(any(Payment.class));
    }

    @Test
    void shouldReturnEmptyListForUserId() {

        long missingUserId = 999L;

        doReturn(Collections.emptyList())
                .when(repository).findAllByUserId(eq(missingUserId));

        List<PaymentResponseDto> actualResult = paymentService.getAllByUser(missingUserId);

        assertThat(actualResult).isEmpty();

        verify(repository).findAllByUserId(anyLong());
        verifyNoInteractions(mapper);
    }

    @Test
    void shouldReturnTwoPaymentsForSameOrderByUserIdAndOrderId() {

        ObjectId failedPaymentId = ObjectId.get();
        ObjectId successPaymentId = ObjectId.get();

        Payment orderOnePaymentFail = TestUtil.getFailedPayment(failedPaymentId, ORDER_ONE_ID, USER_ID,
                                                                LocalDate.of(2025, 9, 28),
                                                                BigDecimal.valueOf(1200));

        Payment orderOnePaymentSuccess = TestUtil.getSuccessPayment(successPaymentId, ORDER_ONE_ID, USER_ID,
                                                                    LocalDate.of(2025, 9, 29),
                                                                    BigDecimal.valueOf(1200));

        doReturn(List.of(orderOnePaymentFail, orderOnePaymentSuccess))
                .when(repository).findAllByUserIdAndOrderId(anyLong(), any(UUID.class));
        mockMapperToResponseDto();

        List<PaymentResponseDto> actualResult = paymentService.getByUserAndOrder(USER_ID, ORDER_ONE_ID);

        assertAll(
                () -> assertThat(actualResult)
                        .extracting(PaymentResponseDto::id)
                        .containsExactlyInAnyOrder(
                                failedPaymentId.toHexString(),
                                successPaymentId.toHexString()),

                () -> assertThat(actualResult)
                        .filteredOn(paymentResp -> paymentResp.status() == PaymentStatus.FAILED)
                        .allSatisfy(resp -> {
                            assertThat(resp.id()).isEqualTo(failedPaymentId.toHexString());
                            assertThat(resp.amount()).isEqualTo(orderOnePaymentFail.getAmount());
                            assertThat(resp.timestamp()).isEqualTo(orderOnePaymentFail.getTimestamp());
                            assertThat(resp.orderId()).isEqualTo(orderOnePaymentFail.getOrderId());
                        }),

                () -> assertThat(actualResult)
                        .filteredOn(paymentResp -> paymentResp.status() == PaymentStatus.SUCCEED)
                        .allSatisfy(resp -> {
                            assertThat(resp.id()).isEqualTo(successPaymentId.toHexString());
                            assertThat(resp.amount()).isEqualTo(orderOnePaymentSuccess.getAmount());
                            assertThat(resp.timestamp()).isEqualTo(orderOnePaymentSuccess.getTimestamp());
                            assertThat(resp.orderId()).isEqualTo(orderOnePaymentSuccess.getOrderId());
                        })
        );

        verify(repository).findAllByUserIdAndOrderId(anyLong(), any(UUID.class));
        verify(mapper, times(2)).toDto(any(Payment.class));
    }

    @Test
    void shouldReturnTwoFailedPaymentsForUser() {

        ObjectId paymentOneId = ObjectId.get();
        ObjectId paymentTwoId = ObjectId.get();

        Payment paymentOne = TestUtil.getFailedPayment(paymentOneId, ORDER_ONE_ID, USER_ID,
                                                       LocalDate.of(2025, 9, 28),
                                                       BigDecimal.valueOf(1200));

        Payment paymentTwo = TestUtil.getFailedPayment(paymentTwoId, ORDER_TWO_ID, USER_ID,
                                                       LocalDate.of(2025, 9, 29),
                                                       BigDecimal.valueOf(2000));

        doReturn(List.of(paymentOne, paymentTwo))
                .when(repository).findAllByUserIdAndStatus(anyLong(), any(PaymentStatus.class));
        mockMapperToResponseDto();

        List<PaymentResponseDto> actualResult = paymentService.getAllByUserAndStatus(USER_ID, PaymentStatus.FAILED);

        assertAll(
                () -> assertThat(actualResult)
                        .extracting(PaymentResponseDto::id)
                        .containsExactlyInAnyOrder(
                                paymentOneId.toHexString(),
                                paymentTwoId.toHexString()),

                () -> assertThat(actualResult)
                        .filteredOn(paymentResp -> paymentResp.orderId().equals(paymentOne.getOrderId()))
                        .allSatisfy(resp -> {
                            assertThat(resp.id()).isEqualTo(paymentOneId.toHexString());
                            assertThat(resp.amount()).isEqualTo(paymentOne.getAmount());
                            assertThat(resp.timestamp()).isEqualTo(paymentOne.getTimestamp());
                            assertThat(resp.status()).isEqualTo(paymentOne.getStatus());
                        }),

                () -> assertThat(actualResult)
                        .filteredOn(paymentResp -> paymentResp.orderId().equals(paymentTwo.getOrderId()))
                        .allSatisfy(resp -> {
                            assertThat(resp.id()).isEqualTo(paymentTwoId.toHexString());
                            assertThat(resp.amount()).isEqualTo(paymentTwo.getAmount());
                            assertThat(resp.timestamp()).isEqualTo(paymentTwo.getTimestamp());
                            assertThat(resp.status()).isEqualTo(paymentTwo.getStatus());
                        })
        );

        verify(repository).findAllByUserIdAndStatus(anyLong(), any(PaymentStatus.class));
        verify(mapper, times(2)).toDto(any(Payment.class));
    }

    @Test
    void shouldReturnNonZeroPositivePaymentTotalForPeriod() {

        PaymentPeriod period = new PaymentPeriod(LocalDate.of(2025, 9, 29),
                                                 LocalDate.of(2025, 9, 30));

        BigDecimal calculatedTotal = BigDecimal.valueOf(1000);

        PeriodTotalResponse expectedResult = new PeriodTotalResponse(period.from().atStartOfDay(),
                                                                     TestUtil.getLocalDateTimeEndOfTheDay(
                                                                             period.to()),
                                                                     calculatedTotal);

        doReturn(Optional.of(new PaymentTotal(calculatedTotal)))
                .when(repository).getTotalSumOfPaymentsInStatusForUserForDatePeriod(anyLong(), any(PaymentStatus.class),
                                                                                    any(), any());

        PeriodTotalResponse actualResult = paymentService.calculatePaymentTotalForPeriod(USER_ID, period);


        assertThat(actualResult).isEqualTo(expectedResult);

    }

    @Test
    void shouldReturnZeroTotalForPeriod() {


        PaymentPeriod period = new PaymentPeriod(LocalDate.of(2025, 9, 29),
                                                 LocalDate.of(2025, 9, 30));

        BigDecimal calculatedTotal = BigDecimal.ZERO;

        PeriodTotalResponse expectedResult = new PeriodTotalResponse(period.from().atStartOfDay(),
                                                                     TestUtil.getLocalDateTimeEndOfTheDay(
                                                                             period.to()),
                                                                     calculatedTotal);

        doReturn(Optional.of(new PaymentTotal(calculatedTotal)))
                .when(repository).getTotalSumOfPaymentsInStatusForUserForDatePeriod(anyLong(), any(PaymentStatus.class),
                                                                                    any(), any());

        PeriodTotalResponse actualResult = paymentService.calculatePaymentTotalForPeriod(USER_ID, period);

        assertThat(actualResult).isEqualTo(expectedResult);

    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenPeriodStartDateIsGreaterThanEnd() {

        PaymentPeriod period = new PaymentPeriod(LocalDate.of(2025, 9, 30),
                                                 LocalDate.of(2025, 9, 29));

        assertThrows(IllegalArgumentException.class,
                     () -> paymentService.calculatePaymentTotalForPeriod(USER_ID, period));

    }

    public void mockMapperToResponseDto() {
        doAnswer(invocation -> {
            Payment payment = invocation.getArgument(0, Payment.class);
            return TestUtil.mapToPaymentResponse(payment);
        }).when(mapper).toDto(any(Payment.class));
    }


}