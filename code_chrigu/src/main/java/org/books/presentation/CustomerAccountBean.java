/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.books.presentation;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import javax.inject.Inject;
import org.books.application.Bookstore;
import org.books.application.BookstoreException;
import org.books.data.entity.Customer;
import org.books.presentation.util.LoginException;
import org.books.presentation.util.MessageFactory;

@Named(value = "customerAccountBean")
@SessionScoped
public class CustomerAccountBean implements Serializable {

    @Inject
    private Bookstore bookstore;

    @Inject
    private LoginBean loginBean;

    @Inject
    private NavigationBean nav;

    private Customer customerToRegister = null;
    private String password;
    private String oldPassword;

    public Customer getCustomer() {
        try {
            return loginBean.getCustomer();
        } catch (LoginException e) {
            return null;
        }
    }

    public void setCustomer(Customer customer) {
        this.customerToRegister = customer;
    }

    public Customer getCustomerToRegister() {
        if (customerToRegister == null) {
            customerToRegister = new Customer();
        }
        return customerToRegister;
    }

    public void setCustomerToRegister(Customer customerToRegister) {
        this.customerToRegister = customerToRegister;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String password) {
        this.oldPassword = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String register() {
        if ((customerToRegister != null) && (password != null)) {
            try {
                bookstore.registerCustomer(customerToRegister, password);
                loginBean.setUser(customerToRegister);
                customerToRegister = null;
            } catch (BookstoreException ex) {
                MessageFactory.error("registrationFailed");
            }
        }
        return nav.getOutcome();
    }

    public String change() {

        String returnValue = "success";
        if ((customerToRegister != null)) {
            try {
                bookstore.updateCustomer(customerToRegister);
                loginBean.setUser(customerToRegister);
            } catch (BookstoreException ex) {
                MessageFactory.error("registrationFailed");
                returnValue = null;
            } finally {
                customerToRegister = null;
            }
        }
        if (loginBean.isUserLoggedIn() && (password != null) && (oldPassword != null)) {
            try {
                Customer customer = loginBean.getCustomer();
                bookstore.authenticateCustomer(customer.getEmail(), oldPassword);
                bookstore.changePassword(customer.getEmail(), password);
            } catch (BookstoreException | LoginException ex) {
                MessageFactory.error("passwordChangeFailed");
                returnValue = null;
            } finally {
                password = null;
                oldPassword = null;
            }
        }
        if (nav.getHasOutcome() && returnValue != null) {
            return nav.getOutcome();
        } else {
            return returnValue;
        }
    }

    public String cancel() {
        if (nav.getHasOutcome()) {
            return nav.getOutcome();
        } else {
            return "cancel";
        }
    }
}
