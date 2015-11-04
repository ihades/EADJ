/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.books.presentation;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    public Customer getCustomer() {
        try {
            return loginBean.getCustomer();
        } catch (LoginException e) {
            MessageFactory.error(e.getLocalizedMessage());
        }
        return null;
    }

    public void setCustomer(Customer customer) {
        this.customerToRegister = customer;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void register() {
        if ((customerToRegister != null) && (password != null)) {
            try {
                bookstore.registerCustomer(customerToRegister, password);
                reLoginUser();
            } catch (BookstoreException ex) {
                MessageFactory.error("registrationFailed");
            }
        }
    }

    public void change() {
        if ((customerToRegister != null)) {
            try {
                bookstore.updateCustomer(customerToRegister);
                reLoginUser();
            } catch (BookstoreException ex) {
                MessageFactory.error("registrationFailed");
            }
        }
        if (loginBean.isUserLoggedIn() && (password != null)) {
            try {
                Customer customer = null;
                try {
                    customer = loginBean.getCustomer();
                } catch (LoginException ex) {
                    Logger.getLogger(CustomerAccountBean.class.getName()).log(Level.SEVERE, null, ex);
                }
                bookstore.changePassword(customer.getEmail(), password);
            } catch (BookstoreException ex) {
                MessageFactory.error("registrationFailed");
            }
        }
    }

    private void reLoginUser() {
        loginBean.logOut();
        loginBean.setUsername(customerToRegister.getEmail());
        loginBean.setPassword(password);
        loginBean.logIn();
    }

}
