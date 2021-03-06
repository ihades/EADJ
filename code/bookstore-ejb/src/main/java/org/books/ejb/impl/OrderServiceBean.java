package org.books.ejb.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Queue;
import org.books.ejb.AmazonCatalogService;
import org.books.ejb.AmazonCatalogServiceLocal;
import org.books.ejb.OrderServiceLocal;
import org.books.ejb.OrderServiceRemote;
import org.books.ejb.dto.OrderDTO;
import org.books.ejb.dto.OrderItemDTO;
import org.books.ejb.exception.BookNotFoundException;
import org.books.ejb.exception.CustomerNotFoundException;
import org.books.ejb.exception.JmsRuntimeException;
import org.books.ejb.exception.OrderAlreadyShippedException;
import org.books.ejb.exception.OrderNotFoundException;
import org.books.ejb.exception.PaymentFailedException;
import org.books.persistence.dao.BookDao;
import org.books.persistence.dao.CustomerDao;
import org.books.persistence.dao.OrderDao;
import org.books.persistence.dto.CustomerInfo;
import org.books.persistence.dto.OrderInfo;
import org.books.persistence.entity.Book;
import org.books.persistence.entity.Customer;
import org.books.persistence.entity.Order;
import org.books.persistence.entity.OrderItem;
import org.modelmapper.ModelMapper;

/**
 *
 * @author cb
 */
@Stateless(name = "OrderService")
public class OrderServiceBean implements OrderServiceLocal, OrderServiceRemote {

    private final static Logger LOGGER = Logger.getLogger(OrderServiceBean.class.getName());

    @EJB
    private OrderDao orderDao;
    @EJB
    private CustomerDao customerDao;
    @EJB
    private BookDao bookDao;

    @EJB(beanInterface = AmazonCatalogServiceLocal.class)
    private AmazonCatalogService amazon;

    @Resource(mappedName = "jms/connectionFactory")
    private ConnectionFactory connectionFactory;

    @Resource(mappedName = "jms/orderQueue")
    private Queue orderQueue;

    private JMSContext context;
    private final ModelMapper modelMapper = new ModelMapper();
    private final static BigDecimal PAYMENT_LIMIT = new BigDecimal(1000);

    @PostConstruct
    public void postctor() {
        context = connectionFactory.createContext();
    }

    @Override
    public void cancelOrder(String orderNr) throws OrderNotFoundException, OrderAlreadyShippedException {
        try {
            enshureExistencyAndCancelOrder(orderNr);
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
        Order order = orderDao.findByNumber(orderNr);
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
        new CreditCardNumberValidator(PAYMENT_LIMIT).validateCreditCard(order.getCreditCard(), order.getAmount());
        orderDao.create(order);
        order.setNumber("O-" + order.getId());
        orderDao.update(order);
        processOrder(order);
        LOGGER.log(Level.INFO, "Customer {} placed Order {} for {}", new Object[]{order.getCustomer().getNumber(), order.getNumber(), order.getAmount()});
        OrderDTO od = modelMapper.map(order, OrderDTO.class);
        od.setCustomerInfo(new CustomerInfo(customerNr, customer.getFirstName(), customer.getLastName(), customer.getEmail()));
        return od;
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
                    findBookByIsbnOrCreateNew(orderItemDTO.getBook().getIsbn()),
                    orderItemDTO.getQuantity()));
        }
        return result;
    }

    private Book findBookByIsbnOrCreateNew(String isbn) throws BookNotFoundException {
        Book book = bookDao.findByIsbn(isbn);
        if (book == null) {
            book = amazon.findByIsbn(isbn);
            bookDao.create(book);
        }
        return book;
    }

    private BigDecimal calculateAmount(List<OrderItem> orderItems) {
        BigDecimal result = new BigDecimal(0);
        //pre-java-8 for there is an incompatibility on OSX with a class. i firgot which one.
        for (OrderItem next : orderItems) {
            result = result.add(next.getBook().getPrice().multiply(new BigDecimal(next.getQuantity())));
        }
        return result;
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

    private void enshureExistencyAndCancelOrder(String orderNr) throws OrderNotFoundException, OrderAlreadyShippedException {
        Order order = orderDao.findByNumberPessimisticLock(orderNr);
        if (order == null) {
            throw new OrderNotFoundException();
        }
        if (order.getStatus() != Order.Status.shipped) {
            order.setStatus(Order.Status.canceled);
        } else {
            throw new OrderAlreadyShippedException();
        }
    }

}
