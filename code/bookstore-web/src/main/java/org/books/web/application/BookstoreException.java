package org.books.web.application;

public class BookstoreException extends Exception {

	public enum Code {

		BOOK_ALREADY_EXISTS, BOOK_NOT_FOUND, CUSTOMER_ALREADY_EXISTS, CUSTOMER_NOT_FOUND, INVALID_PASSWORD,
		INVALID_CREDIT_CARD_NUMBER, CREDIT_CARD_EXPIRED, CREDIT_CARD_LIMIT_EXCEEDED,
		ORDER_NOT_FOUND, ORDER_ALREADY_SHIPPED
	}

	private final Code code;

	public BookstoreException(Code code) {
		super("Bookstore error (" + code + ")");
		this.code = code;
	}

	public Code getCode() {
		return code;
	}
}
