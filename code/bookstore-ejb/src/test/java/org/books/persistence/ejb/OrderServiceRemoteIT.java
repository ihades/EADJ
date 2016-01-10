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
import org.books.persistence.dto.OrderInfo;
import static org.junit.Assert.assertEquals;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static org.books.persistence.ejb.Util.numbGen;
import org.junit.Assert;
import org.testng.annotations.BeforeTest;


public class OrderServiceRemoteIT {
    
    private OrderService orderService;
    
    private CustomerService customerService;
    
    private CatalogService catalogService;
    
    private final AddressDTO addressDTO = new AddressDTO("Wasserschloss 42",
            "Bern",
            "3011",
            "Switzerland");

    private final CreditCardDTO creditCardDTO = new CreditCardDTO(CreditCardDTO.Type.MasterCard,
            "5220754670550780",
            2,
            2017);

    private CustomerDTO customerDTO = new CustomerDTO(Util.numbGen() + "@gmail.com",
            "Erwin",
            "Bazong",
            null,
            addressDTO,
            creditCardDTO);
    
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
    
    //this test is not possible without changing placeOrder. it merely shows that empty
    //orders can be placed. change API accordingly.
    @Test(dependsOnMethods = {"placeOrderWithWrongBook"})
    public void placeEmptyOrder() throws BookAlreadyExistsException, CustomerNotFoundException, BookNotFoundException, PaymentFailedException, OrderNotFoundException {
        orderList.clear();
        catalogService.addBook(bookDTO);
        OrderDTO orderDTO = orderService.placeOrder(customerDTO.getNumber(), orderList);
        Assert.assertEquals(orderDTO.getStatus(), Status.accepted);
        Assert.assertEquals(orderDTO.getItems().size(), 0);
    }
    
    
    @Test(dependsOnMethods = {"placeEmptyOrder"})
    public void placeOrderWithGoodBook() throws BookAlreadyExistsException, CustomerNotFoundException, BookNotFoundException, PaymentFailedException, OrderNotFoundException {
        orderList.add(orderItemDTO);
        testOrderDTO = orderService.placeOrder(customerDTO.getNumber(), orderList);
        Assert.assertEquals(testOrderDTO.getStatus(), Status.accepted);
    }
    
    //this test is not finished: the assertion should actually check the orderInfos' size for 1,
    //but because orders with 0 orderItemDTO in it can be placed as an order, the list's size is wrong.
    //must check this with cb.
    @Test(dependsOnMethods = {"placeOrderWithGoodBook"})
    public void searchOrder() throws CustomerNotFoundException {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        int year = cal.get(Calendar.YEAR);
        List<OrderInfo> orderInfos = orderService.searchOrders(customerDTO.getNumber(), year);
        Assert.assertEquals(2, orderInfos.size());
        
    }
    
    @Test(dependsOnMethods = {"searchOrder"})
    public void findOrder() throws OrderNotFoundException {
        OrderDTO orderDTO = orderService.findOrder(testOrderDTO.getNumber());
        Assert.assertEquals(testOrderDTO.getNumber(), orderDTO.getNumber());
    }
    
    //    @Override
//    public OrderDTO findOrder(String orderNr) throws OrderNotFoundException {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
    
    @Test(dependsOnMethods = {"findOrder"}, expectedExceptions = {OrderNotFoundException.class})
    public void cancelOrder() throws OrderNotFoundException, OrderAlreadyShippedException {
        orderService.cancelOrder(testOrderDTO.getNumber());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Logger.getLogger(OrderServiceRemoteIT.class.getName()).log(Level.SEVERE, null, ex);
        }
        orderService.findOrder(testOrderDTO.getNumber());
    }
    
    //    @Override
//    public void cancelOrder(String orderNr) throws OrderNotFoundException, OrderAlreadyShippedException {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }

//    @Override
//    public List<OrderInfo> searchOrders(String customerNr, Integer year) throws CustomerNotFoundException {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
    
    
    
}
