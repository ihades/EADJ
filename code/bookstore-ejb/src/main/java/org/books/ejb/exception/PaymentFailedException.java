package org.books.ejb.exception;

/**
 *
 * @author cb
 */
public class PaymentFailedException extends Exception {

    public static enum Code {

        CREDIT_CARD_EXPIRED,
        INVALID_CREDIT_CARD,
        PAYMENT_LIMIT_EXCEEDED
    }

    private final PaymentFailedException.Code code;

    public PaymentFailedException(PaymentFailedException.Code code) {
        this.code = code;
    }

    public PaymentFailedException.Code getCode() {
        return code;
    }

}
