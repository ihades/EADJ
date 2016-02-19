/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.books.api.api;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.books.api.api.entities.OrderRequest;
import org.books.ejb.dto.BookDTO;
import org.books.ejb.dto.CustomerDTO;
import org.books.ejb.dto.OrderDTO;
import org.books.ejb.dto.OrderItemDTO;
import org.books.persistence.dto.BookInfo;
import org.junit.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.xml.sax.SAXException;


public class RestIT {
    private String randomizer () {
        return Double.toString(Math.random()*10000);
    }
    
    String xmlForCustomerRegistration = "<registration><customer><number></number><email>testman"+randomizer()+"@whatever"+randomizer()+".com</email><firstName>erwin</firstName><lastName>brunz</lastName><address><street>sulgenbachstrasse 14</street><city>bern</city><postalCode>3007</postalCode><country>switzerland</country></address><creditCard><type>MasterCard</type><number>5555555555554444</number> <expirationMonth>12</expirationMonth> <expirationYear>2016</expirationYear> </creditCard> </customer> <password>1234</password></registration>";
    CustomerDTO testCustomer;
    BookDTO testBook;
    String customerNumber = "";
    String isbn1 = "";
    
    public RestIT() {
    }

    Client client = ClientBuilder.newClient();
    WebTarget booksWebTarget = client.target("http://localhost:8080/bookstore/rest/books");
    WebTarget ordersWebTarget = client.target("http://localhost:8080/bookstore/rest/orders");
    WebTarget customersWebTarget = client.target("http://localhost:8080/bookstore/rest/customers");
    
    
    @Test
    public void ensureBadRequests() {
        Assert.assertEquals(400, booksWebTarget.request().get().getStatusInfo().getStatusCode());
        Assert.assertEquals(400, ordersWebTarget.request().get().getStatusInfo().getStatusCode());
        Assert.assertEquals(400, customersWebTarget.request().get().getStatusInfo().getStatusCode());
    }
    
    @Test
    public void ensure404WithWrongData() {
        Assert.assertEquals(404, booksWebTarget.path("1234").request().get().getStatusInfo().getStatusCode());
        Assert.assertEquals(404, ordersWebTarget.path("1234").request().get().getStatusInfo().getStatusCode());
        Assert.assertEquals(404, customersWebTarget.path("1234").request().get().getStatusInfo().getStatusCode());
    }
    
    @Test
    public void ensure200WithGoodBook() {
        Assert.assertEquals(200, booksWebTarget.path("9781585427659").request().get().getStatusInfo().getStatusCode());
    }
    
    @Test
    public void ensureCorrectXMLPayloadFromBooksService() {
        Response r = booksWebTarget.path("9781585427659").request(MediaType.APPLICATION_XML).get();
        BookDTO bd = null;
        Assert.assertEquals(200, r.getStatusInfo().getStatusCode());
        String xml = r.readEntity(String.class);
        Assert.assertTrue(validateXMLSchema("catalog.xsd", xml));
    }
    
    @Test
    public void ensureCorrectBookDTOGeneration() {
        Response r = booksWebTarget.path("9781585427659").request(MediaType.APPLICATION_XML).get();
        Assert.assertEquals(200, r.getStatusInfo().getStatusCode());
        testBook = r.readEntity(BookDTO.class);
        Assert.assertNotNull(testBook);
    }
    
    @Test
    public void registerCustomer() {
        Response r = customersWebTarget.request(MediaType.TEXT_PLAIN).post(Entity.xml(xmlForCustomerRegistration));
        Assert.assertEquals(201, r.getStatusInfo().getStatusCode());
        customerNumber = r.readEntity(String.class);
        
        r = customersWebTarget.request(MediaType.TEXT_PLAIN).post(Entity.xml(xmlForCustomerRegistration));
        Assert.assertEquals(409, r.getStatusInfo().getStatusCode()); 
    }
    
    @Test(dependsOnMethods = {"registerCustomer"})
    public void findCustomerByNumber() {
        
    }
    
    @Test(dependsOnMethods = {"registerCustomer"}) 
    public void searchCustomersByName() {
        
    }
    
    @Test(dependsOnMethods = {"registerCustomer"}) 
    public void updateCustomer() {
        
    }
    
    @Test(dependsOnMethods = {"registerCustomer"}) 
    public void createOrder() {
        List<OrderItemDTO> items = new ArrayList<>();
        BookInfo b1 = new BookInfo(0l, testBook.getTitle(), testBook.getIsbn(), testBook.getPrice());
        items.add(new OrderItemDTO(b1, testBook.getPrice(), 2));
        
        
        OrderRequest or = new OrderRequest(customerNumber, items);
        Entity e = Entity.entity(or, MediaType.APPLICATION_XML);
        
        
        Response r = ordersWebTarget.request(MediaType.APPLICATION_XML).post(Entity.entity(or, MediaType.APPLICATION_XML));
        //String c = r.readEntity(String.class);
        OrderDTO o = r.readEntity(new GenericType<OrderDTO>() {});
        Assert.assertEquals(2, testBook.getPrice().multiply(new BigDecimal("2")));
    }
    
    @Test(dependsOnMethods = {"registerCustomer"}) 
    public void createOrderByXml() {
        
        String isbn = testBook.getIsbn();
        String orderXml = "<orderRequest><customerNr>"+customerNumber+"</customerNr><items><bookInfo><isbn>"+testBook.getIsbn()+"</isbn><title>"+testBook.getTitle()+"</title><price>"+testBook.getPrice()+"</price></bookInfo><quantity>2</quantity></items></orderRequest>";
        Response r = ordersWebTarget.request(MediaType.APPLICATION_XML).post(Entity.xml(orderXml));
        //String c = r.readEntity(String.class);
        OrderDTO o = r.readEntity(new GenericType<OrderDTO>() {});
        Assert.assertEquals(o.getAmount(), testBook.getPrice().multiply(new BigDecimal("2")));
        
    }
    @Test(dependsOnMethods = {"registerCustomer"}) 
    public void createOrderByJson() {
        JsonBuilderFactory jbf = Json.createBuilderFactory(null);
        JsonObject jo = jbf.createObjectBuilder()
                .add("customerNr", customerNumber)
                .add("items", jbf.createArrayBuilder()
                    .add(jbf.createObjectBuilder()
                        .add("bookInfo", jbf.createObjectBuilder()
                            .add("title", testBook.getTitle())
                            .add("isbn", testBook.getIsbn())
                            .add("price", testBook.getPrice())
                        ).add("quantity", 2)
                    )
                ).build();
        
        String orderJson = jo.toString();
        Response r = ordersWebTarget.request(MediaType.APPLICATION_JSON).post(Entity.json(jo));
        OrderDTO o = r.readEntity(new GenericType<OrderDTO>() {});
        
        Assert.assertEquals(o.getAmount(), testBook.getPrice().multiply(new BigDecimal("2")));
    }
    

    

    @BeforeClass
    public static void setUpClass() throws Exception {
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
    
    
    private boolean validateXMLSchema(String xsdFile, String xml){
        File file = new File("src/main/resources/"+xsdFile);
        try {
            SchemaFactory factory = 
                    SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(file);
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new StringReader(xml)));
        } catch (IOException | SAXException e) {
            System.out.println("Exception: "+e.getMessage());
            return false;
        }
        return true;
    }
}
