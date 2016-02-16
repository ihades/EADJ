package org.books.persistence.ejb;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.naming.InitialContext;
import org.books.ejb.AmazonCatalogService;
import org.books.ejb.amazone.impl.AWSECommerceService;
import org.books.ejb.amazone.impl.AWSECommerceServicePortType;
import org.books.ejb.amazone.impl.Item;
import org.books.ejb.amazone.impl.ItemSearch;
import org.books.ejb.amazone.impl.ItemSearchRequest;
import org.books.ejb.amazone.impl.ItemSearchResponse;
import org.books.ejb.amazone.impl.Items;
import org.books.ejb.dto.BookDTO;
import static org.books.persistence.ejb.Util.numbGen;
import org.books.persistence.entity.Book;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author cb
 */
public class AmazonIT {

    private AmazonCatalogService catalogService;

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
        catalogService = (AmazonCatalogService) context.lookup(Util.AMAZON_SERVICE_NAME);
    }

    @Test
    public void search() throws Exception {
        List<Book> books = catalogService.searchBooks("Austenland");
        int i = 1;
        for (Book book : books) {
            System.out.println(String.format("%2d: ", i++)
                    + book.getTitle());
        }
    }

    @Test
    public void webserviceTest() throws InterruptedException {

        System.setProperty("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump", "true");
        System.setProperty("com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump", "true");
        System.setProperty("com.sun.xml.ws.transport.http.HttpAdapter.dump", "true");
        System.setProperty("com.sun.xml.internal.ws.transport.http.HttpAdapter.dump", "true");

        AWSECommerceService service = new AWSECommerceService();
        AWSECommerceServicePortType port = service.getAWSECommerceServicePort();
        ItemSearchRequest request = new ItemSearchRequest();
        request.setSearchIndex("Books");
        request.setKeywords("Austenland");
        ItemSearch itemSearch = new ItemSearch();
        itemSearch.getRequest().add(request);
        ItemSearchResponse response = port.itemSearch(itemSearch);
        List<Items> itemsList = response.getItems();

        int maxPages = itemsList.get(0).getTotalPages().intValue();
        for (int i = 2; i <= maxPages; i++) {
            TimeUnit.SECONDS.sleep(1);
            request.setItemPage(new BigInteger(String.valueOf(i)));
            itemSearch = new ItemSearch();
            itemSearch.getRequest().add(request);
            response = port.itemSearch(itemSearch);
            itemsList.addAll(response.getItems());
        }

        int i = 1;
        for (Items next : itemsList) {
            for (Item item : next.getItem()) {
                System.out.println(String.format("%2d: ", i++)
                        + item.getItemAttributes().getTitle());
            }
        }
    }
}
