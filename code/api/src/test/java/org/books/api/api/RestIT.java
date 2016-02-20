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
import org.books.persistence.dto.CustomerInfo;
import org.junit.Assert;
import org.junit.Ignore;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.xml.sax.SAXException;

public class RestIT {

    private String randomizer() {
        return Double.toString(Math.random() * 10000);
    }

    String xmlForCustomerRegistration1 = "<registration><customer><number></number><email>testman" + randomizer() + "@whatever" + randomizer() + ".com</email><firstName>erwin</firstName><lastName>brunz</lastName><address><street>klabunkenstrasse 42</street><city>bern</city><postalCode>3011</postalCode><country>switzerland</country></address><creditCard><type>MasterCard</type><number>5555555555554444</number> <expirationMonth>12</expirationMonth> <expirationYear>2016</expirationYear> </creditCard> </customer> <password>1234</password></registration>";
    String xmlForCustomerRegistration2 = "<registration><customer><number></number><email>testwoman" + randomizer() + "@whatever" + randomizer() + ".com</email><firstName>ilmhild</firstName><lastName>bambam</lastName><address><street>rainbowroad 69</street><city>bern</city><postalCode>3666</postalCode><country>switzerland</country></address><creditCard><type>MasterCard</type><number>5555555555554445</number> <expirationMonth>01</expirationMonth> <expirationYear>2015</expirationYear> </creditCard> </customer> <password>1234</password></registration>";

    CustomerDTO testCustomer;
    BookDTO testBook1;
    BookDTO testBook2;
    String customerNumber1 = "";
    String customerNumber2 = "";
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

    @Ignore
    @Test
    public void searchBooks() {
        Response response = booksWebTarget
                .queryParam("keywords", "java")
                .request(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML)
                .get();
        List<BookInfo> result = response.readEntity(new GenericType<List<BookInfo>>() {
        });
        for (BookInfo book : result) {
            System.out.println(book.getIsbn());
        }
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
        testBook1 = r.readEntity(BookDTO.class);
        Assert.assertNotNull(testBook1);
        r = booksWebTarget.path("8601300201986").request(MediaType.APPLICATION_XML).get();
        Assert.assertEquals(200, r.getStatusInfo().getStatusCode());
        testBook2 = r.readEntity(BookDTO.class);
        Assert.assertTrue(testBook2.getTitle().contains("Effective"));
    }

    @Test
    public void registerCustomer1() {
        Response r = customersWebTarget.request(MediaType.TEXT_PLAIN).post(Entity.xml(xmlForCustomerRegistration1));
        Assert.assertEquals(201, r.getStatusInfo().getStatusCode());
        customerNumber1 = r.readEntity(String.class);
        r = customersWebTarget.request(MediaType.TEXT_PLAIN).post(Entity.xml(xmlForCustomerRegistration1));
        Assert.assertEquals(409, r.getStatusInfo().getStatusCode());
    }

    @Test
    public void registerCustomer2() {
        Response r = customersWebTarget.request(MediaType.TEXT_PLAIN).post(Entity.xml(xmlForCustomerRegistration2));
        Assert.assertEquals(201, r.getStatusInfo().getStatusCode());
        customerNumber2 = r.readEntity(String.class);
        r = customersWebTarget.request(MediaType.TEXT_PLAIN).post(Entity.xml(xmlForCustomerRegistration2));
        Assert.assertEquals(409, r.getStatusInfo().getStatusCode());
    }

//    @Test(threadPoolSize = 5, invocationCount = 10, dependsOnMethods = "init")
//    public void registerCustomerInSuccession() {
//        Response r = customersWebTarget.request(MediaType.TEXT_PLAIN).post(Entity.xml(xmlForCustomerRegistration2));
//        Assert.assert(201, r.getStatusInfo().getStatusCode());
//        String customerNumber = r.readEntity(String.class);
//        r = customersWebTarget.request(MediaType.TEXT_PLAIN).post(Entity.xml(xmlForCustomerRegistration2));
//        Assert.assertEquals(409, r.getStatusInfo().getStatusCode());
//    }
    @Test(dependsOnMethods = {"registerCustomer1", "registerCustomer2"})
    public void findCustomerByNumber() {
        Response r = customersWebTarget.path(customerNumber2).request().get();
        CustomerDTO cd = r.readEntity(CustomerDTO.class);
        Assert.assertTrue(cd.getFirstName().equalsIgnoreCase("ilmhild"));
    }

    @Test(dependsOnMethods = {"registerCustomer1", "registerCustomer2"})
    public void searchCustomersByName() {
        Response r = customersWebTarget.queryParam("name", new String[]{"bambam", "brunz"}).request(MediaType.APPLICATION_XML).get();
        //Response r = customersWebTarget.queryParam("name", "brunz").request(MediaType.APPLICATION_XML).get();

        List<CustomerInfo> lci = r.readEntity(new GenericType<List<CustomerInfo>>() {
        });
        Assert.assertTrue(!lci.isEmpty());
    }

    @Test(dependsOnMethods = {"registerCustomer1"})
    public void updateCustomer() {

    }

    @Test(dependsOnMethods = {"registerCustomer1"})
    public void createOrderForWrongCustomer() {
        String orderXml = "<orderRequest><customerNr>007</customerNr><items><bookInfo><isbn>" + testBook1.getIsbn() + "</isbn><title>" + testBook1.getTitle() + "</title><price>" + testBook1.getPrice() + "</price></bookInfo><quantity>2</quantity></items></orderRequest>";
        Response r = ordersWebTarget.request(MediaType.APPLICATION_XML).post(Entity.xml(orderXml));
        Assert.assertEquals(404, r.getStatus());
    }

    @Test(dependsOnMethods = {"registerCustomer1"})
    public void createOrderWithWrongIsbn() {
        String orderXml = "<orderRequest><customerNr>" + customerNumber1 + "</customerNr><items><bookInfo><isbn>1</isbn><title>" + testBook1.getTitle() + "</title><price>" + testBook1.getPrice() + "</price></bookInfo><quantity>2</quantity></items></orderRequest>";
        Response r = ordersWebTarget.request(MediaType.APPLICATION_XML).post(Entity.xml(orderXml));
        Assert.assertEquals(404, r.getStatus());
    }

    @Test(dependsOnMethods = {"registerCustomer1"})
    public void createOrderThatIsTooExpensive() {
        String orderXml = "<orderRequest><customerNr>" + customerNumber1 + "</customerNr><items><bookInfo><isbn>" + testBook1.getIsbn() + "</isbn><title>" + testBook1.getTitle() + "</title><price>" + testBook1.getPrice() + "</price></bookInfo><quantity>1000</quantity></items></orderRequest>";
        Response r = ordersWebTarget.request(MediaType.APPLICATION_XML).post(Entity.xml(orderXml));
        Assert.assertEquals(402, r.getStatus());
    }

    @Test(dependsOnMethods = {"registerCustomer1"})
    public void createOrder() {
        List<OrderItemDTO> items = new ArrayList<>();
        BookInfo b1 = new BookInfo(0l, testBook1.getTitle(), testBook1.getIsbn(), testBook1.getPrice());
        items.add(new OrderItemDTO(b1, testBook1.getPrice(), 2));

        OrderRequest or = new OrderRequest(customerNumber1, items);
        Entity e = Entity.entity(or, MediaType.APPLICATION_XML);

        Response r = ordersWebTarget.request(MediaType.APPLICATION_XML).post(Entity.entity(or, MediaType.APPLICATION_XML));
        //String c = r.readEntity(String.class);
        Assert.assertEquals(201, r.getStatusInfo().getStatusCode());
        OrderDTO o = r.readEntity(new GenericType<OrderDTO>() {
        });
        Assert.assertEquals(o.getAmount(), testBook1.getPrice().multiply(new BigDecimal("2")));
    }

    @Test(dependsOnMethods = {"registerCustomer1"})
    public void createOrderByXml() {
        String isbn = testBook1.getIsbn();
        String orderXml = "<orderRequest><customerNr>" + customerNumber1 + "</customerNr><items><bookInfo><isbn>" + testBook1.getIsbn() + "</isbn><title>" + testBook1.getTitle() + "</title><price>" + testBook1.getPrice() + "</price></bookInfo><quantity>2</quantity></items></orderRequest>";
        Response r = ordersWebTarget.request(MediaType.APPLICATION_XML).post(Entity.xml(orderXml));
        Assert.assertEquals(201, r.getStatusInfo().getStatusCode());
        OrderDTO o = r.readEntity(new GenericType<OrderDTO>() {
        });
        Assert.assertEquals(o.getAmount(), testBook1.getPrice().multiply(new BigDecimal("2")));

    }

    @Test(dependsOnMethods = {"registerCustomer1"})
    public void createOrderByJson() {
        JsonBuilderFactory jbf = Json.createBuilderFactory(null);
        JsonObject jo = jbf.createObjectBuilder()
                .add("customerNr", customerNumber1)
                .add("items", jbf.createArrayBuilder()
                        .add(jbf.createObjectBuilder()
                                .add("bookInfo", jbf.createObjectBuilder()
                                        .add("title", testBook1.getTitle())
                                        .add("isbn", testBook1.getIsbn())
                                        .add("price", testBook1.getPrice())
                                ).add("quantity", 2)
                        )
                ).build();

        String orderJson = jo.toString();
        Response r = ordersWebTarget.request(MediaType.APPLICATION_JSON).post(Entity.json(jo));
        Assert.assertEquals(201, r.getStatusInfo().getStatusCode());
        OrderDTO o = r.readEntity(new GenericType<OrderDTO>() {
        });

        Assert.assertEquals(o.getAmount(), testBook1.getPrice().multiply(new BigDecimal("2")));
    }

    @Test(dependsOnMethods = {"registerCustomer1"})
    public void createOrderByJsonAndReturnXml() {
        JsonBuilderFactory jbf = Json.createBuilderFactory(null);
        JsonObject jo = jbf.createObjectBuilder()
                .add("customerNr", customerNumber1)
                .add("items", jbf.createArrayBuilder()
                        .add(jbf.createObjectBuilder()
                                .add("bookInfo", jbf.createObjectBuilder()
                                        .add("title", testBook1.getTitle())
                                        .add("isbn", testBook1.getIsbn())
                                        .add("price", testBook1.getPrice())
                                ).add("quantity", 2)
                        )
                ).build();

        String orderJson = jo.toString();
        Response r = ordersWebTarget.request(MediaType.APPLICATION_XML).post(Entity.json(jo));
        Assert.assertEquals(201, r.getStatusInfo().getStatusCode());
        OrderDTO o = r.readEntity(new GenericType<OrderDTO>() {
        });
        Assert.assertNotNull(o.getCustomerInfo());
        Assert.assertEquals(o.getAmount(), testBook1.getPrice().multiply(new BigDecimal("2")));
    }

    @Test(dependsOnMethods = {"registerCustomer1"})
    public void createOrderByXmlAndReturnJson() {

        String isbn = testBook1.getIsbn();
        String orderXml = "<orderRequest><customerNr>" + customerNumber1 + "</customerNr><items><bookInfo><isbn>" + testBook1.getIsbn() + "</isbn><title>" + testBook1.getTitle() + "</title><price>" + testBook1.getPrice() + "</price></bookInfo><quantity>2</quantity></items></orderRequest>";
        Response r = ordersWebTarget.request(MediaType.APPLICATION_JSON).post(Entity.xml(orderXml));
        Assert.assertEquals(201, r.getStatusInfo().getStatusCode());
        OrderDTO o = r.readEntity(new GenericType<OrderDTO>() {
        });
        Assert.assertEquals(o.getAmount(), testBook1.getPrice().multiply(new BigDecimal("2")));
        Assert.assertEquals(o.getCustomerInfo().getNumber(), customerNumber1);

    }

    @Test(dependsOnMethods = {"registerCustomer1"})
    public void createOrderByXmlAndReturnValidXml() {

        String isbn = testBook1.getIsbn();
        String orderXml = "<orderRequest><customerNr>" + customerNumber1 + "</customerNr><items><bookInfo><isbn>" + testBook1.getIsbn() + "</isbn><title>" + testBook1.getTitle() + "</title><price>" + testBook1.getPrice() + "</price></bookInfo><quantity>2</quantity></items></orderRequest>";
        Response r = ordersWebTarget.request(MediaType.APPLICATION_XML).post(Entity.xml(orderXml));
        Assert.assertEquals(201, r.getStatusInfo().getStatusCode());
        String xml = r.readEntity(String.class);
        Assert.assertTrue(validateXMLSchema("orders.xsd", xml));

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

    private boolean validateXMLSchema(String xsdFile, String xml) {
        File file = new File("src/main/resources/" + xsdFile);
        try {
            SchemaFactory factory
                    = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(file);
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new StringReader(xml)));
        } catch (IOException | SAXException e) {
            System.out.println(xml);
            System.out.println("Exception: " + e.getMessage());
            return false;
        }
        return true;
    }
}
