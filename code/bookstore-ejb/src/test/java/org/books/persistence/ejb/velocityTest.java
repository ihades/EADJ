package org.books.persistence.ejb;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.books.ejb.dto.AddressDTO;
import org.books.ejb.dto.BookDTO;
import org.books.ejb.dto.CreditCardDTO;
import org.books.ejb.dto.CustomerDTO;
import org.books.ejb.dto.OrderDTO;
import org.books.ejb.dto.OrderItemDTO;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 *
 * @author cb
 */
public class velocityTest {

    private final AddressDTO addressDTO = new AddressDTO("Wasserschloss 42",
            "Bern",
            "3011",
            "Switzerland");

    private final CreditCardDTO validCreditCardDTO = new CreditCardDTO(CreditCardDTO.Type.MasterCard,
            "5555555555554444",
            2,
            2017);

    private CustomerDTO customerDTO = new CustomerDTO(Util.numbGen() + "@gmail.com",
            "Erwin",
            "Bazong",
            null,
            addressDTO,
            validCreditCardDTO);

    //should be different from already existing books in db.
    private BookDTO bookDTO = new BookDTO(Util.numbGen(), "Java9", "Bill Gates", "M$-Press", new Integer(2015), BookDTO.Binding.Hardcover, 1000, new BigDecimal("33.3"));
    private OrderItemDTO orderItemDTO = new OrderItemDTO(bookDTO, bookDTO.getPrice(), 1);

    private List<OrderItemDTO> orderList = new ArrayList<OrderItemDTO>();
    private OrderDTO testOrderDTO = new OrderDTO();

    @BeforeTest
    public void init() {
        orderList.add(orderItemDTO);
        testOrderDTO.setCustomer(customerDTO);
        testOrderDTO.setAddress(addressDTO);
        testOrderDTO.setAmount(new BigDecimal(72l));
        testOrderDTO.setCreditCard(validCreditCardDTO);
        testOrderDTO.setDate(new Date());
        testOrderDTO.setItems(orderList);
        testOrderDTO.setNumber("NUMBER123456");
        testOrderDTO.setStatus(OrderDTO.Status.shipped);
    }

    @Test
    public void test() throws Exception {
        VelocityEngine ve = new VelocityEngine();
        Properties p = new Properties();
        p.put("resource.loader", "class");
        p.put("class.resource.loader.class", " org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        ve.init(p);

        VelocityContext context = new VelocityContext();
        context.put("order", testOrderDTO);
        Template t = ve.getTemplate("orderPlacedTemplate");
        StringWriter writer = new StringWriter();
        t.merge(context, writer);

        System.out.print(writer.toString());
    }

}
