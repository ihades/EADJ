package org.books.api.api.validators;

import java.io.File;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;
import org.books.ejb.dto.CustomerDTO;

@Provider
@Consumes(MediaType.APPLICATION_XML)
public class CustomerValidator extends ValidatingReader<CustomerDTO> {

    @Override
    protected File getSchema() {
        return new File(getClass().getClassLoader().getResource("customers.xsd").getFile());
    }

}
