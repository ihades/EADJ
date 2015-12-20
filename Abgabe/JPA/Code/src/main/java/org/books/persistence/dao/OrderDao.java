package org.books.persistence.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import org.books.persistence.entity.Customer;
import org.books.persistence.entity.Order;
import org.books.persistence.dto.OrderInfo;

@Stateless
@LocalBean
public class OrderDao extends GenericDao<Order> {

    protected OrderDao(EntityManager mgr) {
        super(Order.class, mgr);
    }

    public OrderDao() {
        super(Order.class);
    }

    /**
     * API Compatibility, like {@link #getByNumber(java.lang.String) }.
     *
     * @param number
     * @return
     */
    @Deprecated
    public Order find(String number) {
        return getByNumber(number);
    }

    /**
     * Finds a Order by it's Order-Number.
     *
     * @param number the Order number to look for
     * @return the data of the found book or null is no Order is found.
     */
    public Order getByNumber(String number) {
        try {
            return this.getEM().createNamedQuery("Order.findbyNumber", Order.class)
                    .setParameter(Order.ORDER_FIND_BY_NUMBER_PARAM, number)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * API Compatibility, like {@link #searchByCustomerAndYear(org.books.persistence.entity.Customer, int)
     * }.
     *
     * @param customer
     * @param year
     *
     * @return
     */
    @Deprecated
    public List<OrderInfo> search(Customer customer, int year) {
        return searchByCustomerAndYear(customer, year);
    }

    /**
     * Searches for Orders by Customer and Order-Year.
     *
     * @param customer
     * @param year
     * @return a list of matching Orders (may be empty)
     */
    public List<OrderInfo> searchByCustomerAndYear(Customer customer, int year) {

        Calendar beginCal = Calendar.getInstance();
        beginCal.set(year, 0, 1, 0, 0, 0);
        Date beginDate = new Date(beginCal.getTimeInMillis());

        Calendar endCal = Calendar.getInstance();
        endCal.set(year, 11, 31, 0, 0, 0);
        Date endDate = new Date(endCal.getTimeInMillis());

        return getEM().createNamedQuery(OrderInfo.QUERY_ORDER_BY_CUSTOMER_AND_DATE, OrderInfo.class)
                .setParameter(OrderInfo.QUERY_ORDER_BY_CUSTOMER_AND_DATE_PARAM_CUSTOMER, customer)
                .setParameter(OrderInfo.QUERY_ORDER_BY_CUSTOMER_AND_DATE_PARAM_BEGIN, beginDate)
                .setParameter(OrderInfo.QUERY_ORDER_BY_CUSTOMER_AND_DATE_PARAM_END, endDate)
                .getResultList();
    }
}
