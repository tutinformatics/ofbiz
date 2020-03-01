package ee.taltech.accounting.connector.camel.routes;

import ee.taltech.accounting.connector.camel.service.TemplateService;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.ofbiz.service.LocalDispatcher;

public class TemplateRoute extends BaseRoute {

    private TemplateService templateService;


    public TemplateRoute(LocalDispatcher localDispatcher) {
        super(localDispatcher);
        templateService = new TemplateService(localDispatcher.getDelegator());
    }

    @Override
    public void configure() {
        restConfiguration("rest-api")
                .component("restlet")
                .host("localhost")
                .port("4567")
                .bindingMode(RestBindingMode.auto);

        rest("/api")
                .get("/invoices")
                .id("invoice-get-all")
                .produces("application/json")
                .route()
                .bean(templateService, "getInvoices")
                .endRest();

        rest("/api")
                .post("/invoice")
//                .type(InvoicePojo.class)
                .id("invoice-post")
                .produces("application/json")
                .route()
                .bean(templateService, "createInvoice")
                .endRest();
    }
}
