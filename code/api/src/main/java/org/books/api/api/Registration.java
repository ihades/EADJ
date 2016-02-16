package org.books.api.api;

import javax.xml.bind.annotation.XmlRootElement;
import org.books.ejb.dto.CustomerDTO;

@XmlRootElement
public class Registration {

    private String password;
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
