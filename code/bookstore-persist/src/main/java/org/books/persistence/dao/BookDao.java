package org.books.persistence.dao;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.books.persistence.entity.Book;
import org.books.persistence.entity.Book_;

@Stateless
@LocalBean
public class BookDao extends GenericDao<Book> {

    public BookDao(EntityManager mgr) {
        super(Book.class, mgr);
    }

    public BookDao() {
        super(Book.class);
    }

    public Book getByIsbn(String isbn) {
        return this.getEM().createNamedQuery("Book.findByIsbn", Book.class)
                .setParameter(Book.BOOK_FIND_BY_ISBN_PARAM, isbn)
                .getSingleResult();
    }

    public List<Book> searchByKeywords(List<String> keywords) {
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
        cq.select(book).where(predicates.toArray(new Predicate[]{}));

        return getEM().createQuery(cq).getResultList();
    }

    /**
     * Check all given books if they exists in our local database and add them
     * if required.
     *
     * @param books List of books to check or add
     */
    public void checkAndAddBooks(List<Book> books) {

        Book bookInDb;

        for (Book book : books) {
            try {
                // Try to load the book by ISBN
                bookInDb = this.getByIsbn(book.getIsbn());
            } catch (NoResultException e) {
                bookInDb = null;
            }

            // Nothing found? --> Insert
            if (bookInDb == null) {
                getEM().merge(book);
            }
        }
    }
}
