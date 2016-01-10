package org.books.ejb.exception;

/**
 *
 * @author cb
 */
public class JmsRuntimeException extends RuntimeException {

    public JmsRuntimeException(javax.jms.JMSException cause) {
        super(cause);
    }

    public JmsRuntimeException(String message, javax.jms.JMSException cause) {
        super(message, cause);
    }

}
