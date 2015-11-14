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
import org.books.data.dto.OrderDTO;
import org.books.data.dto.OrderInfo;
import org.books.data.dto.OrderItemDTO;
import org.books.data.entity.Order;
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

    private static final String ORDER_DETAILS = "orderDetails";
    private String searchString;
    private List<OrderInfo> orderInfo;
    private OrderInfo selectedOrder;

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public List<OrderInfo> getOrderInfo() {
        return orderInfo;
    }

    public void setOrderInfo(List<OrderInfo> OrderInfo) {
        this.orderInfo = OrderInfo;
    }

    public String setSelectedOrder(OrderInfo selectedOrder) {
        this.selectedOrder = selectedOrder;
        return ORDER_DETAILS;
    }

    public String cancelOrder(OrderInfo order) {
        try {
            bookstore.cancelOrder(order.getNumber());
        } catch (BookstoreException e) {
            MessageFactory.error(e);
        }
        search();
        return "userDetails";
    }

    public Boolean canCancelOrder(OrderInfo order) {
        return ((order.getStatus() != Order.Status.canceled)
                && (order.getStatus() != Order.Status.shipped));
    }

    public OrderDTO getSelectedOrder() {
        try {
            return bookstore.findOrder(selectedOrder.getNumber());
        } catch (BookstoreException e) {
            MessageFactory.error(e);
        }
        return null;
    }

    public String search() {
        try {
            orderInfo = bookstore.searchOrders(loginBean.getCustomer().getEmail(), Integer.parseInt(searchString));
        } catch (BookstoreException ex) {
            MessageFactory.error("exceptionClassBookstoreException");

        } catch (LoginException ex) {
            MessageFactory.error("loginFailed");
        }
        return null;
    }

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
