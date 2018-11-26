package lt.bta.java2.api;

import lt.bta.java2.services.impl.InvoiceServiceImpl;
import lt.bta.java2.services.impl.OrderServiceImpl;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api")
public class RestApp extends Application {

    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new HashSet<>();
        resources.add(RestApiObjectMapperContextResolver.class);

        resources.add(OrderServiceImpl.class);
        resources.add(InvoiceServiceImpl.class);

        return resources;
    }
}
