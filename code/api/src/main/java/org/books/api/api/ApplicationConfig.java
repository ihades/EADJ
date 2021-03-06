package org.books.api.api;

import java.util.Set;
import javax.ws.rs.core.Application;

@javax.ws.rs.ApplicationPath("rest")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method. It is automatically
     * populated with all resources defined in the project. If required, comment
     * out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(org.books.api.api.BookRessource.class);
        resources.add(org.books.api.api.CustomerResource.class);
        resources.add(org.books.api.api.OrderResource.class);
        resources.add(org.books.api.api.validators.CustomerValidator.class);
        resources.add(org.books.api.api.validators.OrderRequestValidator.class);
        resources.add(org.books.api.api.validators.RegistrationValidator.class);
    }

}
