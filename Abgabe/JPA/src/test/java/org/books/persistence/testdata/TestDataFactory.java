/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.books.persistence.testdata;

import org.books.persistence.entity.Book;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import org.books.persistence.entity.Address;
import org.books.persistence.entity.CreditCard;
import org.books.persistence.entity.Customer;
import org.books.persistence.entity.OrderItem;
import org.books.persistence.entity.Order;
import org.dbunit.IDatabaseTester;
import org.dbunit.PropertiesBasedJdbcDatabaseTester;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;

public class TestDataFactory {

    private final EntityManager em;
    private static final String DB_UNIT_PROPERTIES = "/dbunit.properties";
    private static final String DB_UNIT_DATASET = "/dataset.xml";

    public TestDataFactory(EntityManager em) {
        this.em = em;
    }

    public void setUp() {
        em.getTransaction().begin();
        Customer c42 = em.find(Customer.class, 42l);
        em.getTransaction().commit();
        initDBUnit();
        initManualData();
    }

    public void deleteTestData() {
        em.getTransaction().begin();
        em.createQuery("DELETE FROM OrderItem").executeUpdate();
        em.createQuery("DELETE FROM Book").executeUpdate();
        em.createQuery("DELETE FROM Order").executeUpdate();
        em.createQuery("DELETE FROM Customer").executeUpdate();
        em.createQuery("DELETE FROM Login").executeUpdate();
        em.getTransaction().commit();
    }

    private void initDBUnit() {
        try {
            System.getProperties().load(getClass().getResourceAsStream(DB_UNIT_PROPERTIES));
            IDatabaseTester databaseTester = new PropertiesBasedJdbcDatabaseTester();
            IDataSet dataset = new FlatXmlDataSetBuilder().build(getClass().getResourceAsStream(DB_UNIT_DATASET));
            databaseTester.setSetUpOperation(DatabaseOperation.REFRESH);
            databaseTester.setTearDownOperation(DatabaseOperation.NONE);
            databaseTester.setDataSet(dataset);
            databaseTester.onSetup();
        } catch (Exception ex) {
            Logger.getLogger(AbstractTestBase.class.getName()).log(Level.SEVERE, "Fehler beim Aufsetzen der DBUnit-Daten!", ex);
        }
    }

    private void initManualData() {
        List<Book> testBooks = getTestBooks();
        em.getTransaction().begin();
        Customer c42 = em.find(Customer.class, 42l);
        Customer c84 = em.find(Customer.class, 84l);
        em.persist(createOrder(
                c42,
                OrderData.ORDER_6.amount(),
                OrderData.ORDER_6.date(),
                OrderData.ORDER_6.number(),
                c42.getAddress(),
                c42.getCreditCard(),
                createOrderItem(testBooks.get(0), 2),
                createOrderItem(testBooks.get(3), 1))
        );
        em.persist(createOrder(
                c84,
                OrderData.ORDER_8.amount(),
                OrderData.ORDER_8.date(),
                OrderData.ORDER_8.number(),
                c84.getAddress(),
                c84.getCreditCard(),
                createOrderItem(testBooks.get(0), 2))
        );
        em.getTransaction().commit();
    }

    private OrderItem createOrderItem(Book book, int quantity) {
        return new OrderItem(book, quantity);
    }

    private Order createOrder(Customer customer, BigDecimal amount, Date date, String number, Address address, CreditCard card, OrderItem... lineItems) {
        Order order = new Order();
        order.setCustomer(customer);
        order.setAmount(amount);
        order.setDate(date);
        order.setNumber(number);
        order.setStatus(Order.Status.accepted);
        order.setAddress(address);
        order.setCreditCard(card);
        order.setItems(Arrays.asList(lineItems));
        return order;
    }

    private List<Book> getTestBooks() {
        List<Book> books = new ArrayList<>();
        for (long i = 1; i < 10l; i++) {
            Book book = em.find(Book.class, i);
            if (book != null) {
                books.add(book);
            }
        }
        return books;
    }

}
