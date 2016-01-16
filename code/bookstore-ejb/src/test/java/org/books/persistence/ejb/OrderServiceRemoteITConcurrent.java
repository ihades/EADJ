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
import java.util.HashSet;
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
import static org.junit.Assert.assertEquals;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static org.books.persistence.ejb.Util.numbGen;
import org.junit.Assert;
import org.testng.annotations.BeforeTest;


public class OrderServiceRemoteITConcurrent {
    private static final int THREADS = 2;
    
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

    private CustomerDTO customerDTO1 = new CustomerDTO(Util.numbGen() + "@gmail.com",
            "Erwin",
            "Bazong",
            null,
            addressDTO,
            validCreditCardDTO);
    
    private CustomerDTO customerDTO2 = new CustomerDTO(Util.numbGen() + "@gmail.com",
            "Gimmgamm",
            "Zarwipfel",
            null,
            addressDTO,
            validCreditCardDTO);
    
    //should be different from already existing books in db.
    private BookDTO bookDTO = new BookDTO(numbGen(), "Java9", "Bill Gates", "M$-Press", new Integer(2015), BookDTO.Binding.Hardcover, 1000, new BigDecimal("33.3"));
    private OrderItemDTO orderItemDTO = new OrderItemDTO(bookDTO, bookDTO.getPrice(), 1);
    
    private List<OrderItemDTO> orderList = new ArrayList<OrderItemDTO>();
    private OrderDTO testOrderDTO;
    
    private List<OrderDTO> orders = new ArrayList<>();
    
    
    
    @BeforeClass
    public void setup() throws Exception {
        InitialContext context = new InitialContext(Util.getInitProperties());
        orderService = (OrderService) context.lookup(Util.ORDER_SERVICE_NAME);
        customerService = (CustomerService) context.lookup(Util.CUSTOMER_SERVICE_NAME);
        catalogService = (CatalogService) context.lookup(Util.CATALOG_SERVICE_NAME);
        
    }
    @BeforeTest
    public void init() throws CustomerAlreadyExistsException, CustomerNotFoundException, BookAlreadyExistsException {        
        orderList.add(orderItemDTO);
    }

    @Test
    public void init2()  throws CustomerAlreadyExistsException, BookAlreadyExistsException{
        customerDTO1 = customerService.registerCustomer(customerDTO1, "PW");
        customerDTO2 = customerService.registerCustomer(customerDTO2, "PW");
        catalogService.addBook(bookDTO);
    }
    @Test(dependsOnMethods = {"init2"}, threadPoolSize = THREADS, invocationCount = THREADS)
    public void placeMultipleOrders() throws BookNotFoundException, CustomerAlreadyExistsException, CustomerNotFoundException, PaymentFailedException {   
        orders.add(orderService.placeOrder(customerDTO1.getNumber(), orderList));
        orders.add(orderService.placeOrder(customerDTO1.getNumber(), orderList));
        orders.add(orderService.placeOrder(customerDTO2.getNumber(), orderList));
    }
    
    @Test(dependsOnMethods = {"placeMultipleOrders"})
    public void countOrders() {  
        Assert.assertEquals(6, orders.size());
    }
    
    @Test(dependsOnMethods = {"placeMultipleOrders"})
    public void checkUniqueOrderNumbersAndSize() {  
        Assert.assertEquals(6, orders.size());
        ArrayList<String> numbers = new ArrayList<>();
        for (OrderDTO o : orders) {
            numbers.add(o.getNumber());
        }
        HashSet<String> uniqueOrders = new HashSet<>(numbers);
        Assert.assertEquals(orders.size(), uniqueOrders.size());
    }
    
    
    @Test(dependsOnMethods = {"countOrders"}, expectedExceptions = {ValidationException.class})
    public void placeEmptyOrder() throws BookAlreadyExistsException, CustomerNotFoundException, BookNotFoundException, PaymentFailedException, OrderNotFoundException {
        orderList.clear();
        catalogService.addBook(bookDTO);
        OrderDTO orderDTO = orderService.placeOrder(customerDTO1.getNumber(), orderList);
        //schauen, ob richtig persistiert wird.
        Assert.assertEquals(orderDTO.getStatus(), Status.accepted);
        Assert.assertEquals(orderDTO.getItems().size(), 0);
    }
    
    
    @Test(dependsOnMethods = {"placeEmptyOrder"})
    public void placeOrderWithGoodBook() throws BookAlreadyExistsException, CustomerNotFoundException, BookNotFoundException, PaymentFailedException, OrderNotFoundException {
        orderList.add(orderItemDTO);
        testOrderDTO = orderService.placeOrder(customerDTO1.getNumber(), orderList);
        Assert.assertEquals(testOrderDTO.getStatus(), Status.accepted);
    }
    
    @Test(dependsOnMethods = {"placeOrderWithGoodBook"})
    public void searchOrder() throws CustomerNotFoundException {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        int year = cal.get(Calendar.YEAR);
        List<OrderInfo> orderInfos = orderService.searchOrders(customerDTO1.getNumber(), year);
        Assert.assertEquals(1, orderInfos.size());
        
    }
    
    @Test(dependsOnMethods = {"searchOrder"})
    public void findOrder() throws OrderNotFoundException {
        OrderDTO orderDTO = orderService.findOrder(testOrderDTO.getNumber());
        Assert.assertEquals(testOrderDTO.getNumber(), orderDTO.getNumber());
    }
    
    @Test(dependsOnMethods = {"findOrder"})
    public void cancelOrder() throws OrderNotFoundException, OrderAlreadyShippedException {
        orderService.cancelOrder(testOrderDTO.getNumber());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Logger.getLogger(OrderServiceRemoteITConcurrent.class.getName()).log(Level.SEVERE, null, ex);
        }
        Assert.assertEquals(Status.canceled, orderService.findOrder(testOrderDTO.getNumber()).getStatus());;
    }
    
    @Test(dependsOnMethods = {"findOrder"}, expectedExceptions = {OrderNotFoundException.class})
    public void cancelWrongOrder() throws OrderNotFoundException, OrderAlreadyShippedException {
        OrderDTO wrongOrderDTO = new OrderDTO();
        wrongOrderDTO.setNumber("0");
        orderService.cancelOrder(wrongOrderDTO.getNumber());
    }
    
    //null references need to be caught in cancelOrder.
    @Test(dependsOnMethods = {"cancelWrongOrder"}, expectedExceptions = {OrderNotFoundException.class})
    public void cancelOrderWithWrongOrderDTO() throws OrderNotFoundException, OrderAlreadyShippedException {
        //protect cancelOrder of null pointers. maybe master fischli could have suggested a Builder for DTOs.
        OrderDTO wrongOrderDTO = new OrderDTO();
        orderService.cancelOrder(wrongOrderDTO.getNumber());
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