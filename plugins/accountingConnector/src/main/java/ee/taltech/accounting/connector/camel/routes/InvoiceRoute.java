package ee.taltech.accounting.connector.camel.routes;

import ee.taltech.accounting.connector.camel.service.InvoiceService;
import ee.taltech.accounting.connector.camel.service.PartyService;
import ee.taltech.accounting.connector.camel.service.PaymentService;
import ee.taltech.accounting.connector.camel.service.ProductService;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.ofbiz.service.LocalDispatcher;

public class InvoiceRoute extends BaseRoute {

    private InvoiceService invoiceService;
    private PartyService partyService;
    private ProductService productService;
    private PaymentService paymentService;


    public InvoiceRoute(LocalDispatcher localDispatcher) {
        super(localDispatcher);
        invoiceService = new InvoiceService(localDispatcher.getDelegator());
        partyService = new PartyService(localDispatcher.getDelegator());
        productService = new ProductService(localDispatcher.getDelegator());
        paymentService = new PaymentService(localDispatcher.getDelegator());
    }

    @Override
    public void configure() {
        restConfiguration("rest-api")
                .component("restlet")
                .host("127.0.0.1")
                .port("4567")
                .contextPath("/api")
                .bindingMode(RestBindingMode.auto);

        rest("/invoices")
                .produces("application/json")

                .get()
                    .id("api-invoices-get-all")
                    .route()
                    .bean(invoiceService, "getInvoices")
                    .endRest()

                .get("{id}")
                    .id("api-invoices-get-single")
                    .route()
                    .bean(invoiceService, "getInvoices(${header.id})") // TODO: create service @Kapa
                    .endRest()

                .post() // TODO: Test it, most likely  broken service etc. @Kapa
                    .id("api-invoices-post")
                    .consumes("application/json")
                    .route()
                    .bean(invoiceService, "createInvoice")
                    .endRest();

        rest("/parties")
                .produces("application/json")
                .get()
                .id("parties-get")
                .route()
                .bean(partyService, "getParties")
                .endRest();

        rest("/products")
                .produces("application/json")
                .get()
                .id("product-get")
                .route()
                .bean(productService, "getProducts")
                .endRest();

        rest("/payments")
                .produces("application/json")
                .get()
                .id("payments-get")
                .route()
                .bean(paymentService, "getPayments")
                .endRest();
    }
}
