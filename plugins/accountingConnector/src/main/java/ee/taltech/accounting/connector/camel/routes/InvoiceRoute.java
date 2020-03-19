package ee.taltech.accounting.connector.camel.routes;

import ee.taltech.accounting.connector.camel.service.InvoiceService;
import ee.taltech.accounting.connector.camel.service.PartyService;
import ee.taltech.accounting.connector.camel.service.PaymentService;
import ee.taltech.accounting.connector.camel.service.ProductService;
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

        rest("/api")
                .get("/parties")
                .enableCORS(true)
                .id("parties-get")
                .produces("application/json")
                .route()
                .bean(partyService, "getParties")
                .endRest();

        rest("/api")
                .get("/products")
                .enableCORS(true)
                .id("product-get")
                .produces("application/json")
                .route()
                .bean(productService, "getProducts")
                .endRest();

        rest("/api")
                .get("/payments")
                .enableCORS(true)
                .id("payments-get")
                .produces("application/json")
                .route()
                .bean(paymentService, "getPayments")
                .endRest();
    }
}
