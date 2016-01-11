package org.books.persistence.dao;

import org.books.persistence.testdata.AbstractTestBase;
import java.util.List;
import org.books.persistence.dto.OrderInfo;
import org.books.persistence.entity.Customer;
import org.books.persistence.entity.Order;
import org.books.persistence.testdata.OrderData;
import static org.junit.Assert.*;
import org.junit.Test;

public class OrderDaoTest extends AbstractTestBase {

    @Test
    public void testGetByNumber() {
        Order result = new OrderDao(getEm()).findByNumber(OrderData.ORDER_6.number());
        assertNotNull(result);
        assertEquals(OrderData.ORDER_6.number(), result.getNumber());
        assertEquals(new Long(42), result.getCustomer().getId());
        assertEquals(2, result.getItems().size());

        result = new OrderDao(getEm()).findByNumber(OrderData.ORDER_8.number());
        assertNotNull(result);
        assertEquals(OrderData.ORDER_8.number(), result.getNumber());
        assertEquals(new Long(84), result.getCustomer().getId());
        assertEquals(1, result.getItems().size());
    }

    @Test
    public void testSearchByCustomerAndYear() {
        OrderDao orderDao = new OrderDao(getEm());
        CustomerDao customerDao = new CustomerDao(getEm());
        Customer customer1 = customerDao.findByEmail("egestas.ligula.Nullam@duiSuspendisseac.co.uk");

        List<OrderInfo> result1 = orderDao.searchByCustomerAndYear(customer1, 2014);
        List<OrderInfo> result2 = orderDao.searchByCustomerAndYear(customer1, 1999);

        assertEquals(1, result1.size());
        assertEquals(0, result2.size());
        assertEquals(OrderData.ORDER_6.number(), result1.get(0).getNumber());
    }
}
