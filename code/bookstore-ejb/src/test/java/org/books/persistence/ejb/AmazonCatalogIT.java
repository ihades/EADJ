/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.books.persistence.ejb;

import javax.naming.InitialContext;
import org.books.ejb.AmazonCatalogService;
import org.books.ejb.OrderService;
import org.testng.annotations.Test;
import static org.testng.Assert.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

/**
 *
 * @author edm
 */
public class AmazonCatalogIT {
    
    AmazonCatalogService acs;
    
    public AmazonCatalogIT() {
    }

    @BeforeClass
    public void setUpClass() throws Exception {
        InitialContext context = new InitialContext(Util.getInitProperties());
        acs = (AmazonCatalogService) context.lookup(Util.AMAZON_SERVICE_NAME);
        
    }
    
    @Test
    public void testNotNull() {
       assertTrue(acs != null); 
    }
    
    @Test
    public void testCache() {
       
       assertTrue(acs != null); 
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
