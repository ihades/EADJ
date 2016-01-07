package org.books.ejb.impl;

import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.books.ejb.exception.CustomerAlreadyExistsException;
import org.books.ejb.exception.CustomerNotFoundException;
import org.books.ejb.CustomerServiceLocal;
import org.books.ejb.CustomerServiceRemote;
import org.books.ejb.exception.InvalidPasswordException;
import org.books.ejb.dto.CustomerDTO;
import org.books.persistence.dao.CustomerDao;
import org.books.persistence.dao.LoginDao;
import org.books.persistence.dto.CustomerInfo;
import org.books.persistence.entity.Customer;
import org.books.persistence.entity.Login;
import org.modelmapper.ModelMapper;

/**
 *
 * @author cb
 */
@Stateless(name = "CustomerService")
public class CustomerServiceBean implements CustomerServiceLocal, CustomerServiceRemote {

    @EJB
    private CustomerDao customerDao;

    @EJB
    private LoginDao loginDao;

    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    public void authenticateCustomer(String email, String password) throws CustomerNotFoundException, InvalidPasswordException {
        Login login = loginDao.find(email);
        if (login == null) {
            throw new CustomerNotFoundException();
        }
        if (!login.isPasswordValid(password)) {
            throw new InvalidPasswordException();
        }
    }

    @Override
    public void changePassword(String email, String password) throws CustomerNotFoundException {
        Login login = loginDao.find(email);
        if (login == null) {
            throw new CustomerNotFoundException();
        }
        login.setPassword(password);
        loginDao.update(login);
    }

    @Override
    public CustomerDTO findCustomer(String customerNr) throws CustomerNotFoundException {
        return modelMapper.map(getCustomerByNumber(customerNr), CustomerDTO.class);
    }

    @Override
    public CustomerDTO findCustomerByEmail(String email) throws CustomerNotFoundException {
        Customer customer = customerDao.findByEmail(email);
        if (customer == null) {
            throw new CustomerNotFoundException();
        }
        return modelMapper.map(customer, CustomerDTO.class);
    }

    @Override
    public CustomerDTO registerCustomer(CustomerDTO customerDto, String password) throws CustomerAlreadyExistsException {
        ensureUniqueness(customerDto.getEmail());
        if (customerDto.getNumber() != null) {
            throw new CustomerAlreadyExistsException();
        }
        loginDao.create(new Login(customerDto.getEmail(), password));
        Customer customer = modelMapper.map(customerDto, Customer.class);
        customerDao.create(customer);
        customer.setNumber("C-" + customer.getId());
        customerDao.update(customer);
        return modelMapper.map(customer, CustomerDTO.class);
    }

    @Override
    public List<CustomerInfo> searchCustomers(String name) {
        return customerDao.searchByNamePart(name);
    }

    @Override
    public void updateCustomer(CustomerDTO customerDto) throws CustomerNotFoundException, CustomerAlreadyExistsException {
        ensureUniqueness(customerDto.getEmail());
        Customer customer = getCustomerByNumber(customerDto.getNumber());
        updateLogin(customer.getEmail(), customerDto.getEmail());
    }

    private void updateLogin(String oldEmail, String newEmail) {
        Login login = loginDao.find(oldEmail);
        login.setUserName(newEmail);
        loginDao.update(login);
    }

    private Customer getCustomerByNumber(String customerNr) throws CustomerNotFoundException {
        Customer customer = customerDao.findByNumber(customerNr);
        if (customer == null) {
            throw new CustomerNotFoundException();
        }
        return customer;
    }

    private void ensureUniqueness(String email) throws CustomerAlreadyExistsException {
        if (customerDao.findByEmail(email) != null) {
            throw new CustomerAlreadyExistsException();
        }
    }

}
