/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.books.persistence.ejb;

import javax.ejb.EJB;
import javax.naming.InitialContext;
import org.books.ejb.CustomerService;
import org.books.ejb.dto.AddressDTO;
import org.books.ejb.dto.CreditCardDTO;
import org.books.ejb.dto.CustomerDTO;
import org.books.persistence.dao.CustomerDao;
import org.junit.Assert;
import static org.testng.Assert.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 * @author edm
 */
public class CustomerServiceRemoteITConcurrent {
    
    @EJB
    CustomerDao cd;
    
    private static final int THREADS = 4;
    
    private volatile static int houseNumber = 0;
    
    
    private static final String CUSTOMER_SERVICE_NAME = Util.CUSTOMER_SERVICE_NAME;//"java:global/bookstore-app/bookstore-ejb/CustomerService!org.books.ejb.CustomerServiceRemote";

    private CustomerService customerService;
    
    
    private final AddressDTO addressDTO = new AddressDTO("Affengehege 1",
            "Bern",
            "3011",
            "Switzerland");
    
    private final CreditCardDTO validCreditCardDTO = new CreditCardDTO(CreditCardDTO.Type.MasterCard,
            "5555555555554444",
            2,
            2017);

    private CustomerDTO customerDTO = new CustomerDTO(Util.numbGen() + "@gmail.com",
            "Wupp",
            "Klumpenstunk",
            null,
            addressDTO,
            validCreditCardDTO);
    
    public CustomerServiceRemoteITConcurrent() {
    }


    @BeforeClass
    public void setUpClass() throws Exception {
        InitialContext context = new InitialContext(Util.getInitProperties());
        customerService = (CustomerService) context.lookup(CUSTOMER_SERVICE_NAME);
    }
    
    @Test(threadPoolSize = THREADS, invocationCount = THREADS)
    public void testChangeAddress() {
        addressDTO.setStreet("Strasse "+incrementHouseNumber()); 
        System.out.println("HN: "+houseNumber);
    }
    
    @Test(dependsOnMethods = "testChangeAddress")
    public void testConcurrentCustomerAddressChange() {
        Assert.assertEquals("Strasse "+THREADS, addressDTO.getStreet());
    }
    
    private synchronized int incrementHouseNumber() {
        houseNumber++;
        return houseNumber;
    }
    
    @Test(threadPoolSize = THREADS+20, invocationCount = THREADS+20)
    public void testConcurrentChangeCustomer1() {
        String customerNr = Util.numbGen() + "@gmail.com";
        customerDTO.setEmail("bla"+Math.random());
        
    }
    
    @Test(threadPoolSize = THREADS+20, invocationCount = THREADS+20)
    public void testConcurrentChangeCustomer2() {
        customerDTO.setEmail("bla"+Math.random());
        
    }
    
    @Test(threadPoolSize = THREADS+20, invocationCount = THREADS+20)
    public void testConcurrentReadEmailFromCustomer() {
        customerDTO.getEmail();
        
    }
    
    
    
    
    
    

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @BeforeMethod
    public void setUpMethod() throws Exception {
    }

    @AfterMethod
    public void tearDownMethod() throws Exception {
    }
}
