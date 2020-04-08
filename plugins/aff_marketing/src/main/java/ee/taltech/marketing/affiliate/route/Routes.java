package ee.taltech.marketing.affiliate.route;

import ee.taltech.marketing.affiliate.service.PartyService;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.ofbiz.service.LocalDispatcher;

public class Routes extends RouteBuilder {
    LocalDispatcher localDispatcher;
    private PartyService partyService;

    public Routes(LocalDispatcher localDispatcher) {
        this.localDispatcher = localDispatcher;
        partyService = new PartyService(localDispatcher.getDelegator());
    }

    @Override
    public void configure() throws Exception {
        restConfiguration("rest-api")
                .component("spark-rest")
                .host("localhost")
                .port(4567)
                .contextPath("/api")
                .bindingMode(RestBindingMode.json)
                .enableCORS(true)
                .corsHeaderProperty("Access-Control-Allow-Origin","*");

        rest("/product")
                .produces("application/json")
                .get()
                    .to("bean:productService?method=getProductList")
                .post()
                    .consumes("application/json")
                    .to("bean:productService?method=addProduct")
                .put("{id}")
                    .consumes("application/json")
                    .to("bean:productService?method=updateProduct(${header.id}, ${body})")
                .get("{id}")
                    .to("bean:productService?method=getProductById(${header.id})")
                .get("/type/{typeId}")
                    .to("bean:productService?method=getProductsByParentType(${header.typeId})");

        rest("/order")
                .produces("application/json")
                .get("{partyId}")
                    .to("bean:orderService?method=getOrdersByParty(${header.partyId})")
                .post("{partyId}")
                    .consumes("application/json")
                    .to("bean:orderService?method=createOrder(${header.partyId}, ${body})")
                .get("/id/{orderId}")
                    .to("bean:orderService?method=getOrderById(${header.orderId})")
                .put("/id/{orderId}")
                    .to("bean:orderService?method=updateOrder(${header.orderId}, ${body})");
        rest("/api")
                .get("/parties/find-by-id/{id}")
                .produces("application/json")
                .route()
                .bean(partyService, "getPartyById")
                .endRest();

        // affiliate part
        rest("/api")
                .post("/parties/affiliate/create")
                .produces("application/json")
                .route()
                .bean(partyService, "createAffiliateForUserLogin")
                .endRest();

        rest("/api")
                .post("/parties/createCode")
                .produces("application/json")
                .route()
                .bean(partyService, "createAffiliateCode")
                .endRest();

        rest("/api")
                .get("/parties/getCodes")
                .produces("application/json")
                .route()
                .bean(partyService, "getAffiliateCodes")
                .endRest();

        rest("/api")
                .get("/parties/unconfirmedAffiliates")
                .produces("application/json")
                .route()
                .bean(partyService, "getUnconfirmedAffiliates")
                .endRest();

        rest("/api")
                .get("/parties/affiliates")
                .produces("application/json")
                .route()
                .bean(partyService, "getAffiliates")
                .endRest();

        rest("/api")
                .put("/parties/affiliate/approve")
                .produces("application/json")
                .route()
                .bean(partyService, "approve")
                .endRest();

        rest("/api")
                .put("/parties/affiliate/disapprove")
                .produces("application/json")
                .route()
                .bean(partyService, "disapprove")
                .endRest();
    }
}
