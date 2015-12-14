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

    public Customer getByEmail(String email) {
        try {
            return getEM().createNamedQuery("Customer.findByMail", Customer.class)
                    .setParameter(Customer.CUSTOMER_FIND_BY_MAIL_PARAM, email)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<CustomerInfo> searchByNamePart(String name) {
        return getEM().createNamedQuery(CustomerInfo.CUSTOMER_FIND_BY_NAME_PART_QUERY, CustomerInfo.class)
                .setParameter(CustomerInfo.CUSTOMER_FIND_BY_NAME_PART_PARAM, name)
                .getResultList();
    }
}
