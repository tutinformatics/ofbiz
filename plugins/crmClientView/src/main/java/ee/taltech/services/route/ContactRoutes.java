package ee.taltech.services.route;

import ee.taltech.services.ContactsListService;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.ofbiz.service.LocalDispatcher;

public class ContactRoutes extends RouteBuilder {
    LocalDispatcher localDispatcher;

    private ContactsListService contactsListService;

    public ContactRoutes(LocalDispatcher localDispatcher) {
        this.localDispatcher = localDispatcher;
        contactsListService = new ContactsListService(this.localDispatcher.getDispatchContext());
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

            rest("/api").post("/contact")
                .route()
                .bean(contactsListService, "createContact")
                .endRest();

            rest("/api").get("/contact")
                .produces("application/json")
                .route()
                .bean(contactsListService, "getContactList")
                .endRest();
        rest("/api")
                .get("/contact/contactname/{name}")
                .produces("application/json")
                .route()
                .bean(contactsListService, "getByName")
                .endRest();
        rest("/api")
                .get("/contact/{id}")
                .produces("application/json")
                .route()
                .bean(contactsListService, "getById")
                .endRest();


}

}
