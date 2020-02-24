package ee.taltech.services.rest.route;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.ofbiz.service.LocalDispatcher;

public class DemoRoute extends RouteBuilder {
    LocalDispatcher localDispatcher;

    public DemoRoute(LocalDispatcher localDispatcher) {
        this.localDispatcher = localDispatcher;
    }

    @Override
    public void configure() throws Exception {
        restConfiguration("rest-api")
                .component("spark-rest")
                .host("localhost")
                .port(9898)
                .bindingMode(RestBindingMode.json);

        rest("/say")
                .get("/hello").to("direct:hello")
                .get("/bye").consumes("application/json").to("direct:bye")
                .post("/bye").to("mock:update");

        from("direct:hello")
                .transform().constant("Hello World");
        from("direct:bye")
                .transform().constant("Bye World");
    }
}
