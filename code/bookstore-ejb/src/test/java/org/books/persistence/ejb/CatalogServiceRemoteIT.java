package org.books.persistence.ejb;

import java.math.BigDecimal;
import javax.naming.InitialContext;
import org.books.ejb.CatalogService;
import org.books.ejb.dto.BookDTO;
import org.books.ejb.exception.BookAlreadyExistsException;
import org.books.ejb.exception.BookNotFoundException;
import static org.books.persistence.ejb.Util.numbGen;
import static org.books.persistence.ejb.Util.invalidISBNGenerator;
import static org.junit.Assert.assertEquals;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class CatalogServiceRemoteIT {

    private static final String CATALOG_SERVICE_NAME = Util.CATALOG_SERVICE_NAME;

    private CatalogService catalogService;

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
        catalogService = (CatalogService) context.lookup(Util.CATALOG_SERVICE_NAME);
    }

    @Test
    public void addBook() throws Exception {
        catalogService.addBook(book);
    }
    
    @Test(expectedExceptions = {BookAlreadyExistsException.class}, dependsOnMethods = "addBook")
    public void addBookExpectException() throws BookAlreadyExistsException {
        catalogService.addBook(book);
    }

    @Test(dependsOnMethods = "addBook")
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
    
    @Test(expectedExceptions = {BookNotFoundException.class})
    public void getNonExistingBookByIsbnExpectException() throws BookNotFoundException {
        BookDTO bookDTO = catalogService.findBook(invalidISBNGenerator());
    }

    @Test(dependsOnMethods = {"addBook", "getBookByIsbn"})
    public void changeBook() throws Exception {
        BookDTO bookDTO = catalogService.findBook(book.getIsbn());

        assertEquals(bookDTO.getIsbn(), book.getIsbn());
        assertEquals(bookDTO.getTitle(), book.getTitle());
        assertEquals(bookDTO.getAuthors(), book.getAuthors());
        assertEquals(bookDTO.getPublisher(), book.getPublisher());
        assertEquals(bookDTO.getPublicationYear(), book.getPublicationYear());
        assertEquals(bookDTO.getNumberOfPages(), book.getNumberOfPages());
        assertEquals(bookDTO.getPrice(), book.getPrice());

        BigDecimal newPrice = new BigDecimal(32.5);
        bookDTO.setPrice(newPrice);
        newPrice = bookDTO.getPrice();
        catalogService.updateBook(bookDTO);

        bookDTO = catalogService.findBook(book.getIsbn());
        assertEquals(newPrice, bookDTO.getPrice());
    }
    
    @Test(expectedExceptions = {BookNotFoundException.class}, dependsOnMethods = "addBook")
    public void changeBookExpectException() throws BookNotFoundException {
        BookDTO bookDTO = catalogService.findBook(book.getIsbn());
        bookDTO.setIsbn(invalidISBNGenerator());
        catalogService.updateBook(bookDTO);
    }
    
    //maybe not even needed. if yes, needs a test data set to verify correct search function.
    //must consult with jesus christian.
    @Test()
    public void searchBooks() {
        catalogService.searchBooks("Oracle").forEach(b -> {System.out.println(b.getTitle());});
        assertEquals(true, true);
    }
    
    
}
