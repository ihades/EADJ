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
import java.util.logging.Level;
import java.util.logging.Logger;
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
import org.books.persistence.dto.OrderInfo;
import org.junit.Assert;
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

    @Test(enabled = true)
    public void searchBooks() {
        Response response = booksWebTarget
                .queryParam("keywords", "rifkin")
                .request(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML)
                .get();
        List<BookInfo> result = response.readEntity(new GenericType<List<BookInfo>>() {
        });
        boolean stringContained = false;
        for (BookInfo book : result) {
            if (book.getTitle().contains("Civilization")) {
                stringContained = true;
                break;
            }

        }
        Assert.assertTrue(stringContained);
    }

    @Test(enabled = true)
    public void searchBooksEnsureValidXml() {
        Response response = booksWebTarget
                .queryParam("keywords", "rifkin")
                .request(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML)
                .get();
        String result = response.readEntity(String.class);
        Assert.assertTrue(validateXMLSchema("orders.xsd", result));
    }

    @Test(enabled = true)
    public void searchBooksWithSeveralNamesEnsureValidXml() {
        Response response = booksWebTarget
                .queryParam("keywords", "zombie jesus")
                .request(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML)
                .get();
        String xml = response.readEntity(String.class);
        Assert.assertTrue(validateXMLSchema("catalog.xsd", xml));

        response = booksWebTarget
                .queryParam("keywords", "zombie jesus")
                .request(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML)
                .get();
        List<BookInfo> bookInfoes = response.readEntity(new GenericType<List<BookInfo>>() {
        });
        boolean bethanyContained = false;
        for (BookInfo bi : bookInfoes) {
            if (bi.getTitle().contains("Champagne Jackson Kicks Zombie Ass")) {
                bethanyContained = true;
                break;
            }
        }
        Assert.assertTrue(bethanyContained);
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
        r = customersWebTarget.path(customerNumber2).request(MediaType.APPLICATION_JSON).get();
        CustomerDTO cd2 = r.readEntity(CustomerDTO.class);
        Assert.assertTrue(cd2.getFirstName().equalsIgnoreCase("ilmhild"));
    }

    @Test(dependsOnMethods = {"registerCustomer1", "registerCustomer2"})
    public void searchCustomersByName() {
        Response r = customersWebTarget.queryParam("name", "brunz").request(MediaType.APPLICATION_XML).get();

        List<CustomerInfo> lci = r.readEntity(new GenericType<List<CustomerInfo>>() {
        });
        Assert.assertTrue(!lci.isEmpty());
    }

    @Test(dependsOnMethods = {"registerCustomer1"})
    public void updateCustomerByXmlAndValidateXml() {
        Response r = customersWebTarget.path(customerNumber1).request().get();
        CustomerDTO cd = r.readEntity(CustomerDTO.class);
        String randomizedFirstName = "RADIOACTIVEBOY" + randomizer();
        String xml = "<customer><number>" + customerNumber1 + "</number><email>updateman" + customerNumber1 + "@whatever.com</email><firstName>" + randomizedFirstName + "</firstName><lastName>brunz</lastName><address><street>klabunkenstrasse 42</street><city>bern</city><postalCode>3011</postalCode><country>switzerland</country></address><creditCard><type>MasterCard</type><number>5555555555554444</number> <expirationMonth>12</expirationMonth> <expirationYear>2016</expirationYear> </creditCard> </customer>";
        r = customersWebTarget.path(customerNumber1).request().put(Entity.xml(xml));
        Assert.assertEquals(204, r.getStatus());
        r = customersWebTarget.path(customerNumber1).request().get();
        CustomerDTO cd2 = r.readEntity(CustomerDTO.class);
        Assert.assertTrue(cd2.getFirstName().contains("RADIO"));
        Assert.assertFalse(cd.getEmail().equalsIgnoreCase(cd2.getEmail()));
        r = customersWebTarget.path(customerNumber1).request().get();
        xml = r.readEntity(String.class);
        Assert.assertTrue(validateXMLSchema("customers.xsd", xml));
    }

    @Test(dependsOnMethods = {"registerCustomer1", "updateCustomerByXmlAndValidateXml"})
    public void updateWrongCustomerByXmlAndCheckReturnValue() {
        Response r = customersWebTarget.path("wrong").request().get();
        Assert.assertTrue(r.getStatus() == 404);
        String randomizedFirstName = "RADIOACTIVEBOY" + randomizer();
        String xml1 = "<customer><number>" + customerNumber1 + "</number><email>updateman" + customerNumber1 + "@whatever.com</email><firstName>" + randomizedFirstName + "</firstName><lastName>brunz</lastName><address><street>klabunkenstrasse 42</street><city>bern</city><postalCode>3011</postalCode><country>switzerland</country></address><creditCard><type>MasterCard</type><number>5555555555554444</number> <expirationMonth>12</expirationMonth> <expirationYear>2016</expirationYear> </creditCard> </customer>";
        r = customersWebTarget.path(customerNumber1).request().put(Entity.xml(xml1));
        Assert.assertEquals(204, r.getStatus());
        String xml2 = "<customer><number>" + customerNumber2 + "</number><email>updateman" + customerNumber1 + "@whatever.com</email><firstName>" + randomizedFirstName + "</firstName><lastName>brunz</lastName><address><street>klabunkenstrasse 42</street><city>bern</city><postalCode>3011</postalCode><country>switzerland</country></address><creditCard><type>MasterCard</type><number>5555555555554444</number> <expirationMonth>12</expirationMonth> <expirationYear>2016</expirationYear> </creditCard> </customer>";
        r = customersWebTarget.path(customerNumber2).request().put(Entity.xml(xml2));
        Assert.assertEquals(409, r.getStatus());
    }

    @Test(dependsOnMethods = {"registerCustomer2"})
    public void updateCustomerByJsonAndValidateXml() {
        Response r = customersWebTarget.path(customerNumber2).request().get();
        CustomerDTO cd = r.readEntity(CustomerDTO.class);
        String randomizedFirstName = "STINKMAN" + randomizer();
        cd.setFirstName(randomizedFirstName);
        cd.setEmail("updateman" + cd.getNumber() + "@whatever.com");
        r = customersWebTarget.path(customerNumber2).request(MediaType.APPLICATION_JSON).put(Entity.json(cd));
        Assert.assertEquals(204, r.getStatus());
        r = customersWebTarget.path(customerNumber2).request().get();
        CustomerDTO cd2 = r.readEntity(CustomerDTO.class);
        Assert.assertTrue(cd2.getFirstName().equals(randomizedFirstName));
        r = customersWebTarget.path(customerNumber2).request().get();
    }

    @Test(dependsOnMethods = {"registerCustomer1", "registerCustomer2"})
    public void customerStuff() {
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

    @Test(dependsOnMethods = {"registerCustomer1", "registerCustomer2"})
    public void findOrder() {
        String orderXml = "<orderRequest><customerNr>" + customerNumber1 + "</customerNr><items><bookInfo><isbn>" + testBook1.getIsbn() + "</isbn><title>" + testBook1.getTitle() + "</title><price>" + testBook1.getPrice() + "</price></bookInfo><quantity>2</quantity></items></orderRequest>";
        Response r = ordersWebTarget.request(MediaType.APPLICATION_XML).post(Entity.xml(orderXml));
        OrderDTO od = r.readEntity(OrderDTO.class);
        r = ordersWebTarget.path(od.getNumber()).request(MediaType.APPLICATION_XML).get();
        OrderInfo oi = r.readEntity(OrderInfo.class);
        Assert.assertEquals(oi.getAmount(), od.getAmount());
        r = ordersWebTarget.path(od.getNumber()).request(MediaType.APPLICATION_XML).get();
        String xml = r.readEntity(String.class);
        Assert.assertTrue(validateXMLSchema("orders.xsd", xml));
    }

    @Test(dependsOnMethods = {"registerCustomer1"})
    public void searchOrdersAndValidateResponsesAndXml() {
        String orderXml = "<orderRequest><customerNr>" + customerNumber1 + "</customerNr><items><bookInfo><isbn>" + testBook1.getIsbn() + "</isbn><title>" + testBook1.getTitle() + "</title><price>" + testBook1.getPrice() + "</price></bookInfo><quantity>2</quantity></items></orderRequest>";
        Response r = ordersWebTarget.queryParam("customerNr", customerNumber1).queryParam("year", "2016").request(MediaType.APPLICATION_JSON).get();
        Assert.assertEquals(200, r.getStatus());
        List<OrderInfo> orderInfoes = r.readEntity(new GenericType<List<OrderInfo>>() {
        });
        Assert.assertTrue(!orderInfoes.isEmpty());
        r = ordersWebTarget.queryParam("customerNr", customerNumber1).request(MediaType.APPLICATION_XML).get();
        Assert.assertEquals(400, r.getStatus());
        r = ordersWebTarget.queryParam("customerNr", "St.Bimbam").queryParam("year", "2016").request(MediaType.APPLICATION_XML).get();
        Assert.assertEquals(404, r.getStatus());
        r = ordersWebTarget.queryParam("customerNr", customerNumber1).queryParam("year", "2016").request(MediaType.APPLICATION_XML).get();
        String xml = r.readEntity(String.class);
        validateXMLSchema("orders.xsd", xml);
    }

    @Test(dependsOnMethods = {"searchOrdersAndValidateResponsesAndXml", "createOrderByXmlAndReturnValidXml", "createOrderByXmlAndReturnJson"})
    public void cancelOrdersAndValidateResponsesAndXml() {
        Response r = ordersWebTarget.queryParam("customerNr", customerNumber1).queryParam("year", "2016").request(MediaType.APPLICATION_XML).get();
        Assert.assertEquals(200, r.getStatus());
        List<OrderInfo> orderInfoes = r.readEntity(new GenericType<List<OrderInfo>>() {
        });
        Assert.assertTrue(!orderInfoes.isEmpty());
        for (int i = 0; i < orderInfoes.size(); i++) {
            if (i < orderInfoes.size() - 1) {
                r = ordersWebTarget.path(orderInfoes.get(i).getNumber()).request(MediaType.TEXT_PLAIN).delete();
                Assert.assertEquals(204, r.getStatus());
            } else {
                try {
                    Thread.sleep(31000);
                    r = ordersWebTarget.path(orderInfoes.get(i).getNumber()).request().delete();
                    Assert.assertEquals(403, r.getStatus());
                } catch (InterruptedException ex) {
                    Logger.getLogger(RestIT.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        r = ordersWebTarget.queryParam("customerNr", customerNumber1).request(MediaType.APPLICATION_XML).get();
        Assert.assertEquals(400, r.getStatus());
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
