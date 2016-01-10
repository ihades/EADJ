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
import static org.books.persistence.ejb.Util.numbGen;
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
    
    @BeforeClass
    public void setup() throws Exception {
        InitialContext context = new InitialContext(Util.getInitProperties());
        catalogService = (CatalogService) context.lookup(CATALOG_SERVICE_NAME);
    }
    
    @Test(threadPoolSize = THREADS, invocationCount = THREADS)
    public void changeBook() throws Exception {
        BookDTO bookDTO = catalogService.addBook(book);
    }
}
