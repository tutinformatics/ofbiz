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

        rest("/api").post("/agent")
                .route()
                .bean(agentService, "createAgent")
                .endRest();

        rest("/api").post("/customer")
                .route()
                .bean(customerService, "createCustomer")
                .endRest();

        rest("/api").post("/opportunity")
                .route()
                .bean(opportunityService, "createOpportunity")
                .endRest();

        rest("/api").post("/pipeline")
                .route()
                .bean(pipelineService, "createPipeline")
                .endRest();


        rest("/api").put("/contact")
                .route()
                .bean(contactsService, "getContactByFirstName")
                .endRest();

        rest("/api").get("/contact/firstName/{name}")
                .produces("application/json")
                .route()
                .bean(contactsService, "getContactByFirstName")
                .endRest();

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

        rest("/api").delete("/customer/{customerId}")
                .route()
                .bean(customerService, "deleteCustomer")
                .endRest();

        rest("/api").delete("/opportunity/{opportunityId}")
                .route()
                .bean(opportunityService, "deleteOpportunity")
                .endRest();
    }
}
