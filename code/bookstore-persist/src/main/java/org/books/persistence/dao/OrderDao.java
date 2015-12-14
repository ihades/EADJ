package org.books.persistence.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import org.books.persistence.entity.Customer;
import org.books.persistence.entity.Order;
import org.books.persistence.dto.OrderInfo;

@Stateless
@LocalBean
public class OrderDao extends GenericDao<Order> {

    public OrderDao(EntityManager mgr) {
        super(Order.class, mgr);
    }

    public OrderDao() {
        super(Order.class);
    }

    public Order getByNumber(String number) {
        return this.getEM().createNamedQuery("Order.findbyNumber", Order.class)
                .setParameter(Order.ORDER_FIND_BY_NUMBER_PARAM, number)
                .getSingleResult();
    }

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
