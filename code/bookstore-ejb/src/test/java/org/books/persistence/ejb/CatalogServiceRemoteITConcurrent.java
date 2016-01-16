/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.books.persistence.ejb;

import java.math.BigDecimal;
import javax.jms.JMSContext;
import javax.jms.MapMessage;
import javax.naming.InitialContext;
import org.books.ejb.CatalogService;
import org.books.ejb.dto.BookDTO;
import org.books.ejb.exception.BookAlreadyExistsException;
import static org.books.persistence.ejb.Util.numbGen;
import org.junit.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class CatalogServiceRemoteITConcurrent {
    
    private static final String CATALOG_SERVICE_NAME = Util.CATALOG_SERVICE_NAME;
    
    private CatalogService catalogService;
    
    private static final int THREADS = 4;
    
    private final BookDTO book = new BookDTO(
            numbGen(),
            "Oracle for Dummies",
            "irgendwer",
            "orelly",
            2012,
            BookDTO.Binding.Unknown,
            120,
            new BigDecimal(25.7));
    
    private BookDTO testBook;
    
    @BeforeClass
    public void setup() throws Exception {
        InitialContext context = new InitialContext(Util.getInitProperties());
        catalogService = (CatalogService) context.lookup(CATALOG_SERVICE_NAME);
    }
    
    @Test
    public void init() throws BookAlreadyExistsException{
        testBook = catalogService.addBook(book);
    }
    
    //not thread safe as of now.
    @Test(threadPoolSize = THREADS, invocationCount = THREADS, dependsOnMethods = "init")
    public void changeBook() throws Exception {
        Double p = Math.random() * 1000;
        testBook.setNumberOfPages(p.intValue());
        catalogService.updateBook(testBook);
        BookDTO localTestBook = catalogService.findBook(testBook.getIsbn());
        Assert.assertTrue(localTestBook.getNumberOfPages() == p.intValue());
    }
}
