package ee.taltech.services.route;

import ee.taltech.services.InvoiceService;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.ofbiz.service.LocalDispatcher;

public class InvoiceRoutes extends RouteBuilder {
    LocalDispatcher localDispatcher;

    private InvoiceService invoiceService;

    public InvoiceRoutes(LocalDispatcher localDispatcher) {
        this.localDispatcher = localDispatcher;
        invoiceService = new InvoiceService(this.localDispatcher.getDispatchContext());
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

        rest("/api")
                .get("/invoice")
                .produces("application/json")
                .route()
                .bean(invoiceService, "getContactList()")
                .endRest();

        rest("/api")
                .delete("/invoice/{id}")
                .produces("application/json")
                .route()
                .bean(invoiceService, "deleteInvoice")
                .endRest();
    }
}
