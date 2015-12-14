package org.books.web.data.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.books.web.data.entity.Address;
import org.books.web.data.entity.CreditCard;
import org.books.web.data.entity.Order;
import org.books.web.data.entity.Order.Status;
import org.books.web.data.entity.OrderItem;

public class OrderDTO extends OrderInfo {

	private CustomerInfo customer;
	private Address address;
	private CreditCard creditCard;
	private List<OrderItemDTO> items;

	public OrderDTO() {
	}

	public OrderDTO(String number, Date date, BigDecimal amount, Status status,
			CustomerInfo customer, Address address, CreditCard creditCard, List<OrderItemDTO> items) {
		super(number, date, amount, status);
		this.customer = customer;
		this.address = address;
		this.creditCard = creditCard;
		this.items = items;
	}

	public OrderDTO(Order order) {
		super(order);
		this.customer = new CustomerInfo(order.getCustomer());
		this.address = order.getAddress();
		this.creditCard = order.getCreditCard();
		this.items = new ArrayList<>();
		for (OrderItem item : order.getItems()) {
			BookInfo book = new BookInfo(item.getBook().getIsbn(), item.getBook().getTitle(), item.getPrice());
			items.add(new OrderItemDTO(book, item.getQuantity()));
		}
	}

	public CustomerInfo getCustomer() {
		return customer;
	}

	public void setCustomer(CustomerInfo customer) {
		this.customer = customer;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public CreditCard getCreditCard() {
		return creditCard;
	}

	public void setCreditCard(CreditCard card) {
		this.creditCard = card;
	}

	public List<OrderItemDTO> getItems() {
		if (items == null) {
			items = new ArrayList<>();
		}
		return items;
	}

	public void setItems(List<OrderItemDTO> items) {
		this.items = items;
	}
}
