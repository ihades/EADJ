package org.books.persistence.entity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.books.persistence.dto.CustomerInfo;

@NamedQueries({
    @NamedQuery(name = "Order.findbyNumber", query = "SELECT o "
            + "FROM Order o "
            + "WHERE UPPER(o.number) = UPPER(:number)")
})
@Entity
@Table(name = "BOOKORDER")
public class Order extends IDObject {

    public transient static String ORDER_FIND_BY_NUMBER_PARAM = "number";

    public enum Status {

        accepted, processing, shipped, canceled
    }

    @Column(name = "ORDER_NUMBER",
            unique = true,
            nullable = false)
    private String number;

    @Column(name = "ORDER_DATE",
            nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @Column(nullable = false,
            precision = 7,
            scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne(optional = false,
            cascade = CascadeType.REFRESH)
    @JoinColumn(name = "CUSTOMER_ID")
    private Customer customer;

    @Embedded
    private Address address;

    @Embedded
    private CreditCard creditCard;

    @OneToMany(fetch = FetchType.EAGER,
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @JoinColumn(name = "ORDER_ID")
    private List<OrderItem> items;

    public Order() {
    }

    public Order(String number, Date date, BigDecimal amount, Status status,
            Customer customer, Address address, CreditCard creditCard, List<OrderItem> items) {
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
        this.amount = amount.setScale(2, RoundingMode.HALF_UP);
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Customer getCustomer() {
        if (customer == null) {
            customer = new Customer();
        }
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setCustomerInfo(CustomerInfo info) {
        //this.customerInfo = info;
    }

    public Address getAddress() {
        if (address == null) {
            address = new Address();
        }
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public CreditCard getCreditCard() {
        if (creditCard == null) {
            creditCard = new CreditCard();
        }
        return creditCard;
    }

    public void setCreditCard(CreditCard card) {
        this.creditCard = card;
    }

    public List<OrderItem> getItems() {
        if (items == null) {
            items = new ArrayList<>();
        }
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public void addItem(OrderItem item) {
        this.getItems().add(item);
    }
}
