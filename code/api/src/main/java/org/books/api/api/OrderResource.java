package org.books.api.api;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletResponse;
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
import javax.ws.rs.core.Response;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.FORBIDDEN;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.PAYMENT_REQUIRED;
import org.books.api.api.entities.OrderRequest;
import org.books.ejb.OrderService;
import org.books.ejb.dto.OrderDTO;
import org.books.ejb.exception.BookNotFoundException;
import org.books.ejb.exception.CustomerNotFoundException;
import org.books.ejb.exception.OrderAlreadyShippedException;
import org.books.ejb.exception.OrderNotFoundException;
import org.books.ejb.exception.PaymentFailedException;
import org.books.persistence.dto.OrderInfo;

@Path("orders")
public class OrderResource {

    private OrderService os;

    @PostConstruct
    public void init() {
        try {
            InitialContext ctx = new InitialContext();
            os = (OrderService) ctx.lookup("java:global/bookstore-app/bookstore-ejb/OrderService!org.books.ejb.OrderServiceLocal");
        } catch (NamingException ne) {
            System.out.println("\n[OrderResource] NamingException: " + ne);
            ne.printStackTrace();
        }
    }

    /**
     * Places an order on the bookstore.
     *
     * @param orderRequest customerNr - the number of the customer / items - the
     * order items
     * @param response
     * @return the data of the placed order
     * @responseMessage 201 created
     * @responseMessage 400 bad request (incomplete order data)
     * @responseMessage 404 not found (customer or book not found)
     * @responseMessage 402 payment required (payment error)
     * @responseMessage 500 internal server error (unexpected system error)
     */
    @POST
    @Produces({APPLICATION_XML, APPLICATION_JSON})
    public OrderDTO placeOrder(OrderRequest orderRequest, @Context final HttpServletResponse response) {
        ensureCompleteness(orderRequest);
        try {
            OrderDTO order = os.placeOrder(orderRequest.getCustomerNr(), orderRequest.getItems());
            response.setStatus(HttpServletResponse.SC_CREATED);
            try {
                response.flushBuffer();
            } catch (Exception e) {
            }
            return order;
        } catch (CustomerNotFoundException | BookNotFoundException ex) {
            throw new WebApplicationException(ex.getMessage(),
                    Response.status(NOT_FOUND).entity(ex.getMessage()).build());
        } catch (PaymentFailedException ex) {
            throw new WebApplicationException(ex.getMessage(),
                    Response.status(PAYMENT_REQUIRED).entity(ex.getMessage()).build());
        }
    }

    /**
     * Finds an order by number.
     *
     * @param number the number of the order
     * @return the data of the found order
     * @responseMessage 404 not found (order not found)
     * @responseMessage 500 internal server error (unexpected system error)
     */
    @GET
    @Produces({APPLICATION_XML, APPLICATION_JSON})
    @Path("{number}")
    public OrderDTO findOrder(@PathParam("number") String number) {
        try {
            return os.findOrder(number);
        } catch (OrderNotFoundException ex) {
            throw new WebApplicationException(ex.getMessage(),
                    Response.status(NOT_FOUND).entity(ex.getMessage()).build());
        }
    }

    /**
     * Searches for orders by customer and year.
     *
     * @param customerNr the Customernumber address of the customer
     * @param year the year of the orders
     * @return a list of matching orders (may be empty)
     * @responseMessage 400 bad request (name missing)
     * @responseMessage 404 not found (customer not found)
     * @responseMessage 500 internal server error (unexpected system error)
     */
    @GET
    @Produces({APPLICATION_XML, APPLICATION_JSON})
    public List<OrderInfo> searchOrdersOfCustomer(@QueryParam("customerNr") String customerNr, @QueryParam("year") String year) {
        if (customerNr != null && !customerNr.isEmpty()
                && year != null && !year.isEmpty()) {
            try {
                return os.searchOrders(customerNr, Integer.parseInt(year));
            } catch (CustomerNotFoundException ex) {
                throw new WebApplicationException(ex.getMessage(),
                        Response.status(NOT_FOUND).entity(ex.getMessage()).build());
            } catch (NumberFormatException ex) {
                throw new WebApplicationException(ex.getMessage(),
                        Response.status(BAD_REQUEST).entity(ex.getMessage()).build());
            }

        } else {
            throw new WebApplicationException("keyword missing",
                    Response.status(BAD_REQUEST).build());
        }
    }

    /**
     * Cancels an order.
     *
     * @param number the number of the order
     * @responseMessage 403 forbidden (order not cancelable)
     * @responseMessage 404 not found (order not found)
     * @responseMessage 500 internal server error (unexpected system error)
     */
    @PUT
    @Produces({APPLICATION_XML, APPLICATION_JSON})
    @Path("{number}")
    public void cancelOrder(@PathParam("number") String number) {
        try {
            os.cancelOrder(number);
        } catch (OrderNotFoundException ex) {
            throw new WebApplicationException(ex.getMessage(),
                    Response.status(NOT_FOUND).entity(ex.getMessage()).build());
        } catch (OrderAlreadyShippedException ex) {
            throw new WebApplicationException(ex.getMessage(),
                    Response.status(FORBIDDEN).entity(ex.getMessage()).build());
        }

    }

    private void ensureCompleteness(OrderRequest request) throws WebApplicationException {

    }

}