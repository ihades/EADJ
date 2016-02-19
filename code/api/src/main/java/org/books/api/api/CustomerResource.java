package org.books.api.api;

import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import javax.ws.rs.core.Response;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import org.books.api.api.entities.Registration;
import org.books.ejb.CustomerService;
import org.books.ejb.CustomerServiceLocal;
import org.books.ejb.dto.CustomerDTO;
import org.books.ejb.exception.CustomerAlreadyExistsException;
import org.books.ejb.exception.CustomerNotFoundException;
import org.books.persistence.dto.CustomerInfo;

@RequestScoped
@Path("customers")
public class CustomerResource {

    @EJB(beanInterface = CustomerServiceLocal.class)
    private CustomerService cs;

    /**
     * Registers a customer with the bookstore. The email address and password
     * will be used to authenticate the customer.
     *
     * @param registration the data of the customer to be registered (the number
     * must be null)
     * @param response
     * @return the Number of the new Customer
     * @responseMessage 201 created
     * @responseMessage 400 bad request (incomplete customer data)
     * @responseMessage 409 conflict (email already used)
     * @responseMessage 500 internal server error (unexpected system error)
     */
    @POST
    @Produces({TEXT_PLAIN})
    @Consumes({APPLICATION_XML, APPLICATION_JSON})
    public String registerCustomer(Registration registration, @Context final HttpServletResponse response) {

        if (registration.getCustomer() == null
                || registration.getPassword() == null
                || registration.getPassword().isEmpty()) {
            throw new WebApplicationException("incomplete customer data",
                    Response.status(BAD_REQUEST).entity("incomplete customer data").build());
        }

        ensureCompletenessWithoutId(registration.getCustomer());
        try {
            registration.getCustomer().setNumber(null);
            String number = cs.registerCustomer(registration.getCustomer(), registration.getPassword())
                    .getNumber();
            response.setStatus(HttpServletResponse.SC_CREATED);
            try {
                response.flushBuffer();
            } catch (Exception e) {
            }
            return number;
        } catch (CustomerAlreadyExistsException ex) {
            throw new WebApplicationException(ex.getMessage(),
                    Response.status(CONFLICT).entity(ex.getMessage()).build());
        }
    }

    /**
     * Finds a customer by number.
     *
     * @param number the number of the customer
     * @return the data of the found customer
     * @responseMessage 404 not found (customer not found)
     * @responseMessage 500 internal server error (unexpected system error)
     */
    @GET
    @Produces({APPLICATION_XML, APPLICATION_JSON})
    @Path("{number}")
    public CustomerDTO findCustomer(@PathParam("number") String number) {
        try {
            return cs.findCustomer(number);
        } catch (CustomerNotFoundException ex) {
            throw new WebApplicationException(ex.getMessage(),
                    Response.status(NOT_FOUND).entity(ex.getMessage()).build());
        }
    }

    /**
     * Searches for customers by name. A customer is included in the result list
     * if the specified name is part of the customer's first or last name.
     *
     * @param name the name to search for
     * @return a list of matching customers (may be empty)
     * @responseMessage 400 bad request (name missing)
     * @responseMessage 500 internal server error (unexpected system error)
     */
    @GET
    @Produces({APPLICATION_XML, APPLICATION_JSON})
    public List<CustomerInfo> searchCustomer(@QueryParam("name") String name) {
        if (name != null && !name.isEmpty()) {
            return cs.searchCustomers(name);
        } else {
            throw new WebApplicationException("keyword missing",
                    Response.status(BAD_REQUEST).build());
        }
    }

    /**
     * Updates the data of a customer. If the email address is to be changed,
     * the new email address is used for authentication.
     *
     * @param number the number of the customer to be updated
     * @param customer the data of the customer to be updated (the number must
     * not be null)
     * @responseMessage 400 bad request (incomplete customer data)
     * @responseMessage 404 not found (customer not found)
     * @responseMessage 409 conflict (new email already used)
     * @responseMessage 500 internal server error (unexpected system error)
     */
    @PUT
    @Consumes({APPLICATION_XML, APPLICATION_JSON})
    @Path("{number}")
    public void updateCustomer(@PathParam("number") String number, CustomerDTO customer) {
        ensureCompletenessWithId(number, customer);
        try {
            cs.updateCustomer(customer);
        } catch (CustomerNotFoundException ex) {
            throw new WebApplicationException(ex.getMessage(),
                    Response.status(NOT_FOUND).entity(ex.getMessage()).build());
        } catch (CustomerAlreadyExistsException ex) {
            throw new WebApplicationException(ex.getMessage(),
                    Response.status(CONFLICT).entity(ex.getMessage()).build());
        }

    }

    private void ensureCompletenessWithId(String number, CustomerDTO customer) throws WebApplicationException {
        if (customer.getNumber() == null
                || customer.getNumber().isEmpty()
                || number == null
                || number.isEmpty()
                || !number.equals(customer.getNumber())) {
            throw new WebApplicationException("incomplete customer data",
                    Response.status(BAD_REQUEST).entity("incomplete customer data").build());
        }
        ensureCompletenessWithoutId(customer);
    }

    private void ensureCompletenessWithoutId(CustomerDTO customer) throws WebApplicationException {
        if (customer.getAddress() == null
                || customer.getAddress().getCity() == null
                || customer.getAddress().getCountry() == null
                || customer.getAddress().getPostalCode() == null
                || customer.getAddress().getStreet() == null
                || customer.getCreditCard() == null
                || customer.getCreditCard().getExpirationMonth() == null
                || customer.getCreditCard().getExpirationYear() == null
                || customer.getCreditCard().getNumber() == null
                || customer.getCreditCard().getType() == null
                || customer.getEmail() == null
                || customer.getFirstName() == null
                || customer.getLastName() == null) {
            throw new WebApplicationException("incomplete customer data",
                    Response.status(BAD_REQUEST).entity("incomplete customer data").build());
        }
    }

}
