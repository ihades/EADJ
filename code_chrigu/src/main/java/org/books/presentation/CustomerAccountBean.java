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

    private Customer customerToRegister = null;

    private String password = null;
    private String oldPassword;

    public Customer getCustomer() {
        try {
            return loginBean.getCustomer();
        } catch (LoginException e) {
            return new Customer();
        }
    }

    public void setCustomer(Customer customer) {
        this.customerToRegister = customer;
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

    public void register() {
        if ((customerToRegister != null) && (password != null)) {
            try {
                bookstore.registerCustomer(customerToRegister, password);
                loginBean.setUser(customerToRegister);
            } catch (BookstoreException ex) {
                MessageFactory.error("registrationFailed");
            }
        }
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
        return returnValue;
    }
}
