package com.taltech.crm.services.route;

import com.taltech.crm.services.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.ofbiz.service.LocalDispatcher;

public class ContactRoutes extends RouteBuilder {
    private LocalDispatcher localDispatcher;

    private ContactsService contactsService;

    public ContactRoutes(LocalDispatcher localDispatcher) {
        this.localDispatcher = localDispatcher;
        contactsService = new ContactsService(this.localDispatcher.getDispatchContext());
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
    }
}
