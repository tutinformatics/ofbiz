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
                .bean(partyService, "createAffiliate")
                .endRest();
    }
}
