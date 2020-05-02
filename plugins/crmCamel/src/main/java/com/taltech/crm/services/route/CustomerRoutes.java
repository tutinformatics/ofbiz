package com.taltech.crm.services.route;

import com.taltech.crm.services.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.ofbiz.service.LocalDispatcher;

public class CustomerRoutes extends RouteBuilder {
    private LocalDispatcher localDispatcher;

    private CustomerService customerService;

    public CustomerRoutes(LocalDispatcher localDispatcher) {
        this.localDispatcher = localDispatcher;
        customerService = new CustomerService(this.localDispatcher.getDispatchContext());
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

        rest("/api").post("/customer")
                .route()
                .bean(customerService, "createCustomer")
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

    }
}
