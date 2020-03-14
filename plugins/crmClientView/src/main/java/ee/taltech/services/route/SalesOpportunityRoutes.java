package main.java.ee.taltech.services.route;

import main.java.ee.taltech.services.ContactsListService;
import main.java.ee.taltech.services.SalesOpportunityService;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.ofbiz.service.LocalDispatcher;

public class SalesOpportunityRoutes extends RouteBuilder {
    LocalDispatcher localDispatcher;

    private ContactsListService contactsListService;
    private SalesOpportunityService salesOpportunityService;

    public SalesOpportunityRoutes(LocalDispatcher localDispatcher) {
        this.localDispatcher = localDispatcher;
        salesOpportunityService = new SalesOpportunityService(this.localDispatcher.getDispatchContext());
    }

    @Override
    public void configure() throws Exception {
        restConfiguration("rest-api")
                .component("spark-rest")
                .host("localhost")
                .port(7463)
                .bindingMode(RestBindingMode.json);

        rest("/api")
                .get("/salesopportunity")
                .produces("application/json")
                .route()
                .bean(salesOpportunityService, "getSalesOpportunityList()")
                .endRest();
    }
}
