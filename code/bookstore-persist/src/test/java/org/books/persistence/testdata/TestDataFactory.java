/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.books.persistence.testdata;

import org.books.persistence.entity.Book;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
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
        em.getTransaction().begin();
        Customer c42 = em.find(Customer.class, 42l);
        Customer c84 = em.find(Customer.class, 84l);
        em.persist(createOrder(
                c42,
                OrderData.O_2222_001.amount(),
                OrderData.O_2222_001.date(),
                OrderData.O_2222_001.number(),
                c42.getAddress(),
                c42.getCreditCard())
        );
        em.persist(createOrder(
                c84,
                OrderData.O_3333_001.amount(),
                OrderData.O_3333_001.date(),
                OrderData.O_3333_001.number(),
                c84.getAddress(),
                c84.getCreditCard()));
        em.getTransaction().commit();
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

}
