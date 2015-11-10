/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.books.presentation;

import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author edm
 */
@Named("navigationBean")
@SessionScoped
public class Navigation implements Serializable {
    
    private String target = "index";
    private String source = "index";

    public String lastTarget() {
        System.out.println("target returned= "+target);
        return target+".xhtml?faces-redirect=true";
    }
    
    public String getTarget() {
        return target;
    }
    
    public void setMyTarget(String t){
        target = t;
    }
    
    public void setTarget(String t) {
        System.out.println("target set: "+t);
        target = t;
    }

    public  String getSource() {
        return source;
    }

    public void setSource(String s) {
        source = s;
    }
    
    
    
    
}
