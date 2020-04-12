package ee.taltech.marketing.affiliate.route;

import ee.taltech.marketing.affiliate.service.PartyService;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.ofbiz.service.LocalDispatcher;

public class Routes extends RouteBuilder {
    LocalDispatcher localDispatcher;
//    private PartyService partyService;

    public Routes(LocalDispatcher localDispatcher) {
        this.localDispatcher = localDispatcher;
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

        // affiliate part

        rest("/parties")
                .produces("application/json")
                .get("/affiliates")
                    .to("bean:partyService?method=getAffiliates")
                .post("/getCodes")
                    .consumes("application/json")
                    .to("bean:partyService?method=getAffiliateCodes(${body})")
                .get("/unconfirmedAffiliates")
                    .to("bean:partyService?method=getUnconfirmedAffiliates")
                .put("/affiliate/approve")
                    .consumes("application/json")
                    .to("bean:partyService?method=approve(${body})")
                .put("/affiliate/disapprove")
                    .consumes("application/json")
                    .to("bean:partyService?method=disapprove(${body})")
                .post("/affiliate/create")
                    .consumes("application/json")
                    .to("bean:partyService?method=createAffiliateForUserLogin(${body})")
                .post("/createCode")
                    .consumes("application/json")
                    .to("bean:partyService?method=createAffiliateCode(${body})");
    }
}
