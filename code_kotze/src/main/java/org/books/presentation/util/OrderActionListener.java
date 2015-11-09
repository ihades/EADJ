/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.books.presentation.util;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;

/**
 *
 * @author edm
 */
public class OrderActionListener implements ActionListener {

    @Override
    public void processAction(ActionEvent event) throws AbortProcessingException {
        System.out.println("Blablabla");
    }
    
}
