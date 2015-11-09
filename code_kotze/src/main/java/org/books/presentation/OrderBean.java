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
import org.books.data.dto.OrderItemDTO;

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
    
    public String doIt(){
        if (!loginBean.isUserLoggedIn()) {
            return "userLogin";
        }
        return null;
    }
    
    public String confirm() {
        orderedBooks.add(shoppingBasketBean.getBooks());
        shoppingBasketBean.getBooks().clear();
        return "index";
    }
    
    
    
    
    
    
}
