package org.books.api.api.validators;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Providers;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.xml.sax.SAXException;

public abstract class ValidatingReader<T> implements MessageBodyReader<T> {

    @Context
    protected Providers providers;

    @Override
    public boolean isReadable(Class<?> arg0, Type arg1, Annotation[] arg2,
            MediaType arg3) {

        Class<T> readableClass = (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
        return arg0 == readableClass;
    }

    @Override
    public T readFrom(Class<T> arg0, Type arg1, Annotation[] arg2,
            MediaType arg3, MultivaluedMap<String, String> arg4,
            InputStream arg5) throws IOException, WebApplicationException {

        T type = null;
        JAXBContext jaxbContext = null;
        ContextResolver<JAXBContext> resolver = providers.getContextResolver(
                JAXBContext.class, arg3);
        try {
            if (resolver != null) {
                jaxbContext = resolver.getContext(arg0);
            }
            if (jaxbContext == null) {
                jaxbContext = JAXBContext.newInstance(arg0);

            }
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = schemaFactory.newSchema(getSchema());
            unmarshaller.setSchema(schema);
            type = (T) unmarshaller.unmarshal(arg5);

        } catch (JAXBException | SAXException e) {
            if (e.getCause() != null) {
                throw new WebApplicationException(e.getCause().getMessage(),
                        Response.status(BAD_REQUEST).entity(e.getCause().getMessage()).build());
            } else if (e.getMessage() != null) {
                throw new WebApplicationException(e.getMessage(),
                        Response.status(BAD_REQUEST).entity(e.getMessage()).build());
            } else {
                throw new WebApplicationException(BAD_REQUEST);
            }
        }

        return type;
    }

    protected abstract File getSchema();

}
