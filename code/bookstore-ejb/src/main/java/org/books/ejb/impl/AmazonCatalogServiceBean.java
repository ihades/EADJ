package org.books.ejb.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.xml.ws.WebServiceRef;
import org.books.ejb.AmazonCatalogServiceLocal;
import org.books.ejb.AmazonCatalogServiceRemote;
import org.books.ejb.amazone.impl.AWSECommerceService;
import org.books.ejb.amazone.impl.AWSECommerceServicePortType;
import org.books.ejb.amazone.impl.Item;
import org.books.ejb.amazone.impl.ItemAttributes;
import org.books.ejb.amazone.impl.ItemLookup;
import org.books.ejb.amazone.impl.ItemLookupRequest;
import org.books.ejb.amazone.impl.ItemLookupResponse;
import org.books.ejb.amazone.impl.ItemSearch;
import org.books.ejb.amazone.impl.ItemSearchRequest;
import org.books.ejb.amazone.impl.ItemSearchResponse;
import org.books.ejb.amazone.impl.OperationRequest;
import org.books.ejb.amazone.impl.Price;
import org.books.ejb.exception.BookNotFoundException;
import org.books.persistence.entity.Book;
import org.books.persistence.entity.Book.Binding;

@Stateless(name = "AmazonCatalogService")
public class AmazonCatalogServiceBean implements AmazonCatalogServiceLocal, AmazonCatalogServiceRemote {

    private static final Logger LOGGER = Logger.getLogger(AmazonCatalogServiceBean.class.getName());

    private static final String AMAZON_ID_TYPE_ISBN = "ISBN";
    private static final String AMAZON_RESPONSE_GROUP = "ItemAttributes";
    private static final String AMAZON_SEARCH_INDEX = "Books";
    private static final BigInteger AMAZON_MAX_PAGES = BigInteger.TEN;

    @WebServiceRef(AWSECommerceService.class)
    private AWSECommerceServicePortType webServicePort;

    @EJB
    private AmazonThroughputBreakeBean amazonRequestBreake;

    @Override
    public Book findBook(final String isbn) throws BookNotFoundException {

        final ItemLookupResponse awsRsponse = itemLookup(isbn);
        if (awsRsponse == null) {
            throw new BookNotFoundException();
        }
        final Book book = processAwsItemLookupResponse(awsRsponse);
        if (book == null || !book.isComplete()) {
            throw new BookNotFoundException();
        }
        return book;
    }

    @Override
    public List<Book> searchBooks(final String searchString) {
        BigInteger actualPage = BigInteger.ZERO;
        ItemSearchResponse awsResponse = itemSearch(searchString, actualPage);

        if (awsResponse != null) {
            final List<Book> searchResult = new ArrayList<>();
            searchResult.addAll(extractBooks(awsResponse));

            BigInteger totalPages = awsResponse.getItems().get(0).getTotalPages();
            if (AMAZON_MAX_PAGES.compareTo(totalPages) < 0) {
                totalPages = AMAZON_MAX_PAGES;
            }

            while (!actualPage.equals(totalPages)) {
                actualPage.add(BigInteger.ONE);
                awsResponse = itemSearch(searchString, actualPage);
                if (awsResponse != null) {
                    searchResult.addAll(extractBooks(awsResponse));
                } else {
                    break;
                }
            }
            return searchResult;
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    private boolean checkAwsResponseForErrorsAndLog(final OperationRequest req) {
        if (req.getErrors() == null) {
            return true;
        } else {
            LOGGER.log(Level.SEVERE, Arrays.toString(req.getErrors().getError().toArray()));
            return false;
        }
    }

    private Book createBookFromAwsResponseItem(final Item item) {
        final ItemAttributes itemAttributes = item.getItemAttributes();
        final Book book = new Book();
        book.setIsbn(itemAttributes.getISBN());
        book.setTitle(itemAttributes.getTitle());
        book.setAuthors(String.join(", ", itemAttributes.getAuthor()));
        book.setPublisher(itemAttributes.getPublisher());

        BigInteger numberOfPages = itemAttributes.getNumberOfPages();
        if (numberOfPages != null) {
            book.setNumberOfPages(itemAttributes.getNumberOfPages().intValue());
        }

        Price listPrice = itemAttributes.getListPrice();
        if (listPrice != null) {
            book.setPrice(new BigDecimal(listPrice.getAmount()).divide(BigDecimal.valueOf(100)));
        }

        book.setBinding(mapAwsToBookstoreBinding(itemAttributes.getBinding()));
        try {
            LocalDate dateTime = LocalDate.parse(itemAttributes.getPublicationDate());
            book.setPublicationYear(dateTime.getYear());
        } catch (DateTimeParseException | NullPointerException e) {
            LOGGER.log(Level.WARNING, "Cannot parse publication year for ISBN " + book.getIsbn()
                    + " Received value: " + itemAttributes.getPublicationDate(), e);
        }

        return book;
    }

    private Binding mapAwsToBookstoreBinding(final String binding) {
        switch (binding) {
            case "Paperback":
                return Binding.Paperback;
            case "Hardcover":
                return Binding.Hardcover;
            default:
                return Binding.Unknown;
        }
    }

    private Book processAwsItemLookupResponse(final ItemLookupResponse awsRsponse) {
        Book book = null;
        if (awsRsponse != null
                && checkAwsResponseForErrorsAndLog(awsRsponse.getOperationRequest())
                && awsRsponse.getItems().size() == 1
                && awsRsponse.getItems().get(0).getItem().size() > 0) {

            for (Item item : awsRsponse.getItems().get(0).getItem()) {
                String binding = item.getItemAttributes().getBinding();
                if (mapAwsToBookstoreBinding(binding) != Binding.Unknown) {
                    book = createBookFromAwsResponseItem(item);
                }
            }
            if (book == null) {
                book = createBookFromAwsResponseItem(awsRsponse.getItems().get(0).getItem().get(0));
            }
        }
        return book;
    }

    private ItemSearchResponse itemSearch(final String searchString, final BigInteger page) {
        ItemSearch searchBody = new ItemSearch();
        ItemSearchRequest req = new ItemSearchRequest();

        req.setSearchIndex(AMAZON_SEARCH_INDEX);
        req.getResponseGroup().add(AMAZON_RESPONSE_GROUP);
        req.setKeywords(searchString);
        req.setItemPage(page);

        searchBody.getRequest().add(req);
        amazonRequestBreake.beFriendlyWithAmazon();
        LOGGER.info("Sending search request for page number: " + page.intValue());
        ItemSearchResponse awsResponse = webServicePort.itemSearch(searchBody);

        if (awsResponse != null
                && checkAwsResponseForErrorsAndLog(awsResponse.getOperationRequest())
                && awsResponse.getItems().size() == 1
                && awsResponse.getItems().get(0).getTotalResults().compareTo(BigInteger.ZERO) > 0) {
            return awsResponse;
        } else {
            return null;
        }
    }

    private Collection<Book> extractBooks(final ItemSearchResponse awsResponse) {
        Collection<Book> result = new ArrayList<>();
        for (Item item : awsResponse.getItems().get(0).getItem()) {
            Book book = createBookFromAwsResponseItem(item);
            if (book != null) {
                result.add(book);
            }
        }
        return result;
    }

    private ItemLookupResponse itemLookup(final String isbn) {
        final ItemLookupRequest req = new ItemLookupRequest();
        req.setIdType(AMAZON_ID_TYPE_ISBN);
        req.setSearchIndex(AMAZON_SEARCH_INDEX);
        req.getItemId().add(isbn);
        req.getResponseGroup().add(AMAZON_RESPONSE_GROUP);

        final ItemLookup lookupBody = new ItemLookup();
        lookupBody.getRequest().add(req);
        amazonRequestBreake.beFriendlyWithAmazon();
        final ItemLookupResponse awsResponse = webServicePort.itemLookup(lookupBody);
        if (awsResponse != null
                && checkAwsResponseForErrorsAndLog(awsResponse.getOperationRequest())
                && awsResponse.getItems().size() == 1
                && awsResponse.getItems().get(0).getTotalResults().compareTo(BigInteger.ZERO) > 0) {
            return awsResponse;
        } else {
            return null;
        }
    }
}
