package org.books.persistence.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import org.books.persistence.entity.Order;

public class OrderInfo implements Serializable {

    public static final String QUERY_ORDER_BY_CUSTOMER_AND_DATE = "org.books.persistence.OrderByCustomerAndDate";
    public static final String QUERY_ORDER_BY_CUSTOMER_AND_DATE_PARAM_BEGIN = "begin";
    public static final String QUERY_ORDER_BY_CUSTOMER_AND_DATE_PARAM_END = "end";
    public static final String QUERY_ORDER_BY_CUSTOMER_AND_DATE_PARAM_CUSTOMER = "customer";

    private Long id;

    private String number;
    private Date date;
    private BigDecimal amount;
    private Order.Status status;

    public OrderInfo(Long id, String number, Date date, BigDecimal amount, Order.Status status) {
        this.id = id;
        this.number = number;
        this.date = date;
        this.amount = amount;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public Date getDate() {
        return date;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Order.Status getStatus() {
        return status;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setStatus(Order.Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [Id: " + getId() + " Status: " + getStatus() + "]";
    }

}
