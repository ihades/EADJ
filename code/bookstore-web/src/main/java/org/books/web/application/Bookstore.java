package org.books.web.application;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import org.books.web.application.BookstoreException.Code;
import org.books.web.data.dto.BookInfo;
import org.books.web.data.dto.CustomerInfo;
import org.books.web.data.dto.OrderDTO;
import org.books.web.data.dto.OrderInfo;
import org.books.web.data.dto.OrderItemDTO;
import org.books.web.data.entity.Address;
import org.books.web.data.entity.Book;
import org.books.web.data.entity.CreditCard;
import org.books.web.data.entity.Customer;
import org.books.web.data.entity.Order;
import org.books.web.data.entity.Order.Status;
import org.books.web.data.entity.OrderItem;
import org.books.web.persistence.XmlParser;

/**
 * The class Bookstore is a mock implementation of the bookstore application.
 *
 * @author Stephan Fischli
 */
@ApplicationScoped
public class Bookstore {

	private static final String CATALOG_DATA = "/data/catalog.xml";
	private static final String CUSTOMERS_DATA = "/data/customers.xml";
	private static final Pattern CREDIT_CARD_NUMBER_PATTERN = Pattern.compile("\\d{16}");
	private static final long CREDIT_CARD_LIMIT = 1000;
	private static final long ORDER_PROCESS_TIME = 600000;

	private static final Logger logger = Logger.getLogger(Bookstore.class.getName());

	private final Map<String, Book> books = new HashMap<>();
	private final Map<String, Customer> customers = new HashMap<>();
	private final Map<String, Order> orders = new HashMap<>();
	private final Map<String, String> logins = new HashMap<>();

	@PostConstruct
	public void init() {
		for (Book book : XmlParser.parse(CATALOG_DATA, Book.class)) {
			books.put(book.getIsbn(), book);
		}
		for (Customer customer : XmlParser.parse(CUSTOMERS_DATA, Customer.class)) {
			customers.put(customer.getEmail(), customer);
			logins.put(customer.getEmail(), customer.getFirstName().toLowerCase());
		}
	}

	/**
	 * Adds a book to the catalog.
	 *
	 * @param book the book data
	 * @throws BookstoreException if a book with the same ISBN number already exists
	 */
	public void addBook(Book book) throws BookstoreException {
		logger.log(Level.INFO, "Adding book with isbn ''{0}''", book.getIsbn());
		if (books.containsKey(book.getIsbn())) {
			throw new BookstoreException(Code.BOOK_ALREADY_EXISTS);
		}
		books.put(book.getIsbn(), book);
	}

	/**
	 * Finds a book with a particular ISBN number.
	 *
	 * @param isbn the ISBN number to look for
	 * @return the data of the found book
	 * @throws BookstoreException if no book with the specified ISBN number exists
	 */
	public Book findBook(String isbn) throws BookstoreException {
		logger.log(Level.INFO, "Finding book with isbn ''{0}''", isbn);
		if (!books.containsKey(isbn)) {
			throw new BookstoreException(Code.BOOK_NOT_FOUND);
		}
		return books.get(isbn);
	}

	/**
	 * Searches for books by keywords.<br>
	 * A book is included in the result list if every keyword is contained in its title, authors or publisher field.
	 *
	 * @param keywords the keywords to search for
	 * @return a list of matching books (may be empty)
	 */
	public List<BookInfo> searchBooks(String keywords) {
		logger.log(Level.INFO, "Searching books with keywords ''{0}''", keywords);
		List<BookInfo> results = new ArrayList<>();
		loop:
		for (Book book : books.values()) {
			for (String keyword : keywords.toLowerCase().split("\\s+")) {
				if (!book.getTitle().toLowerCase().contains(keyword)
						&& !book.getAuthors().toLowerCase().contains(keyword)
						&& !book.getPublisher().toLowerCase().contains(keyword)) {
					continue loop;
				}
			}
			results.add(new BookInfo(book));
		}
		return results;
	}

	/**
	 * Updates a book.<br>
	 *
	 * @param book the book data
	 * @throws BookstoreException if the book is not contained in the catalog
	 */
	public void updateBook(Book book) throws BookstoreException {
		logger.log(Level.INFO, "Updating book with isbn ''{0}''", book.getIsbn());
		if (books.get(book.getIsbn()) != book) {
			throw new BookstoreException(Code.BOOK_NOT_FOUND);
		}
	}

	/**
	 * Registers a customer.<br>
	 *
	 * @param customer the customer to be registered
	 * @param password the password of the customer
	 * @throws BookstoreException if another customer with the same email address already exists
	 */
	public void registerCustomer(Customer customer, String password) throws BookstoreException {
		logger.log(Level.INFO, "Registering customer with email ''{0}''", customer.getEmail());
		if (customers.containsKey(customer.getEmail())) {
			throw new BookstoreException(Code.CUSTOMER_ALREADY_EXISTS);
		}
		customers.put(customer.getEmail(), customer);
		logins.put(customer.getEmail(), password);
	}

	/**
	 * Authenticates a customer.
	 *
	 * @param email the email address of the customer
	 * @param password the password of the customer
	 * @throws BookstoreException if no customer with the specified email address exists, or if the password is invalid
	 */
	public void authenticateCustomer(String email, String password) throws BookstoreException {
		logger.log(Level.INFO, "Authenticating customer with email ''{0}''", email);
		if (!logins.containsKey(email)) {
			throw new BookstoreException(Code.CUSTOMER_NOT_FOUND);
		}
		if (!logins.get(email).equals(password)) {
			throw new BookstoreException(Code.INVALID_PASSWORD);
		}
	}

	/**
	 * Changes the password of a customer.
	 *
	 * @param email the email address of the customer
	 * @param password the new password of the customer
	 * @throws BookstoreException if no customer with the specified email address exists
	 */
	public void changePassword(String email, String password) throws BookstoreException {
		logger.log(Level.INFO, "Changing password of customer with email ''{0}''", email);
		if (!logins.containsKey(email)) {
			throw new BookstoreException(Code.CUSTOMER_NOT_FOUND);
		}
		logins.put(email, password);
	}

	/**
	 * Finds a customer with a particular email address.
	 *
	 * @param email the email address to look for
	 * @return the data of the found customer
	 * @throws BookstoreException if no customer with the specified email address exists
	 */
	public Customer findCustomer(String email) throws BookstoreException {
		logger.log(Level.INFO, "Finding customer with email ''{0}''", email);
		if (!customers.containsKey(email)) {
			throw new BookstoreException(Code.CUSTOMER_NOT_FOUND);
		}
		return customers.get(email);
	}

	/**
	 * Searches for customers by name.<br>
	 * A customer is included in the result list if the specified name is part of its first or last name.
	 *
	 * @param name the name to search for
	 * @return a list of matching customers (may be empty)
	 */
	public List<CustomerInfo> searchCustomers(String name) {
		logger.log(Level.INFO, "Searching customers with name ''{0}''", name);
		name = name.toLowerCase();
		List<CustomerInfo> results = new ArrayList<>();
		for (Customer customer : customers.values()) {
			if (customer.getFirstName().toLowerCase().contains(name)
					|| customer.getLastName().toLowerCase().contains(name)) {
				results.add(new CustomerInfo(customer));
			}
		}
		return results;
	}

	/**
	 * Updates the data of a customer.<br>
	 * If the email address is to be changed, the new email address is also used for authentication.
	 *
	 * @param customer the data of the customer to be updated (the id must not be null)
	 * @throws BookstoreException if the customer is not yet registered, or if another customer with the same email
	 * address already exists
	 */
	public void updateCustomer(Customer customer) throws BookstoreException {
		logger.log(Level.INFO, "Updating customer with email ''{0}''", customer.getEmail());
		String registeredEmail = getRegisteredEmail(customer);
		if (!customer.getEmail().equals(registeredEmail)) {
			if (customers.containsKey(customer.getEmail())) {
				throw new BookstoreException(Code.CUSTOMER_ALREADY_EXISTS);
			}
			customers.put(customer.getEmail(), customers.remove(registeredEmail));
			logins.put(customer.getEmail(), logins.remove(registeredEmail));
		}
	}

	/**
	 * Places an order.
	 *
	 * @param email the email address of the customer
	 * @param items the order items
	 * @return the data of the placed order
	 * @throws BookstoreException if the customer does not exist, if an order item references a non-existing book, or if
	 * a payment error occurs
	 */
	public OrderDTO placeOrder(String email, List<OrderItemDTO> items) throws BookstoreException {
		logger.log(Level.INFO, "Placing order for customer with email ''{0}''", email);
		Customer customer = findCustomer(email);
		Order order = createOrder(customer, items);
		makePayment(customer, order.getAmount());
		orders.put(order.getNumber(), order);
		processOrder(order);
		return new OrderDTO(order);
	}

	/**
	 * Finds an order with a particular number.
	 *
	 * @param number the number to look for
	 * @return the data of the found order
	 * @throws BookstoreException if no order with the specified number exists
	 */
	public OrderDTO findOrder(String number) throws BookstoreException {
		logger.log(Level.INFO, "Finding order with number ''{0}''", number);
		if (!orders.containsKey(number)) {
			throw new BookstoreException(Code.ORDER_NOT_FOUND);
		}
		return new OrderDTO(orders.get(number));
	}

	/**
	 * Searches for orders by customer and year.
	 *
	 * @param email the email address of the customer
	 * @param year the year of the orders
	 * @return a list of matching orders (may be empty)
	 * @throws BookstoreException if no customer with the specified email address exists
	 */
	public List<OrderInfo> searchOrders(String email, Integer year) throws BookstoreException {
		logger.log(Level.INFO, "Searching orders of customer with email ''{0}''", email);
		Customer customer = findCustomer(email);
		List<OrderInfo> results = new ArrayList<>();
		for (Order order : orders.values()) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(order.getDate());
			if (order.getCustomer().equals(customer) && calendar.get(Calendar.YEAR) == year) {
				results.add(new OrderInfo(order));
			}
		}
		return results;
	}

	/**
	 * Cancels an order.
	 *
	 * @param number the number of the order
	 * @throws BookstoreException if no order with the specified number exists, or if the order has already been shipped
	 */
	public void cancelOrder(String number) throws BookstoreException {
		logger.log(Level.INFO, "Canceling order with number ''{0}''", number);
		if (!orders.containsKey(number)) {
			throw new BookstoreException(Code.ORDER_NOT_FOUND);
		}
		Order order = orders.get(number);
		if (order.getStatus() == Status.shipped) {
			throw new BookstoreException(Code.ORDER_ALREADY_SHIPPED);
		}
		order.setStatus(Status.canceled);
	}

	private String getRegisteredEmail(Customer customer) throws BookstoreException {
		for (Entry<String, Customer> entry : customers.entrySet()) {
			if (entry.getValue() == customer) {
				return entry.getKey();
			}
		}
		throw new BookstoreException(Code.CUSTOMER_NOT_FOUND);
	}

	private Order createOrder(Customer customer, List<OrderItemDTO> items) throws BookstoreException {
		Order order = new Order();
		Calendar calendar = Calendar.getInstance();
		String number = String.valueOf(Math.random()).substring(2, 7);
		order.setNumber(calendar.get(Calendar.YEAR) + "-" + number);
		order.setDate(calendar.getTime());
		order.setStatus(Status.accepted);
		order.setCustomer(customer);
		order.setAddress(new Address(customer.getAddress()));
		order.setCreditCard(new CreditCard(customer.getCreditCard()));
		addItems(order, items);
		return order;
	}

	private void addItems(Order order, List<OrderItemDTO> items) throws BookstoreException {
		BigDecimal totalPrice = BigDecimal.ZERO;
		for (OrderItemDTO item : items) {
			Book book = findBook(item.getBook().getIsbn());
			order.getItems().add(new OrderItem(book, book.getPrice(), item.getQuantity()));
			totalPrice = totalPrice.add(book.getPrice().multiply(new BigDecimal(item.getQuantity())));
		}
		order.setAmount(totalPrice);
	}

	private void makePayment(Customer customer, BigDecimal amount) throws BookstoreException {
		logger.log(Level.INFO, "Making payment for customer with email ''{0}''", customer.getEmail());
		CreditCard card = customer.getCreditCard();
		checkNumber(card);
		checkExpiration(card);
		if (amount.compareTo(BigDecimal.valueOf(CREDIT_CARD_LIMIT)) > 0) {
			throw new BookstoreException(Code.CREDIT_CARD_LIMIT_EXCEEDED);
		}
	}

	private void checkNumber(CreditCard card) throws BookstoreException {
		String number = card.getNumber();
		Matcher matcher = CREDIT_CARD_NUMBER_PATTERN.matcher(number);
		if (!matcher.matches()) {
			throw new BookstoreException(Code.INVALID_CREDIT_CARD_NUMBER);
		}
	}

	private void checkExpiration(CreditCard card) throws BookstoreException {
		Calendar calendar = Calendar.getInstance();
		if (card.getExpirationYear() < calendar.get(Calendar.YEAR)
				|| (card.getExpirationYear() == calendar.get(Calendar.YEAR)
				&& card.getExpirationMonth() < calendar.get(Calendar.MONTH) + 1)) {
			throw new BookstoreException(Code.CREDIT_CARD_EXPIRED);
		}
	}

	private void processOrder(final Order order) {
		logger.log(Level.INFO, "Processing order with number ''{0}''", order.getNumber());
		TimerTask task = new TimerTask() {

			@Override
			public void run() {
				if (order.getStatus() == Order.Status.processing) {
					logger.log(Level.INFO, "Shipping order with number ''{0}''", order.getNumber());
					order.setStatus(Order.Status.shipped);
				}
			}
		};
		new Timer().schedule(task, ORDER_PROCESS_TIME);
		order.setStatus(Order.Status.processing);
	}
}
