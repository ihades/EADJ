/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.books.presentation;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import org.books.application.Bookstore;
import org.books.application.BookstoreException;
import org.books.data.entity.Customer;
import org.books.presentation.util.LoginException;
import org.books.presentation.util.MessageFactory;
import org.books.presentation.util.Navigation;


/**
 * FacesContext context = FacesContext.getCurrentInstance(); if
 * (!context.getExternalContext().getSessionMap().containsKey("customer")) {
 * jemand ist eingelogt. Um den User zu erhalten: Customer customer = (Customer)
 * context.getExternalContext().getSessionMap().get("customer"); }
 *
 * @author cb
 */
@Named(value = "loginBean")
@SessionScoped
public class LoginBean implements Serializable {

    @Inject
    private Navigation navigation;
    
    @Inject
    private Bookstore bookstore;

    private String username, password;

    private Customer customer = null;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String logIn() {
        try {
            bookstore.authenticateCustomer(username, password);
            customer = bookstore.findCustomer(username);
            return navigation.lastTarget();
        } catch (BookstoreException ex) {
            MessageFactory.error("loginFailed");
        }
        return null;
    }

    public String logOut() {
        customer = null;
        return "success";
    }

    public boolean isUserLoggedIn() {
        return customer != null;
    }

    public Customer getCustomer() throws LoginException {
        if (customer == null) {
            throw new LoginException();
        }
        return customer;
    }
}