/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.books.presentation;

import javax.faces.bean.NoneScoped;
import javax.inject.Named;

/**
 *
 * @author edm
 */
@Named("quantityBean")
@NoneScoped
public class Quantity {
    private int quantity = 0;
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int q) {
        this.quantity = q;
    }
}
