package com.taltech.crm.services.route;

import com.taltech.crm.services.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.ofbiz.service.LocalDispatcher;

public class OpportunityRoutes extends RouteBuilder {
    private LocalDispatcher localDispatcher;

    private OpportunityService opportunityService;

    public OpportunityRoutes(LocalDispatcher localDispatcher) {
        this.localDispatcher = localDispatcher;
        opportunityService = new OpportunityService(this.localDispatcher.getDispatchContext());
    }

    @Override
    public void configure() throws Exception {
        restConfiguration("rest-api")
                .component("spark-rest")
                .host("localhost")
                .port(7463)
                .bindingMode(RestBindingMode.json)
                .enableCORS(true)
                .corsHeaderProperty("Access-Control-Allow-Origin","*");;

        rest("/api").post("/opportunity")
                .route()
                .bean(opportunityService, "createOpportunity")
                .endRest();

        rest("/api")
                .get("/opportunities")
                .produces("application/json")
                .route()
                .bean(opportunityService, "getOpportunities()")
                .endRest();

        rest("/api").delete("/opportunity/{opportunityId}")
                .route()
                .bean(opportunityService, "deleteOpportunity")
                .endRest();

        rest("/api").get("/opportunity/{id}/{stage}")
                .produces("application/json")
                .route()
                .bean(opportunityService, "getOpportunitiesByStage")
                .endRest();
    }
}
