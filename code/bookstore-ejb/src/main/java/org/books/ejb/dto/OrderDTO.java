package org.books.ejb.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
@XmlRootElement(name="order")
@XmlType(propOrder = {"number", "date", "amount", "status", "customer", "address", "creditCard", "items"})
public class OrderDTO implements Serializable {

    public enum Status {

        accepted, processing, shipped, canceled
    }

    private String number;
    private Date date;
    private BigDecimal amount;
    private Status status;
    private CustomerDTO customer;
    private AddressDTO address;
    private CreditCardDTO creditCard;
    private List<OrderItemDTO> items;

    public OrderDTO() {
    }

    public OrderDTO(String number, Date date, BigDecimal amount, Status status,
            CustomerDTO customer, AddressDTO address, CreditCardDTO creditCard, List<OrderItemDTO> items) {
        this.number = number;
        this.date = date;
        this.amount = amount;
        this.status = status;
        this.customer = customer;
        this.address = address;
        this.creditCard = creditCard;
        this.items = items;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public CustomerDTO getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerDTO customer) {
        this.customer = customer;
    }

    public AddressDTO getAddress() {
        return address;
    }

    public void setAddress(AddressDTO address) {
        this.address = address;
    }

    public CreditCardDTO getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(CreditCardDTO card) {
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
