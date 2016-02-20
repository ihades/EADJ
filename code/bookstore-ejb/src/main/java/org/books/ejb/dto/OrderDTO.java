package org.books.ejb.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.books.persistence.dto.CustomerInfo;
@XmlRootElement(name = "order")
@XmlType(propOrder = {"number", "date", "amount", "status", "customerInfo", "address", "creditCard", "items"})
public class OrderDTO implements Serializable {
    
    @XmlType(name="status")
    @XmlEnum
    public enum Status {
        accepted, processing, shipped, canceled
    }

    private String number;
    private Date date;
    private BigDecimal amount;
    private Status status;
    private CustomerDTO customer;
    //private CustomerInfoUnused customerInfo = new CustomerInfoUnused();
    private CustomerInfo customerInfo;
    private AddressDTO address;
    private CreditCardDTO creditCard;
    private List<OrderItemDTO> items;

    public OrderDTO() {}

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
        //this.customerInfo = new CustomerInfoUnused(customer.getNumber(), customer.getEmail(), customer.getFirstName(), customer.getLastName());
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
    
    public CustomerInfo getCustomerInfo() {
        return customerInfo;
    }

    public void setCustomerInfo(CustomerInfo ci) {
        this.customerInfo = ci;
    }
    
    @XmlTransient
    public CustomerDTO getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerDTO customer) {
        this.customer = customer;
//        this.customerInfo.setNumber(customer.getNumber()); 
//        this.customerInfo.setEmail(customer.getEmail());
//        this.customerInfo.setFirstName(customer.getFirstName());
//        this.customerInfo.setLastName(customer.getLastName());
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
