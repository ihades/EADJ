package org.books.ejb.exception;

import java.util.List;
import javax.ejb.ApplicationException;

@ApplicationException
public class ValidationException extends RuntimeException {

    private final List<String> messages;

    public ValidationException(List<String> messages) {
        this.messages = messages;
    }

    public List<String> getMessages() {
        return messages;
    }

}
