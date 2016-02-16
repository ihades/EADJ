package org.books.api.api;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import javax.ws.rs.core.Response;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import org.books.ejb.CatalogService;
import org.books.ejb.dto.BookDTO;
import org.books.ejb.exception.BookNotFoundException;
import org.books.persistence.dto.BookInfo;

@Path("books")
public class BookRessource {

    private CatalogService catalog;

    @PostConstruct
    public void init() {
        try {
            InitialContext ctx = new InitialContext();
            catalog = (CatalogService) ctx.lookup("java:global/bookstore-app/bookstore-ejb/CatalogService!org.books.ejb.CatalogServiceLocal");
        } catch (NamingException ne) {
            System.out.println("\n[BookRessource] NamingException: " + ne);
            ne.printStackTrace();
        }
    }

    /**
     * Finds a book by ISBN number.
     *
     * @param isbn the ISBN number of the book
     * @return the data of the found book
     * @responseMessage 404 not found (book not found)
     * @responseMessage 500 internal server error (unexpected system error)
     */
    @GET
    @Produces({APPLICATION_XML, APPLICATION_JSON})
    @Path("{isbn}")
    public BookDTO findBook(@PathParam("isbn") String isbn) {
        try {
            return catalog.findBook(isbn);
        } catch (BookNotFoundException ex) {
            throw new WebApplicationException(ex.getMessage(),
                    Response.status(NOT_FOUND).entity(ex.getMessage()).build());
        }
    }

    /**
     * Searches for books by keywords. A book is included in the result list if
     * every keyword is contained in its title, authors or publisher field.
     *
     * @param keywords
     * @return the data of the found book
     * @responseMessage 400 bad request (keywords missing)
     * @responseMessage 500 internal server error (unexpected system error)
     */
    @GET
    @Produces({APPLICATION_XML, APPLICATION_JSON})
    public List<BookInfo> searchBook(@QueryParam("keywords") String keywords) {
        if (keywords != null && !keywords.isEmpty()) {
            return catalog.searchBooks(keywords);
        } else {
            throw new WebApplicationException("keyword missing",
                    Response.status(BAD_REQUEST).build());
        }
    }

}
