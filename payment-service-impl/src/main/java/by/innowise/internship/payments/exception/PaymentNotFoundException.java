package by.innowise.internship.payments.exception;

import by.innowise.common.library.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class PaymentNotFoundException extends ApplicationException {

    public PaymentNotFoundException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }

}
