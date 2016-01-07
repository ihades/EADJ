package org.books.ejb.impl;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Queue;
import org.books.ejb.OrderServiceLocal;
import org.books.ejb.OrderServiceRemote;
import org.books.ejb.dto.OrderDTO;
import org.books.ejb.dto.OrderItemDTO;
import org.books.ejb.exception.BookNotFoundException;
import org.books.ejb.exception.CustomerNotFoundException;
import org.books.ejb.exception.OrderAlreadyShippedException;
import org.books.ejb.exception.OrderNotFoundException;
import org.books.ejb.exception.PaymentFailedException;
import org.books.persistence.dao.BookDao;
import org.books.persistence.dto.OrderInfo;

/**
 *
 * @author cb
 */
@Stateless(name = "OrderService")
public class OrderServiceBean implements OrderServiceLocal, OrderServiceRemote {

    @EJB
    private BookDao bookDao;

    @Resource(mappedName = "jms/connectionFactory")
    private static ConnectionFactory connectionFactory;

    @Resource(mappedName = "jms/orderQueue")
    private static Queue orderQueue;

    @Override
    public void cancelOrder(String orderNr) throws OrderNotFoundException, OrderAlreadyShippedException {
        try {
            //Enshure Order Existency and State
            JMSContext context = connectionFactory.createContext();
            MapMessage message = context.createMapMessage();
            message.setJMSType("cancelOrder");
            message.setString("orderNumber", orderNr);
            context.createProducer().send(orderQueue, message);
        } catch (JMSException ex) {
            Logger.getLogger(OrderServiceBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public OrderDTO findOrder(String orderNr) throws OrderNotFoundException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public OrderDTO placeOrder(String customerNr, List<OrderItemDTO> items) throws CustomerNotFoundException, BookNotFoundException, PaymentFailedException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<OrderInfo> searchOrders(String customerNr, Integer year) throws CustomerNotFoundException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
