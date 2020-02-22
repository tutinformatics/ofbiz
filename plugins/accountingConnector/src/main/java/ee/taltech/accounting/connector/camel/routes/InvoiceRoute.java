package ee.taltech.accounting.connector.camel.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;

public class InvoiceRoute extends RouteBuilder {

    private static Users users = new UsersImpl();

    @Override
    public void configure() throws Exception {
        restConfiguration("rest-api")
                .component("restlet")
                .host("localhost")
                .port("4567")
                .bindingMode(RestBindingMode.auto);

        rest("/api")
                .get("/users")
                .id("api-users")
                .produces("application/json")
                .route()
                .bean(users, "listAllUsers")
                .endRest();
    }
}
