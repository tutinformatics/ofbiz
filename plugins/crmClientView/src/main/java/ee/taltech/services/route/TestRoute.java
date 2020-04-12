package ee.taltech.services.route;

import ee.taltech.services.TestService;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.ofbiz.service.LocalDispatcher;

public class TestRoute extends RouteBuilder {
    LocalDispatcher localDispatcher;
    TestService testService;

    public TestRoute(LocalDispatcher localDispatcher) {
        this.localDispatcher = localDispatcher;
        testService = new TestService(this.localDispatcher.getDispatchContext());
    }

    @Override
    public void configure() throws Exception {
        restConfiguration("rest-api")
                .component("spark-rest")
                .host("localhost")
                .port(7463)
                .bindingMode(RestBindingMode.json)
                .enableCORS(true)
                .corsHeaderProperty("Access-Control-Allow-Origin", "*");

        rest("/api").get("/test")
                .produces("application/json")
                .route()
                .bean(testService, "get")
                .endRest();

        rest("/api").post("/test")
                .route()
                .bean(testService, "create")
                .endRest();

        rest("/api").put("/test")
                .route()
                .bean(testService, "update")
                .endRest();

        rest("/api").delete("/test")
                .route()
                .bean(testService, "delete")
                .endRest();
    }
}
