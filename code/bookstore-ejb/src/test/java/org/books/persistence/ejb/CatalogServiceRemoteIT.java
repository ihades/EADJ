package org.books.persistence.ejb;

import java.math.BigDecimal;
import javax.naming.InitialContext;
import org.books.ejb.CatalogService;
import org.books.ejb.dto.BookDTO;
import static org.junit.Assert.assertEquals;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class CatalogServiceRemoteIT {

    private static final String CATALOG_SERVICE_NAME = "java:global/bookstore-app/bookstore-ejb/CatalogService!org.books.ejb.CatalogServiceRemote";

    private CatalogService catalogService;

    private final BookDTO book = new BookDTO(
            "123456789",
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

    @Test
    public void addBook() throws Exception {
        catalogService.addBook(book);
    }

    @Test()
    public void getBookByIsbn() throws Exception {
        BookDTO bookDTO = catalogService.findBook(book.getIsbn());

        assertEquals(bookDTO.getIsbn(), book.getIsbn());
        assertEquals(bookDTO.getTitle(), book.getTitle());
        assertEquals(bookDTO.getAuthors(), book.getAuthors());
        assertEquals(bookDTO.getPublisher(), book.getPublisher());
        assertEquals(bookDTO.getPublicationYear(), book.getPublicationYear());
        assertEquals(bookDTO.getNumberOfPages(), book.getNumberOfPages());
        assertEquals(bookDTO.getPrice(), book.getPrice());
    }

}
