/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.books.presentation;

import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author edm
 */
@Named("orderBean")
@SessionScoped
public class OrderBean implements Serializable {
   
    
    @Inject
    private LoginBean loginBean;
    
    public String doIt(){
        if (!loginBean.isUserLoggedIn()) {
            return "userLogin";
        }
        return null;
    }
    
}
