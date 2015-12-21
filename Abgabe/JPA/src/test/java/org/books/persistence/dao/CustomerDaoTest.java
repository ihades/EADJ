package org.books.persistence.dao;

import org.books.persistence.testdata.AbstractTestBase;
import java.util.List;
import org.books.persistence.dto.CustomerInfo;
import org.books.persistence.entity.Address;
import org.books.persistence.entity.CreditCard;
import org.books.persistence.entity.Customer;
import static org.junit.Assert.*;
import org.junit.Test;

public class CustomerDaoTest extends AbstractTestBase {

    @Test
    public void testGetById() {
        Customer result = new CustomerDao(getEm()).getById(42l);
        assertNotNull(result);
        assertEquals("Kellie", result.getFirstName());
        assertEquals("Cochran", result.getLastName());
        assertEquals("egestas.ligula.Nullam@duiSuspendisseac.co.uk", result.getEmail());
    }

    @Test
    public void testGetByEmail() {
        Customer result = new CustomerDao(getEm()).getByEmail("egestas.ligula.Nullam@duiSuspendisseac.co.uk");
        assertNotNull(result);
        assertEquals("Kellie", result.getFirstName());
        assertEquals("Cochran", result.getLastName());
        assertEquals(new Long(42), result.getId());
    }

    @Test
    public void testNotExisting() {
        Customer result = new CustomerDao(getEm()).getById(402l);
        assertNull(result);
    }

    @Test
    public void testSearchByNamePart() {
        CustomerDao customerDao = new CustomerDao(getEm());
        List<CustomerInfo> result1 = customerDao.searchByNamePart("Little");
        List<CustomerInfo> result2 = customerDao.searchByNamePart("Dawn");
        List<CustomerInfo> result3 = customerDao.searchByNamePart("j");
        List<CustomerInfo> result4 = customerDao.searchByNamePart("jo");
        List<CustomerInfo> result5 = customerDao.searchByNamePart("PAT");
        assertEquals(1, result1.size());
        assertEquals(1, result2.size());
        assertEquals(16, result3.size());
        assertEquals(6, result4.size());
        assertEquals(2, result5.size());
        assertEquals("Leila", result1.get(0).getFirstName());
        assertEquals("Guthrie", result2.get(0).getLastName());
        assertEquals("Justin", result3.get(0).getFirstName());
        assertEquals("Faith", result4.get(0).getFirstName());
        assertEquals("Ann", result5.get(0).getFirstName());
    }

    @Test
    public void testCreate() {
        getEm().clear();
        getEm().getTransaction().begin();
        Customer result = new Customer();
        result.setFirstName("Christoph");
        result.setLastName("Bühlmann");
        result.setEmail("christoph@buehlmann.tk");
        result.setAddress(new Address("Sulgenbachstrasse 14", "Bern", "3007", "Switzerland"));
        result.setCreditCard(new CreditCard(CreditCard.Type.MasterCard, "5380152162655250", 12, 2020));
        CustomerDao customerDao = new CustomerDao(getEm());
        customerDao.create(result);
        assertNotNull(result.getId());
        getEm().getTransaction().commit();

        //simulate new Call -> new Transaction
        getEm().clear();
        getEm().getTransaction().begin();
        List<CustomerInfo> resultList = customerDao.searchByNamePart("Büh");
        assertEquals(1, resultList.size());
        assertEquals("Christoph", resultList.get(0).getFirstName());
        getEm().getTransaction().commit();
    }
}
