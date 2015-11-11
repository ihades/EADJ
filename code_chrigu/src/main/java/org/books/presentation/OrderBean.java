/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.books.presentation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.books.application.Bookstore;
import org.books.application.BookstoreException;
import org.books.data.dto.OrderItemDTO;
import org.books.presentation.util.LoginException;
import org.books.presentation.util.MessageFactory;

/**
 *
 * @author edm
 */
@Named("orderBean")
@SessionScoped
public class OrderBean implements Serializable {
   
    private List<List<OrderItemDTO>> orderedBooks = new ArrayList<>();
    
    @Inject 
    private ShoppingBasketBean shoppingBasketBean;
    
    @Inject
    private LoginBean loginBean;
    
    @Inject
    private Bookstore bookstore;
    
    public String confirm() {
        try {
            bookstore.placeOrder(loginBean.getCustomer().getEmail(), shoppingBasketBean.getBooks());
            shoppingBasketBean.getBooks().clear();
            return "success";
        } catch (BookstoreException ex) {
            MessageFactory.error("exceptionClassBookstoreException");
            
        } catch (LoginException ex) {
            MessageFactory.error("loginFailed");
        }
        return null;
    }
    
    
    
    
    
    
}
