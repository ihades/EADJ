package org.books.persistence.ejb;

import javax.naming.InitialContext;
import org.books.ejb.CustomerService;
import org.books.ejb.dto.AddressDTO;
import org.books.ejb.dto.CreditCardDTO;
import org.books.ejb.dto.CustomerDTO;
import org.junit.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class CustomerServiceRemoteIT {

    private static final String CUSTOMER_SERVICE_NAME = "java:global/bookstore-app/bookstore-ejb/CustomerService!org.books.ejb.CustomerServiceRemote";

    private CustomerService customerService;

    private final AddressDTO address = new AddressDTO("Sulgenbachsträsse 14",
            "Bern",
            "3007",
            "Switzerland");

    private final CreditCardDTO creditCard = new CreditCardDTO(CreditCardDTO.Type.MasterCard,
            "5220754670550780",
            2,
            2017);

    private final CustomerDTO customer = new CustomerDTO(Util.numbGen() + "@gmail.com",
            "Christoph",
            "Bühlmann",
            null,
            address,
            creditCard);

    @BeforeClass
    public void setup() throws Exception {
        InitialContext context = new InitialContext(Util.getInitProperties());
        customerService = (CustomerService) context.lookup(CUSTOMER_SERVICE_NAME);
    }

    @Test
    public void registerCustomer() throws Exception {
        CustomerDTO registred = customerService.registerCustomer(customer, "123456789");
        Assert.assertNotNull(registred.getNumber());
        Assert.assertNotSame("TEMPORARY", registred.getNumber());
        System.out.println("Kunde mit Nummer " + registred.getNumber() + " registriert.");
    }

//    @Test(dependsOnMethods = "registerCustomer")
//    public void getCustomerByEmail() throws Exception {
//        BookDTO bookDTO = customerService.findCustomerByEmail(customer.getIsbn());
//
//        assertEquals(bookDTO.getIsbn(), customer.getIsbn());
//        assertEquals(bookDTO.getTitle(), customer.getTitle());
//        assertEquals(bookDTO.getAuthors(), customer.getAuthors());
//        assertEquals(bookDTO.getPublisher(), customer.getPublisher());
//        assertEquals(bookDTO.getPublicationYear(), customer.getPublicationYear());
//        assertEquals(bookDTO.getNumberOfPages(), customer.getNumberOfPages());
//        assertEquals(bookDTO.getPrice(), customer.getPrice());
//    }
//
//    @Test(dependsOnMethods = {"addBook", "getBookByIsbn"})
//    public void changeBook() throws Exception {
//        BookDTO bookDTO = customerService.findBook(customer.getIsbn());
//
//        assertEquals(bookDTO.getIsbn(), customer.getIsbn());
//        assertEquals(bookDTO.getTitle(), customer.getTitle());
//        assertEquals(bookDTO.getAuthors(), customer.getAuthors());
//        assertEquals(bookDTO.getPublisher(), customer.getPublisher());
//        assertEquals(bookDTO.getPublicationYear(), customer.getPublicationYear());
//        assertEquals(bookDTO.getNumberOfPages(), customer.getNumberOfPages());
//        assertEquals(bookDTO.getPrice(), customer.getPrice());
//
//        BigDecimal newPrice = new BigDecimal(32.5);
//        bookDTO.setPrice(newPrice);
//        newPrice = bookDTO.getPrice();
//        customerService.updateBook(bookDTO);
//
//        bookDTO = customerService.findBook(customer.getIsbn());
//        assertEquals(newPrice, bookDTO.getPrice());
//    }
//
//    private static String numbGen() {
//        while (true) {
//            long numb = (long) (Math.random() * 100000000 * 1000000); // had to use this as int's are to small for a 13 digit number.
//            if (String.valueOf(numb).length() == 13) {
//                return String.valueOf(numb);
//            }
//        }
//    }
}
