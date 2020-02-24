package ee.taltech.accounting.connector.camel.routes;

import ee.taltech.accounting.connector.camel.service.InvoiceService;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.ofbiz.service.LocalDispatcher;

public class InvoiceRoute extends BaseRoute {

    private InvoiceService invoiceService;


    public InvoiceRoute(LocalDispatcher localDispatcher) {
        super(localDispatcher);
        invoiceService = new InvoiceService(localDispatcher.getDelegator());
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
                .id("api-users")
                .produces("application/json")
                .route()
                .bean(invoiceService, "getInvoices")
                .endRest();
    }
}
