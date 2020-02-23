package ee.taltech.accounting.connector.camel.routes;

import ee.taltech.accounting.connector.camel.service.InvoiceService;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.ofbiz.service.LocalDispatcher;

public class InvoiceRoute extends RouteBuilder {

    private InvoiceService invoiceService;
    private LocalDispatcher localDispatcher;


    public InvoiceRoute(LocalDispatcher localDispatcher) {
        this.localDispatcher = localDispatcher;
        invoiceService = new InvoiceService(localDispatcher.getDelegator());
    }

    @Override
    public void configure() throws Exception {
        restConfiguration("rest-api")
                .component("restlet")
                .host("localhost")
                .port("4567")
                .bindingMode(RestBindingMode.auto);

        rest("/api")
                .get("/users")
                .id("api-users")
                .produces("application/json")
                .route()
                .bean(invoiceService, "getInvoices")
                .endRest();
    }
}
