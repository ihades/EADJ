package org.books.persistence.ejb;

import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.books.ejb.amazone.impl.AWSECommerceService;
import org.books.ejb.amazone.impl.AWSECommerceServicePortType;
import org.books.ejb.amazone.impl.Item;
import org.books.ejb.amazone.impl.ItemSearch;
import org.books.ejb.amazone.impl.ItemSearchRequest;
import org.books.ejb.amazone.impl.ItemSearchResponse;
import org.books.ejb.amazone.impl.Items;
import org.testng.annotations.Test;

/**
 *
 * @author cb
 */
public class AmazonIT {

    public AmazonIT() {
    }

    @Test
    public void findTest() throws InterruptedException {

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
