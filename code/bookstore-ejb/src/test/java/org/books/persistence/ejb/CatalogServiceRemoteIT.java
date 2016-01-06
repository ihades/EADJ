package org.books.persistence.ejb;

import java.math.BigDecimal;
import javax.naming.InitialContext;
import org.books.ejb.CatalogService;
import org.books.ejb.dto.BookDTO;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class CatalogServiceRemoteIT {

    private static final String CATALOG_SERVICE_NAME = "java:global/bookstore-app/bookstore-ejb/CatalogService!org.books.ejb.CatalogServiceRemote";

    private CatalogService accountService;

    @BeforeClass
    public void setup() throws Exception {
        InitialContext context = new InitialContext(Util.getInitProperties());
        accountService = (CatalogService) context.lookup(CATALOG_SERVICE_NAME);
    }

    @Test
    public void openAccount() throws Exception {
        BookDTO book = new BookDTO(
                "123456789",
                "Oracle for Dummies",
                "irgendwer",
                "orelly",
                2012,
                BookDTO.Binding.Unknown,
                120,
                new BigDecimal(25.7));
        accountService.addBook(book);
    }

}
