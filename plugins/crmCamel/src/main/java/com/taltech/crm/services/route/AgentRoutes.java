package com.taltech.crm.services.route;

import com.taltech.crm.services.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.ofbiz.service.LocalDispatcher;

public class AgentRoutes extends RouteBuilder {
    private LocalDispatcher localDispatcher;

    private AgentService agentService;

    public AgentRoutes(LocalDispatcher localDispatcher) {
        this.localDispatcher = localDispatcher;
        agentService = new AgentService(this.localDispatcher.getDispatchContext());
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

        rest("/api").post("/agent")
                .route()
                .bean(agentService, "createAgent")
                .endRest();

        rest("/api")
                .get("/agents")
                .produces("application/json")
                .route()
                .bean(agentService, "getAgents()")
                .endRest();
    }
}
