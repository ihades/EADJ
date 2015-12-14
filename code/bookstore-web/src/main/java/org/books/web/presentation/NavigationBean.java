/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.books.web.presentation;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Deque;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author edm
 */
@Named("navigationBean")
@SessionScoped
public class NavigationBean implements Serializable {

    private final Deque<String> stack = new ArrayDeque<>();

    public void setOutcome(String outcome) {
        stack.push(outcome);
    }

    public Boolean getHasOutcome() {
        return (stack.peek() != null);
    }

    public String getOutcome() {
        return stack.poll();
    }

}
