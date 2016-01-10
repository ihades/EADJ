/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.books.ejb.impl;

import java.util.List;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import junit.framework.Assert;
import org.books.ejb.CatalogService;
import org.books.ejb.CatalogServiceRemote;
import org.books.ejb.dto.BookDTO;
import org.books.ejb.exception.BookAlreadyExistsException;
import org.books.ejb.exception.BookNotFoundException;
import org.books.persistence.dto.BookInfo;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;



/**
 *
 * @author edm
 */
public class CatalogServiceIT {
    private static final String CATALOG_SERVICE_NAME = "java:global/bookstore-ejb/CatalogService";
    private CatalogService catalogService;
    
    BookDTO existingBook, nonExistingBook;
    
    
    @BeforeClass
    public void setup() throws NamingException {
        catalogService = (CatalogService) new InitialContext().lookup(CATALOG_SERVICE_NAME);
        existingBook = new BookDTO();
        
    }

    @Test
    public void addBook() {
        Assert.assertNotNull(catalogService);
        //Assert.assertEquals(1, 1);
    }
}
