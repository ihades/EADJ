package org.books.api.api.validators;

import java.io.File;
import javax.ws.rs.Consumes;
import javax.ws.rs.ext.Provider;
import org.books.api.api.entities.Registration;

@Provider
@Consumes("application/xml")
public class RegistrationValidator extends ValidatingReader<Registration> {

    @Override
    protected File getSchema() {
        return new File(getClass().getClassLoader().getResource("customers.xsd").getFile());
    }

}
