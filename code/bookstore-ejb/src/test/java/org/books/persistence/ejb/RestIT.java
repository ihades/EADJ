/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.books.persistence.ejb;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.books.ejb.dto.BookDTO;
import org.junit.Assert;
import static org.testng.Assert.*;
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
    public void ensureBadRequest() {
        Assert.assertEquals(400, booksWebTarget.request().get().getStatusInfo().getStatusCode());
    }
    
    @Test
    public void ensure404WithWrongBook() {
        Assert.assertEquals(404, booksWebTarget.path("1234").request().get().getStatusInfo().getStatusCode());
    }
    
    @Test
    public void ensure200WithGoodBook() {
        Assert.assertEquals(200, booksWebTarget.path("9781585427659").request().get().getStatusInfo().getStatusCode());
    }
    
    @Test
    public void ensureCorrectXMLPayload() {
        Response r = booksWebTarget.path("9781585427659").request(MediaType.APPLICATION_XML).get();
        BookDTO bd = null;
        Assert.assertEquals(200, r.getStatusInfo().getStatusCode());
        String xml = r.readEntity(String.class);
        Assert.assertTrue(validateXMLSchema("", xml));
        
        Assert.assertNotNull(bd);
        Assert.assertEquals(bd.getBinding(), BookDTO.Binding.Hardcover);
        
        
        
        //Assert.assertEquals(404, booksWebTarget.path("978-1585427659").request().get().getStatusInfo().getStatusCode());
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
    
    public boolean validateXMLSchema(String xsdPath, String xml){
        xsdPath = "catalog.xsd";
        InputStream fis = null;
        File file = new File("src/main/resources/catalog.xsd");
        
        
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
