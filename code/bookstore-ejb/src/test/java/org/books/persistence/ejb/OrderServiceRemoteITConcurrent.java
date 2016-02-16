/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.books.persistence.ejb;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.naming.InitialContext;
import org.books.ejb.CatalogService;
import org.books.ejb.CustomerService;
import org.books.ejb.OrderService;
import org.books.ejb.dto.AddressDTO;
import org.books.ejb.dto.BookDTO;
import org.books.ejb.dto.CreditCardDTO;
import org.books.ejb.dto.CustomerDTO;
import org.books.ejb.dto.OrderDTO;
import org.books.ejb.dto.OrderItemDTO;
import org.books.ejb.exception.BookAlreadyExistsException;
import org.books.ejb.exception.BookNotFoundException;
import org.books.ejb.exception.CustomerAlreadyExistsException;
import org.books.ejb.exception.CustomerNotFoundException;
import org.books.ejb.exception.PaymentFailedException;
import org.books.persistence.dto.BookInfo;
import static org.books.persistence.ejb.Util.numbGen;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
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
    private BookInfo bookInfo = new BookInfo(1l, "Java9", numbGen(), new BigDecimal("33.3"));

    private OrderItemDTO orderItemDTO = new OrderItemDTO(bookInfo, bookDTO.getPrice(), 1);

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
    public void init2() throws CustomerAlreadyExistsException, BookAlreadyExistsException {
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

}
