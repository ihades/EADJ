package ch.bedag.sa.rest;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * A client for sending type safe requests to a particular REST resource, for
 * use in integration tests.
 */
public abstract class ApiClient {

    protected static final MediaType JSON = MediaType.APPLICATION_JSON_TYPE;

    /**
     * We ask for the response content even if it must be empty to make Jersey
     * check the response status (surprisingly, it does not check it otherwise).
     */
    protected static final Class<?> VOID = String.class;

    protected static final WebTarget baseTarget;

    static {
        // boot the application to test

        String targetUrl = System.getProperty("integration.test.target", "http://localhost:8080/bookstore/rest/");
//        Client client = ClientBuilder.newBuilder().register(new TestAuthenticationSender()).build();
        Client client = ClientBuilder.newClient();
        baseTarget = client.target(targetUrl);
    }

    protected RuntimeException decorate(RuntimeException e) {
        if (e instanceof BadRequestException) {
            // the exception message only contains the http status and reason,
            // but we usually receive a more specific message in the body,
            // which we want to appear in stacktraces, as that's all we'll see in the JUnit output
            Response response = ((BadRequestException) e).getResponse();
            if (MediaType.TEXT_PLAIN_TYPE.isCompatible(response.getMediaType())) {
                String message = response.readEntity(String.class);
                return new BadRequestException(e.getMessage() + ": " + message, response);
            }
        }
        return e;
    }
}
