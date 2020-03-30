package ee.taltech.marketing.affiliate.connector.camel.routes;

import ee.taltech.marketing.affiliate.connector.camel.service.PartyService;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.ofbiz.service.LocalDispatcher;

public class PartyRoute extends BaseRoute {

    private PartyService partyService;

    public PartyRoute(LocalDispatcher localDispatcher) {
        super(localDispatcher);
        partyService = new PartyService(localDispatcher.getDelegator());
    }

    @Override
    public void configure() {
        restConfiguration("rest-api")
                .component("restlet")
                .host("localhost")
                .port("4567")
                .bindingMode(RestBindingMode.auto);

        rest("/api")
                .get("/parties/find-by-id/{id}")
                .produces("application/json")
                .route()
                .bean(partyService, "getPartyById")
                .endRest();

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
