package org.books.ejb;

import java.io.Serializable;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.books.ejb.dto.OrderDTO;
import org.books.ejb.dto.OrderItemDTO;
import org.books.ejb.exception.BookNotFoundException;
import org.books.ejb.exception.CustomerNotFoundException;
import org.books.ejb.exception.OrderAlreadyShippedException;
import org.books.ejb.exception.OrderNotFoundException;
import org.books.ejb.exception.PaymentFailedException;
import org.books.persistence.dto.OrderInfo;

/**
 *
 * @author cb
 */
public interface OrderService extends Serializable {

    /**
     * Cancels an order.
     *
     * @param orderNr - the number of the order
     * @throws OrderNotFoundException - if no order with the specified number
     * exists
     * @throws OrderAlreadyShippedException - if the order has already been
     * shipped
     */
    void cancelOrder(@NotNull String orderNr) throws OrderNotFoundException, OrderAlreadyShippedException;

    /**
     * Finds an order by number.
     *
     * @param orderNr - the number of the order
     * @return the data of the found order
     * @throws OrderNotFoundException - if no order with the specified number
     * exists
     */
    OrderDTO findOrder(String orderNr) throws OrderNotFoundException;

    /**
     * Places an order on the bookstore.
     *
     * @param customerNr - the number of the customer
     * @param items - the order items
     * @return the data of the placed order
     * @throws CustomerNotFoundException - if no customer with the specified
     * number exists
     * @throws BookNotFoundException - if an order item references a book that
     * does not exist
     * @throws PaymentFailedException - if a payment error occurs
     */
    OrderDTO placeOrder(String customerNr, @Size(min = 1) List<OrderItemDTO> items) throws CustomerNotFoundException, BookNotFoundException, PaymentFailedException;

    /**
     * Searches for orders by customer and year.
     *
     * @param customerNr - the Customernumber of the customer
     * @param year - the year of the orders
     * @return a list of matching orders (may be empty)
     * @throws CustomerNotFoundException - if no customer with the specified
     * email address exists
     */
    List<OrderInfo> searchOrders(String customerNr, Integer year) throws CustomerNotFoundException;
}
