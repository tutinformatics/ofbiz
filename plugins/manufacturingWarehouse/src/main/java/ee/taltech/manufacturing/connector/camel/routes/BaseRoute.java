package ee.taltech.manufacturing.connector.camel.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.ofbiz.service.LocalDispatcher;

public abstract class BaseRoute extends RouteBuilder {
    protected LocalDispatcher localDispatcher;


    public BaseRoute(LocalDispatcher localDispatcher) {
        this.localDispatcher = localDispatcher;
    }
}
