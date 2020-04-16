package ee.taltech.accounting.connector.camel.routes;

import ee.taltech.accounting.connector.camel.service.*;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.ofbiz.service.LocalDispatcher;

public class GenericRoute extends BaseRoute {

    private GenericService genericService;


    public GenericRoute(LocalDispatcher localDispatcher) {
        super(localDispatcher);
        genericService = new GenericService(localDispatcher.getDelegator());
    }

    @Override
    public void configure() {
        restConfiguration("rest-api")
                .component("restlet")
                .host("127.0.0.1")
                .port("4567")
                .contextPath("/api/v2")
                .bindingMode(RestBindingMode.auto);

        rest("/{table}")
                .produces("application/json")

                .get()
                    .id("api-get-all")
                    .route()
                    .bean(genericService, "getAll(${header.table})")
                    .endRest()

                .get("{id}?idColumn={idColumn}") // TODO: Query pram not recognised, otherwise works
                    .id("api-get-single")
                    .route()
                    .bean(genericService, "getSingle(${header.table}, ${header.id}, ${header.idColumn})")
                    .endRest()

                .post() // TODO: Broken, Big data is working on it?
                    .id("api-post")
                    .consumes("application/json")
                    .route()
                    .bean(genericService, "create(${header.table})")
                    .endRest();

 /*       rest("/parties")
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
                .endRest();*/
    }
}
