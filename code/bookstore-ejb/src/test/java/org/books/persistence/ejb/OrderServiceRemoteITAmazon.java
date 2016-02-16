/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.books.persistence.ejb;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import org.books.ejb.CatalogService;
import org.books.ejb.CustomerService;
import org.books.ejb.OrderService;
import org.books.ejb.dto.AddressDTO;
import org.books.ejb.dto.BookDTO;
import org.books.ejb.dto.CreditCardDTO;
import org.books.ejb.dto.CustomerDTO;
import org.books.ejb.dto.OrderDTO;
import org.books.ejb.dto.OrderDTO.Status;
import org.books.ejb.dto.OrderItemDTO;
import org.books.ejb.exception.BookAlreadyExistsException;
import org.books.ejb.exception.BookNotFoundException;
import org.books.ejb.exception.CustomerAlreadyExistsException;
import org.books.ejb.exception.CustomerNotFoundException;
import org.books.ejb.exception.OrderAlreadyShippedException;
import org.books.ejb.exception.OrderNotFoundException;
import org.books.ejb.exception.PaymentFailedException;
import org.books.ejb.exception.ValidationException;
import org.books.persistence.dto.OrderInfo;
import static org.books.persistence.ejb.Util.numbGen;
import org.junit.Assert;
import static org.junit.Assert.fail;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class OrderServiceRemoteITAmazon {

    private int timerTimeoutForShipment = 35000;

    private OrderService orderService;

    private CustomerService customerService;

    private CatalogService catalogService;

    private final AddressDTO addressDTO = new AddressDTO("Wasserschloss 42",
            "Bern",
            "3011",
            "Switzerland");

    private final CreditCardDTO validCreditCardDTO = new CreditCardDTO(CreditCardDTO.Type.MasterCard,
            "5555555555554444",
            2,
            2017);
    private final CreditCardDTO invalidCreditCardDTO = new CreditCardDTO(CreditCardDTO.Type.MasterCard,
            "5220754670550780",
            2,
            2017);
    private final CreditCardDTO expiredCreditCardDTO = new CreditCardDTO(CreditCardDTO.Type.MasterCard,
            "5220754670550780",
            2,
            2014);

    private CustomerDTO customerDTO = new CustomerDTO(numbGen() + "chrigu.b@gmail.com",
            "Erwin",
            "Bazong",
            null,
            addressDTO,
            validCreditCardDTO);

    //should be different from already existing books in db.
    private BookDTO bookDTO = new BookDTO(numbGen(), "Java9", "Bill Gates", "M$-Press", new Integer(2015), BookDTO.Binding.Hardcover, 1000, new BigDecimal("33.3"));
    private OrderItemDTO orderItemDTO = new OrderItemDTO(bookDTO, bookDTO.getPrice(), 1);

    private List<OrderItemDTO> orderList = new ArrayList<OrderItemDTO>();
    private OrderDTO testOrderDTO;

    @BeforeClass
    public void setup() throws Exception {
        InitialContext context = new InitialContext(Util.getInitProperties());
        orderService = (OrderService) context.lookup(Util.ORDER_SERVICE_NAME);
        customerService = (CustomerService) context.lookup(Util.CUSTOMER_SERVICE_NAME);
        catalogService = (CatalogService) context.lookup(Util.CATALOG_SERVICE_NAME);
        bookDTO = catalogService.findBook("978-1585427659");
    }

    @BeforeTest
    public void init() throws CustomerAlreadyExistsException, CustomerNotFoundException {
        orderList.add(orderItemDTO);
    }

    @Test(expectedExceptions = {BookNotFoundException.class})
    public void placeOrderWithWrongBook() throws BookNotFoundException, CustomerAlreadyExistsException, CustomerNotFoundException, PaymentFailedException {
        customerDTO = customerService.registerCustomer(customerDTO, "PW");
        orderService.placeOrder(customerDTO.getNumber(), orderList);

    }

    @Test(dependsOnMethods = {"placeOrderWithWrongBook"}, expectedExceptions = {ValidationException.class})
    public void placeEmptyOrder() throws BookAlreadyExistsException, CustomerNotFoundException, BookNotFoundException, PaymentFailedException, OrderNotFoundException {
        orderList.clear();
        catalogService.addBook(bookDTO);
        OrderDTO orderDTO = orderService.placeOrder(customerDTO.getNumber(), orderList);
    }

    @Test(dependsOnMethods = {"placeEmptyOrder"})
    public void placeOrderWithGoodBook() throws BookAlreadyExistsException, CustomerNotFoundException, BookNotFoundException, PaymentFailedException, OrderNotFoundException {
        orderList.add(orderItemDTO);
        testOrderDTO = orderService.placeOrder(customerDTO.getNumber(), orderList);
        Assert.assertEquals(testOrderDTO.getStatus(), Status.accepted);
    }

    @Test(dependsOnMethods = {"placeOrderWithGoodBook"})
    public void placeOrderWithTooExpensiveBook() throws BookAlreadyExistsException, CustomerNotFoundException, BookNotFoundException, PaymentFailedException, OrderNotFoundException {

        List<OrderItemDTO> ld = new ArrayList<>();
        BookDTO bd = new BookDTO(numbGen(), "Java10", "Rübezahl", "Alphabet-Press", new Integer(2015), BookDTO.Binding.Hardcover, 1000, new BigDecimal("500.0"));
        catalogService.addBook(bd);
        OrderItemDTO oid = new OrderItemDTO(bd, new BigDecimal("500.0"), 3);
        ld.add(oid);
        try {
            orderService.placeOrder(customerDTO.getNumber(), ld);
            fail("PaymentFailedException.Code.PAYMENT_LIMIT_EXCEEDED expected");
        } catch (PaymentFailedException pex) {
            Assert.assertEquals(PaymentFailedException.Code.PAYMENT_LIMIT_EXCEEDED, pex.getCode());
        }

    }

    @Test(dependsOnMethods = {"placeOrderWithTooExpensiveBook"})
    public void placeOrderWithWrongCreditCard() throws BookAlreadyExistsException, CustomerNotFoundException, BookNotFoundException, PaymentFailedException, OrderNotFoundException, CustomerAlreadyExistsException {
        customerDTO.setCreditCard(invalidCreditCardDTO);
        customerService.updateCustomer(customerDTO);
        List<OrderItemDTO> ld = new ArrayList<>();
        BookDTO bd = new BookDTO(numbGen(), "Java10", "Rübezahl", "Alphabet-Press", new Integer(2015), BookDTO.Binding.Hardcover, 1000, new BigDecimal("500.0"));
        catalogService.addBook(bd);
        OrderItemDTO oid = new OrderItemDTO(bd, new BigDecimal("500.0"), 1);
        ld.add(oid);
        try {
            orderService.placeOrder(customerDTO.getNumber(), ld);
            fail("PaymentFailedException.Code.INVALID_CREDIT_CARD expected");
        } catch (PaymentFailedException pex) {
            Assert.assertEquals(PaymentFailedException.Code.INVALID_CREDIT_CARD, pex.getCode());
        }
        customerDTO.setCreditCard(validCreditCardDTO);
        customerService.updateCustomer(customerDTO);
    }

    @Test(dependsOnMethods = {"placeOrderWithTooExpensiveBook"})
    public void placeOrderWithExpiredCreditCard() throws BookAlreadyExistsException, CustomerNotFoundException, BookNotFoundException, PaymentFailedException, OrderNotFoundException, CustomerAlreadyExistsException {
        customerDTO.setCreditCard(expiredCreditCardDTO);
        customerService.updateCustomer(customerDTO);
        List<OrderItemDTO> ld = new ArrayList<>();
        BookDTO bd = new BookDTO(numbGen(), "Java10", "Rübezahl", "Alphabet-Press", new Integer(2015), BookDTO.Binding.Hardcover, 1000, new BigDecimal("500.0"));
        catalogService.addBook(bd);
        OrderItemDTO oid = new OrderItemDTO(bd, new BigDecimal("500.0"), 1);
        ld.add(oid);
        try {
            orderService.placeOrder(customerDTO.getNumber(), ld);
            fail("PaymentFailedException.Code.CREDIT_CARD_EXPIRED expected");
        } catch (PaymentFailedException pex) {
            Assert.assertEquals(PaymentFailedException.Code.CREDIT_CARD_EXPIRED, pex.getCode());
        }
        customerDTO.setCreditCard(validCreditCardDTO);
        customerService.updateCustomer(customerDTO);
    }

    @Test(dependsOnMethods = {"placeOrderWithExpiredCreditCard"})
    public void searchOrder() throws CustomerNotFoundException {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        int year = cal.get(Calendar.YEAR);
        List<OrderInfo> orderInfos = orderService.searchOrders(customerDTO.getNumber(), year);
        Assert.assertEquals(1, orderInfos.size());

    }

    @Test(dependsOnMethods = {"searchOrder"})
    public void findOrder() throws OrderNotFoundException {
        OrderDTO orderDTO = orderService.findOrder(testOrderDTO.getNumber());
        Assert.assertEquals(testOrderDTO.getNumber(), orderDTO.getNumber());
    }

    @Test(dependsOnMethods = {"findOrder"}, expectedExceptions = OrderAlreadyShippedException.class)
    public void cancelOrderExpectException() throws OrderNotFoundException, OrderAlreadyShippedException {
        orderService.cancelOrder(testOrderDTO.getNumber());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Logger.getLogger(OrderServiceRemoteITAmazon.class.getName()).log(Level.SEVERE, null, ex);
        }
        Assert.assertEquals(Status.canceled, orderService.findOrder(testOrderDTO.getNumber()).getStatus());;
    }

    @Test(dependsOnMethods = {"findOrder"}, expectedExceptions = {OrderNotFoundException.class})
    public void cancelWrongOrder() throws OrderNotFoundException, OrderAlreadyShippedException {
        OrderDTO wrongOrderDTO = new OrderDTO();
        wrongOrderDTO.setNumber("0");
        orderService.cancelOrder(wrongOrderDTO.getNumber());
    }

    @Test(dependsOnMethods = {"cancelWrongOrder"}, expectedExceptions = {ValidationException.class})
    public void cancelOrderWithWrongOrderDTO() throws OrderNotFoundException, OrderAlreadyShippedException {
        OrderDTO wrongOrderDTO = new OrderDTO();
        orderService.cancelOrder(wrongOrderDTO.getNumber());
    }

    @Test(dependsOnMethods = {"placeOrderWithGoodBook", "searchOrder"})
    public void checkTimer() throws OrderNotFoundException {
        try {
            Thread.sleep(timerTimeoutForShipment);
        } catch (InterruptedException ex) {
            Logger.getLogger(OrderServiceRemoteITAmazon.class.getName()).log(Level.SEVERE, null, ex);
        }

        Assert.assertEquals(Status.shipped, orderService.findOrder(testOrderDTO.getNumber()).getStatus());
    }

    @Test(dependsOnMethods = "checkTimer")
    public void cancelValidOrder() throws CustomerNotFoundException, BookNotFoundException, PaymentFailedException, OrderNotFoundException, OrderAlreadyShippedException {
        OrderItemDTO oid = new OrderItemDTO(bookDTO, bookDTO.getPrice(), 1);
        List<OrderItemDTO> ol = new ArrayList<OrderItemDTO>();
        ol.add(oid);
        OrderDTO od = orderService.placeOrder(customerDTO.getNumber(), ol);
        Assert.assertEquals(Status.accepted, od.getStatus());
        orderService.cancelOrder(od.getNumber());
        Assert.assertEquals(Status.canceled, orderService.findOrder(od.getNumber()).getStatus());

    }
}
