package org.books.ejb.impl;

import org.books.ejb.exception.JmsRuntimeException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Init;
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
import org.books.persistence.dao.CustomerDao;
import org.books.persistence.dao.OrderDao;
import org.books.persistence.dto.OrderInfo;
import org.books.persistence.entity.Book;
import org.books.persistence.entity.Customer;
import org.books.persistence.entity.Order;
import org.books.persistence.entity.OrderItem;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import java.lang.reflect.Type;

/**
 *
 * @author cb
 */
@Stateless(name = "OrderService")
public class OrderServiceBean implements OrderServiceLocal, OrderServiceRemote {

    @EJB
    private OrderDao orderDao;
    @EJB
    private CustomerDao customerDao;
    @EJB
    private BookDao bookDao;

    @Resource(mappedName = "jms/connectionFactory")
    private static ConnectionFactory connectionFactory;

    @Resource(mappedName = "jms/orderQueue")
    private static Queue orderQueue;

    private JMSContext context;
    private final ModelMapper modelMapper = new ModelMapper();

    @Init
    public void init() throws Exception {
        context = connectionFactory.createContext();
    }

    @Override
    public void cancelOrder(String orderNr) throws OrderNotFoundException, OrderAlreadyShippedException {
        try {
            //Enshure Order Existency and State
            MapMessage message = context.createMapMessage();
            message.setJMSType("cancelOrder");
            message.setString("orderNumber", orderNr);
            context.createProducer().send(orderQueue, message);
        } catch (JMSException ex) {
            throw new JmsRuntimeException("Canceling of Order " + orderNr + " was not possible!", ex);
        }
    }

    @Override
    public OrderDTO findOrder(String orderNr) throws OrderNotFoundException {
        Order order = orderDao.getByNumber(orderNr);
        if (order == null) {
            throw new OrderNotFoundException();
        }
        return modelMapper.map(order, OrderDTO.class);
    }

    @Override
    public OrderDTO placeOrder(String customerNr, List<OrderItemDTO> items) throws CustomerNotFoundException, BookNotFoundException, PaymentFailedException {
        Customer customer = findCustomer(customerNr);
        List<OrderItem> orderItems = generateOrderItems(items);
        Order order = new Order("TEMPORARY",
                Calendar.getInstance().getTime(),
                calculateAmount(orderItems),
                Order.Status.accepted,
                customer,
                customer.getAddress(),
                customer.getCreditCard(),
                orderItems);
        verifyPayment(order);
        orderDao.create(order);
        order.setNumber("O-" + order.getId());
        orderDao.update(order);
        processOrder(order);
        return modelMapper.map(order, OrderDTO.class);
    }

    @Override
    public List<OrderInfo> searchOrders(String customerNr, Integer year) throws CustomerNotFoundException {
        return orderDao.searchByCustomerAndYear(findCustomer(customerNr), year);
    }

    private Customer findCustomer(String customerNr) throws CustomerNotFoundException {
        Customer customer = customerDao.findByNumber(customerNr);
        if (customer == null) {
            throw new CustomerNotFoundException();
        }
        return customer;
    }

    private List<OrderItem> generateOrderItems(final List<OrderItemDTO> orderItemDTOs) throws BookNotFoundException {
        final List<OrderItem> result = new ArrayList<>(orderItemDTOs.size());
        for (OrderItemDTO orderItemDTO : orderItemDTOs) {
            result.add(new OrderItem(
                    findBookByIsbn(orderItemDTO.getBook().getIsbn()),
                    orderItemDTO.getQuantity()));
        }
        return result;
    }

    private Book findBookByIsbn(String isbn) throws BookNotFoundException {
        Book book = bookDao.getByIsbn(isbn);
        if (book == null) {
            throw new BookNotFoundException();
        }
        return book;
    }

    private BigDecimal calculateAmount(List<OrderItem> orderItems) {
        final BigDecimal result = new BigDecimal(0);
        orderItems.forEach(item -> result.add(item.getPrice()));
        return result;
    }

    private void verifyPayment(Order order) throws PaymentFailedException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void processOrder(Order order) {
        try {
            MapMessage message = context.createMapMessage();
            message.setJMSType("placeOrder");
            message.setString("orderNumber", order.getNumber());
            context.createProducer().send(orderQueue, message);
        } catch (JMSException ex) {
            //force Transaction Rollback
            throw new JmsRuntimeException("Processing of Order " + order.getNumber() + " was not possible!", ex);
        }
    }

}
