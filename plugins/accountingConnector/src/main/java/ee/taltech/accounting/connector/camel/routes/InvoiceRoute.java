package ee.taltech.accounting.connector.camel.routes;

import ee.taltech.accounting.connector.camel.service.InvoiceService;
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
                .host("127.0.0.1")
                .port("4567")
                .setEnableCORS(true);

        rest("/api")
                .get("/invoices")
                .enableCORS(true)
                .id("api-users")
                .produces("application/json")
                .route()
                .bean(invoiceService, "getInvoices")
                .endRest();

        rest("/api")
                .post("/invoice")
                .enableCORS(true)
                .id("invoice-post")
                .produces("application/json")
                .route()
                .bean(invoiceService, "createInvoice")
                .endRest();
    }
}
