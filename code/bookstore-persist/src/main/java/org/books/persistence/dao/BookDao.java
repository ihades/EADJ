package org.books.persistence.dao;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.books.persistence.dto.BookInfo;
import org.books.persistence.entity.Book;
import org.books.persistence.entity.Book_;

@Stateless
@LocalBean
public class BookDao extends GenericDao<Book> {

    protected BookDao(EntityManager mgr) {
        super(Book.class, mgr);
    }

    public BookDao() {
        super(Book.class);
    }

    /**
     * API Compatibility, like {@link #findByIsbn(java.lang.String) }.
     *
     * @param isbn
     * @return
     */
    @Deprecated
    public Book find(String isbn) {
        return findByIsbn(isbn);
    }

    /**
     * Finds a Book by it's ISBN-Number.
     *
     * @param isbn the ISBN number to look for
     * @return the data of the found book or null is no Book is found.
     */
    public Book findByIsbn(String isbn) {
        try {
            return this.getEM().createNamedQuery("Book.findByIsbn", Book.class)
                    .setParameter(Book.BOOK_FIND_BY_ISBN_PARAM, isbn)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Finds a Book by it's ISBN-Number and Locks that Book.
     *
     * @param isbn the ISBN number to look for
     * @return the data of the found book or null is no Book is found.
     */
    public Book findByIsbnPessimisticLock(String isbn) {
        try {
            return this.getEM().createNamedQuery("Book.findByIsbnPessimisticLock", Book.class)
                    .setParameter(Book.BOOK_FIND_BY_ISBN_PARAM, isbn)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * API Compatibility, like {@link #searchByKeywords(List<String> keywords)
     * }.
     *
     * @param keywords
     * @return
     */
    @Deprecated
    public List<BookInfo> search(List<String> keywords) {
        return searchByKeywords(keywords);
    }

    /**
     * Searches for books by keywords. A book is included in the result list if
     * every keyword is contained in its title, authors or publisher field.
     *
     * @param keywords the keywords to search for
     * @return a list of matching books (may be empty)
     */
    public List<BookInfo> searchByKeywords(List<String> keywords) {
        CriteriaBuilder qb = getEM().getCriteriaBuilder();
        CriteriaQuery cq = qb.createQuery();
        Root<Book> book = cq.from(Book.class);

        List<Predicate> predicates = new ArrayList<>();
        for (String string : keywords) {
            predicates.add(
                    qb.or(new Predicate[]{
                        qb.like(
                                qb.lower(book.get(Book_.authors)),
                                "%" + string.toLowerCase() + "%"),
                        qb.like(
                                qb.lower(book.get(Book_.publisher)),
                                "%" + string.toLowerCase() + "%"),
                        qb.like(
                                qb.lower(book.get(Book_.title)),
                                "%" + string.toLowerCase() + "%")
                    }));
        }
        //query itself
        cq.select(qb.construct(
                BookInfo.class,
                book.get(Book_.id),
                book.get(Book_.title),
                book.get(Book_.isbn),
                book.get(Book_.price)))
                .where(predicates.toArray(new Predicate[]{}));

        return getEM().createQuery(cq).getResultList();
    }
}
