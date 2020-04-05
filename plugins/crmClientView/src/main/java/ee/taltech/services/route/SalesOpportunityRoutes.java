package ee.taltech.services.route;

import ee.taltech.services.ContactsListService;
import ee.taltech.services.SalesOpportunityService;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.ofbiz.service.LocalDispatcher;

public class SalesOpportunityRoutes extends RouteBuilder {
    LocalDispatcher localDispatcher;


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
                .bindingMode(RestBindingMode.json)
                .enableCORS(true)
                .corsHeaderProperty("Access-Control-Allow-Origin","*");

        rest("/api")
                .get("/salesopportunity")
                .produces("application/json")
                .route()
                .bean(salesOpportunityService, "getSalesOpportunityList")
                .endRest();
        rest("/api")
                .post("/salesopportunity")
                .produces("application/json")
                .route()
                .bean(salesOpportunityService, "createSaleOpportunity")
                .endRest();
        rest("/api")
                .delete("/salesopportunity/{id}")
                .route()
                .bean(salesOpportunityService, "deleteSaleOpportunity")
                .endRest();
        rest("/api")
                .put("/salesopportunity/update")
                .route()
                .bean(salesOpportunityService, "updateSaleOpportunity")
                .endRest();
    }

}
