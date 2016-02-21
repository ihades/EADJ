package org.books.api.api;

import io.swagger.client.api.BooksApi;
import io.swagger.client.api.CustomersApi;
import io.swagger.client.api.OrdersApi;
import io.swagger.client.model.Address;
import io.swagger.client.model.Book;
import io.swagger.client.model.BookInfo;
import io.swagger.client.model.CreditCard;
import io.swagger.client.model.Customer;
import io.swagger.client.model.CustomerInfo;
import io.swagger.client.model.OrderItem;
import io.swagger.client.model.OrderRequest;
import io.swagger.client.model.Registration;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import org.hamcrest.Matchers;
import org.junit.Assert;
import static org.junit.Assert.assertThat;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author cb
 */
public class GeneratedRestIT {

    private final BooksApi booksApi = new BooksApi();
    private final CustomersApi customersApi = new CustomersApi();
    private final OrdersApi ordersApi = new OrdersApi();

    private static Customer customerToRegister;

    private Customer customertToUse;

    @BeforeClass
    public static void init() {
        Address address = new Address();
        address.city = "Bern";
        address.country = "Switzerland";
        address.postalCode = "3014";
        address.street = "Polygonstrasse 32a";
        CreditCard creditCard = new CreditCard();
        creditCard.expirationMonth = 12;
        creditCard.expirationYear = 2020;
        creditCard.number = "5555555555554444";
        creditCard.type = CreditCard.TypeEnum.MasterCard;
        customerToRegister = new Customer();
        customerToRegister.address = address;
        customerToRegister.creditCard = creditCard;
        customerToRegister.email = randomizer() + "@whatever.com";
        customerToRegister.firstName = "Hans";
        customerToRegister.lastName = "Wurst";
    }
    private String customerNumber;

    @Test(enabled = true)
    public void bookSearchTest() {
        for (BookInfo book : booksApi.searchBook("java")) {
            assertThat(book.isbn, Matchers.notNullValue());
            assertThat(book.title, Matchers.notNullValue());
            assertThat(book.price, Matchers.notNullValue());
            System.out.println("Book " + book.title + " has ISBN " + book.isbn);
        }
    }

    @Test
    public void bookByIsbnTest() {
        Book book = booksApi.findBook("9781585427659");
        assertThat(book.authors, Matchers.equalToIgnoringCase("Jeremy Rifkin"));
        assertThat(book.title, Matchers.equalToIgnoringCase("The Empathic Civilization: The Race to Global Consciousness in a World in Crisis"));
        assertThat(book.isbn, Matchers.equalToIgnoringCase("1585427659"));
    }

    @Test(expectedExceptions = {NotFoundException.class})
    public void bookByWrongIsbnTest() {
        booksApi.findBook("1234567890");
    }

    @Test
    public void registerCustomer() {
        Registration registration = new Registration();
        registration.customer = customerToRegister;
        registration.password = "Start1234";
        Entity<Registration> e = Entity.entity(registration, MediaType.APPLICATION_JSON_TYPE);
        System.out.println(e.toString());
        customerNumber = customersApi.registerCustomer(registration);
        Assert.assertTrue(!customerNumber.isEmpty());
    }

    @Test(dependsOnMethods = {"registerCustomer"})
    public void searchCustomer() {
        List<CustomerInfo> customers = customersApi.searchCustomer(customerToRegister.firstName);
        Assert.assertTrue(!customers.isEmpty());
    }

    @Test(dependsOnMethods = {"registerCustomer"})
    public void getCustomer() {
        customertToUse = customersApi.findCustomer(customerNumber);
        Assert.assertEquals(customerToRegister, customertToUse);
    }

    @Test(dependsOnMethods = {"registerCustomer"})
    public void placeOrder() {
        List<OrderItem> items = new ArrayList<>();
        OrderItem item1 = new OrderItem();
        item1.bookInfo = booksApi.searchBook("9781585427659").get(0);
        item1.quantity = 3;
        items.add(item1);
        OrderRequest request = new OrderRequest();
        request.customerNr = customerNumber;
        request.items = items;
        String order = ordersApi.placeOrder(request);

        Assert.assertNotNull(order);
    }

    private static String randomizer() {
        return Double.toString(Math.random() * 10000);
    }
}
