package ch.bedag.sa.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

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
     * Converts {@code this} to an instance of another class by recursively
     * copying all common properties. This is equivalent to serializing to JSON,
     * and deserializing to an object of the target class, while ignoring
     * unknown properties.
     */
    public <M> M convertTo(Class<M> clazz) {
        return MAPPER.convertValue(this, clazz);
    }
}
