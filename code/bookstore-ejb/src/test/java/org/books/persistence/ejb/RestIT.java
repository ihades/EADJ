/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.books.persistence.ejb;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.books.ejb.dto.BookDTO;
import org.junit.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.xml.sax.SAXException;

/**
 *
 * @author edm
 */
public class RestIT {
    
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
        BookDTO bd = null;
        Assert.assertEquals(200, r.getStatusInfo().getStatusCode());
        bd = r.readEntity(BookDTO.class);
        Assert.assertNotNull(bd);
    }
    
    @Test
    public void ensureCorrectCustomerRegistrationWithCorrectXml() {
        String xml = "<registration>" +
        "<customer>" +
        "  <number></number>  " +
        "  <email>chrigu.b@gmail2.com</email>" +
        "  <firstName>chrigu</firstName>" +
        "  <lastName>bÃ¼hlmann</lastName>" +
        "  <address>" +
        "    <street>sulgenbachstrasse 14</street>" +
        "    <city>bern</city>" +
        " <postalCode>3007</postalCode>" +
        "    <country>switzerland</country>" +
        "  </address>" +
        "  <creditCard>" +
        "    <type>MasterCard</type>" +
        "    <number>5555555555554444</number>" +
        "    <expirationMonth>12</expirationMonth>" +
        "    <expirationYear>2016</expirationYear>" +
        "  </creditCard>" +
        "</customer>" +
        "<password>1234</password>" +
        "</registration>";
        Response r = customersWebTarget.request(MediaType.APPLICATION_XML).post(Entity.xml(xml));
        //String xml2 = r.readEntity(String.class);
        Assert.assertEquals(201, r.getStatusInfo().getStatusCode());
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
