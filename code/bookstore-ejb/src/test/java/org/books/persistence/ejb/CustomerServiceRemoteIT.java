package org.books.persistence.ejb;

import javax.naming.InitialContext;
import org.books.ejb.CustomerService;
import org.books.ejb.dto.AddressDTO;
import org.books.ejb.dto.CreditCardDTO;
import org.books.ejb.dto.CustomerDTO;
import org.books.ejb.exception.CustomerNotFoundException;
import org.books.ejb.exception.InvalidPasswordException;
import org.junit.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class CustomerServiceRemoteIT {

    private static final String CUSTOMER_SERVICE_NAME = Util.CUSTOMER_SERVICE_NAME;//"java:global/bookstore-app/bookstore-ejb/CustomerService!org.books.ejb.CustomerServiceRemote";

    private CustomerService customerService;

    private final AddressDTO address = new AddressDTO("Sulgenbachsträsse 14",
            "Bern",
            "3007",
            "Switzerland");

    private final CreditCardDTO creditCard = new CreditCardDTO(CreditCardDTO.Type.MasterCard,
            "5220754670550780",
            2,
            2017);

    private CustomerDTO customer = new CustomerDTO(Util.numbGen() + "@gmail.com",
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
        customer = registred;
    }

    @Test(dependsOnMethods = "registerCustomer")
    public void getCustomerByEmail() throws Exception {
        CustomerDTO customerDTO = customerService.findCustomerByEmail(customer.getEmail());

        Assert.assertEquals(customer.getEmail(), customerDTO.getEmail());
    }

    @Test(dependsOnMethods = "registerCustomer")
    public void changeLogin() throws Exception {
        String curentEmail = customer.getEmail();
        try {
            customerService.authenticateCustomer(customer.getEmail(), "123456789");
            try {
                customerService.authenticateCustomer(customer.getEmail(), "12345678");
                Assert.fail("InvalidPasswordException expected");
            } catch (InvalidPasswordException e) {
                //OK
            }
            try {
                customerService.authenticateCustomer(customer.getEmail() + "bladibla", "123456789");
                Assert.fail("CustomerNotFoundException expected");
            } catch (CustomerNotFoundException e) {
                //OK
            }
            customer.setEmail("chrigu.b1234567@gmail.com");
            customerService.updateCustomer(customer);
            customerService.authenticateCustomer(customer.getEmail(), "123456789");
        } finally {
            customer.setEmail(curentEmail);
            customerService.updateCustomer(customer);
        }
    }

}
