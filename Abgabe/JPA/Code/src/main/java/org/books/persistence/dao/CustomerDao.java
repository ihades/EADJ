package org.books.persistence.dao;

import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import org.books.persistence.entity.Customer;
import org.books.persistence.dto.CustomerInfo;

@Stateless
@LocalBean
public class CustomerDao extends GenericDao<Customer> {

    public CustomerDao(EntityManager mgr) {
        super(Customer.class, mgr);
    }

    public CustomerDao() {
        super(Customer.class);
    }

    /**
     * API Compatibility, like {@link #getByEmail(java.lang.String) }.
     *
     * @param email
     * @return
     */
    @Deprecated
    public Customer find(String email) {
        return getByEmail(email);
    }

    /**
     * Finds a customer by email address.
     *
     * @param email the email address to look for
     * @return the data of the found customer or null if no Customer is found.
     */
    public Customer getByEmail(String email) {
        try {
            return getEM().createNamedQuery("Customer.findByMail", Customer.class)
                    .setParameter(Customer.CUSTOMER_FIND_BY_MAIL_PARAM, email)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Searches for customers by name. A customer is included in the result list
     * if the specified name is part of its first or last name.
     *
     * @param name the name to search for
     * @return a list of matching customers (may be empty)
     */
    public List<CustomerInfo> searchByNamePart(String name) {
        return getEM().createNamedQuery(CustomerInfo.CUSTOMER_FIND_BY_NAME_PART_QUERY, CustomerInfo.class)
                .setParameter(CustomerInfo.CUSTOMER_FIND_BY_NAME_PART_PARAM, name)
                .getResultList();
    }
}
