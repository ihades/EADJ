package org.books.api.api.entities;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.books.ejb.dto.CustomerDTO;

@XmlRootElement(name="registration")
@XmlType(propOrder = {"customer", "password"})
public class Registration {

    private String password;
    @XmlElementRef(type = CustomerDTO.class, name="customer", required = true)
    private CustomerDTO customer;

    public Registration() {
    }

    public Registration(String password, CustomerDTO customer) {
        this.password = password;
        this.customer = customer;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public CustomerDTO getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerDTO customer) {
        this.customer = customer;
    }

}
