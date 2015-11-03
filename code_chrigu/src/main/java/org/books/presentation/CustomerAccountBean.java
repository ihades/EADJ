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
import org.books.presentation.util.MessageFactory;

/**
 * FacesContext context = FacesContext.getCurrentInstance(); if
 * (!context.getExternalContext().getSessionMap().containsKey("customer")) {
 * jemand ist eingelogt. Um den User zu erhalten: Customer customer = (Customer)
 * context.getExternalContext().getSessionMap().get("customer"); }
 *
 * @author cb
 */
@Named(value = "customerAccountBean")
@SessionScoped
public class CustomerAccountBean implements Serializable {

    @Inject
    private Bookstore bookstore;

    public Customer getCustomer() {
        Customer customer;
        FacesContext context = FacesContext.getCurrentInstance();
        if (context.getExternalContext().getSessionMap().containsKey("customer")) {
            customer = (Customer) context.getExternalContext().getSessionMap().get("customer");
        } else {
            customer = new Customer();
        }
        return customer;
    }

    public void setCustomer(Customer customer) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getSessionMap().put("customerToRegister", customer);
    }

    public void setPassword(String password) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getSessionMap().put("password", password);
    }

    public void register() {
        FacesContext context = FacesContext.getCurrentInstance();
        if ((context.getExternalContext().getSessionMap().containsKey("customerToRegister"))
                && (context.getExternalContext().getSessionMap().containsKey("password"))) {
            Customer customerToRegister = (Customer) context.getExternalContext().getSessionMap().get("customerToRegister");
            String password = (String) context.getExternalContext().getSessionMap().get("password");
            try {
                bookstore.registerCustomer(customerToRegister, password);
                context.getExternalContext().getSessionMap().put("customer", customerToRegister);
            } catch (BookstoreException ex) {
                MessageFactory.error("registrationFailed");
            }
        }
    }

    public void change() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context.getExternalContext().getSessionMap().containsKey("customerToRegister")) {
            Customer customerToRegister = (Customer) context.getExternalContext().getSessionMap().get("customerToRegister");
            try {
                bookstore.updateCustomer(customerToRegister);
                context.getExternalContext().getSessionMap().put("customer", customerToRegister);
            } catch (BookstoreException ex) {
                MessageFactory.error("registrationFailed");
            }
        }
        if ((context.getExternalContext().getSessionMap().containsKey("customer"))
                && (context.getExternalContext().getSessionMap().containsKey("password"))) {
            String password = (String) context.getExternalContext().getSessionMap().get("password");
            Customer customer = (Customer) context.getExternalContext().getSessionMap().get("customerToRegister");
            try {
                bookstore.changePassword(customer.getEmail(), password);
            } catch (BookstoreException ex) {
                MessageFactory.error("registrationFailed");
            }
        }
    }

}
