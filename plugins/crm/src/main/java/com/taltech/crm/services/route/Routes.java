package com.taltech.crm.services.route;

import com.taltech.crm.services.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.ofbiz.service.LocalDispatcher;

public class Routes extends RouteBuilder {
    private LocalDispatcher localDispatcher;

    private ContactsService contactsService;
    private AgentService agentService;
    private OpportunityService opportunityService;
    private PipelineService pipelineService;
    private CustomerService customerService;

    public Routes(LocalDispatcher localDispatcher) {
        this.localDispatcher = localDispatcher;
        agentService = new AgentService(this.localDispatcher.getDispatchContext());
        opportunityService = new OpportunityService(this.localDispatcher.getDispatchContext());
        pipelineService = new PipelineService(this.localDispatcher.getDispatchContext());
        contactsService = new ContactsService(this.localDispatcher.getDispatchContext());
        customerService = new CustomerService(this.localDispatcher.getDispatchContext());
    }

    @Override
    public void configure() throws Exception {
        restConfiguration("rest-api")
                .component("spark-rest")
                .host("localhost")
                .port(7463)
                .bindingMode(RestBindingMode.json);

        rest("/api")
                .get("/contacts")
                .produces("application/json")
                .route()
                .bean(contactsService, "getContactList()")
                .endRest();

        rest("/api")
                .get("/pipelines")
                .produces("application/json")
                .route()
                .bean(pipelineService, "getPipelines()")
                .endRest();

        rest("/api")
                .get("/opportunities")
                .produces("application/json")
                .route()
                .bean(opportunityService, "getOpportunities()")
                .endRest();
        rest("/api")
                .get("/agents")
                .produces("application/json")
                .route()
                .bean(agentService, "getAgents()")
                .endRest();
        rest("/api")
                .get("/customers")
                .produces("application/json")
                .route()
                .bean(customerService, "getCustomers()")
                .endRest();
    }
}
