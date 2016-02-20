# rest-client-generator

This maven plugin generates typesafe clients for a swagger-documented REST API for use in tests.

## FAQ

### Doesn't swagger-codegen do this already? 

To some extent, but: 

* the "JavaClient" binding creates an entire maven project, i.e. assumes the generated client will be built separately. 
This plugin adds the generated sources to the test-compile phase of the current maven build, permitting the swagger spec and the tests to coexist in the same maven module.
* by default, the "JavaClient" language binding creates api clients that depend on the Jersey client API, which was replaced by the JAX-RS Client API in Jersey 2.0. The api clients generated by our plugin directly use the JAX-RS Client API, and are therefore compatible with current versions of Jersey.
* our plugin generates model and api client that are optimized for tests (by extending a suitable superclass)

### The generated classes extend classes from ch.bedag.sa.rest

It is intended that you modify the templates to refer to your own superclasses. Our classes look like this, but you'll want to modify them to suit your needs:

```
/**
 * A client for sending type safe requests to a particular REST resource, for use in integration
 * tests.
 */
public abstract class ApiClient {

    protected static final MediaType JSON = MediaType.APPLICATION_JSON_TYPE;

    /**
     * We ask for the response content even if it must be empty to make Jersey check the response
     * status (surprisingly, it does not check it otherwise).
     */
    protected static final Class<?> VOID = String.class;

    protected static final WebTarget baseTarget;

    static {
        // boot the application to test
       
        String targetUrl = System.getProperty("integration.test.target", "http://localhost:8080/api");
        Client client = ClientBuilder.newBuilder().register(new TestAuthenticationSender()).build();
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
```

and 

```
/**
 * A DTO used as parameter or return value in the API.
 */
public abstract class ApiModel {

    private static final ObjectMapper MAPPER;

    static {
        MAPPER = new ObjectMapper();
        MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public String toString() {
        try {
            return MAPPER.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || toString().equals(obj.toString());
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    /**
     * Converts {@code this} to an instance of another class by recursively copying all common
     * properties. This is equivalent to serializing to JSON, and deserializing to an object of the
     * target class, while ignoring unknown properties.
     */
    public <M> M convertTo(Class<M> clazz) {
        return MAPPER.convertValue(this, clazz);
    }
}
```