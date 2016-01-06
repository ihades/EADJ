package org.books.ejb.impl;

import java.util.List;
import javax.ejb.Stateless;
import org.books.ejb.exception.CustomerAlreadyExistsException;
import org.books.ejb.exception.CustomerNotFoundException;
import org.books.ejb.CustomerServiceLocal;
import org.books.ejb.CustomerServiceRemote;
import org.books.ejb.exception.InvalidPasswordException;
import org.books.ejb.dto.CustomerDTO;
import org.books.persistence.dto.CustomerInfo;

/**
 *
 * @author cb
 */
@Stateless(name = "CustomerService")
public class CustomerServiceBean implements CustomerServiceLocal, CustomerServiceRemote {

    @Override
    public void authenticateCustomer(String email, String password) throws CustomerNotFoundException, InvalidPasswordException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void changePassword(String email, String password) throws CustomerNotFoundException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public CustomerDTO findCustomer(String customerNr) throws CustomerNotFoundException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public CustomerDTO findCustomerByEmail(String email) throws CustomerNotFoundException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String registerCustomer(CustomerDTO customer, String password) throws CustomerAlreadyExistsException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<CustomerInfo> searchCustomers(String name) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateCustomer(CustomerDTO customer) throws CustomerNotFoundException, CustomerAlreadyExistsException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
