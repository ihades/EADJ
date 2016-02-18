package org.books.api.api.validators;

import java.io.File;
import javax.ws.rs.Consumes;
import javax.ws.rs.ext.Provider;
import org.books.api.api.entities.OrderRequest;

@Provider
@Consumes("application/xml")
public class OrderRequestValidator extends ValidatingReader<OrderRequest> {

    @Override
    protected File getSchema() {
        return new File(getClass().getClassLoader().getResource("orders.xsd").getFile());
    }

}
