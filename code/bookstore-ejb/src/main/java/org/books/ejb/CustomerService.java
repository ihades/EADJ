package org.books.ejb;

import java.io.Serializable;
import java.util.List;
import org.books.ejb.dto.CustomerDTO;
import org.books.ejb.exception.CustomerAlreadyExistsException;
import org.books.ejb.exception.CustomerNotFoundException;
import org.books.ejb.exception.InvalidPasswordException;
import org.books.persistence.dto.CustomerInfo;

/**
 *
 * @author cb
 */
public interface CustomerService extends Serializable {

    /**
     * Authenticates a customer.
     *
     * @param email - the email address of the customer
     * @param password - the password of the customer
     * @throws org.books.ejb.exception.CustomerNotFoundException - if no
     * customer with the specified email address exists
     * @throws org.books.ejb.exception.InvalidPasswordException - if the
     * password is invalid
     */
    void authenticateCustomer(String email, String password) throws CustomerNotFoundException,
            InvalidPasswordException;

    /**
     * Changes the password of a customer.
     *
     * @param email - the email address of the customer
     * @param password - the new password of the customer
     * @throws org.books.ejb.exception.CustomerNotFoundException - if no
     * customer with the specified email address exists
     *
     */
    void changePassword(String email, String password) throws CustomerNotFoundException;

    /**
     * Finds a customer by number.
     *
     * @param customerNr - the number of the customer
     * @return the data of the found customer
     * @throws org.books.ejb.exception.CustomerNotFoundException - if no
     * customer with the specified number exists
     */
    CustomerDTO findCustomer(String customerNr) throws CustomerNotFoundException;

    /**
     * Finds a customer by email address.
     *
     * @param email - the email address of the customer
     * @return the data of the found customer
     * @throws org.books.ejb.exception.CustomerNotFoundException - if no
     * customer with the specified email address exists
     */
    CustomerDTO findCustomerByEmail(String email) throws CustomerNotFoundException;

    /**
     * Registers a customer with the bookstore. The email address and password
     * will be used to authenticate the customer.
     *
     * @param customer - the data of the customer to be registered (the number
     * must be null)
     * @param password - the password of the customer
     * @throws org.books.ejb.exception.CustomerAlreadyExistsException - if
     * another customer with the same email address already exists
     * @return the data of the registered customer (including the number)
     */
    CustomerDTO registerCustomer(CustomerDTO customer, String password) throws CustomerAlreadyExistsException;

    /**
     * Searches for customers by name. A customer is included in the result list
     * if the specified name is part of the customer's first or last name.
     *
     * @param name - the name to search for
     * @return a list of matching customers (may be empty)
     */
    List<CustomerInfo> searchCustomers(String name);

    /**
     * Updates the data of a customer. If the email address is to be changed,
     * the new email address is used for authentication.
     *
     * @param customer - the data of the customer to be updated (the number must
     * not be null)
     * @throws org.books.ejb.exception.CustomerNotFoundException - if no
     * customer with the specified number exists
     * @throws org.books.ejb.exception.CustomerAlreadyExistsException - if
     * another customer with the same email address already exists
     */
    void updateCustomer(CustomerDTO customer) throws CustomerNotFoundException, CustomerAlreadyExistsException;
}
