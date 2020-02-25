package ee.taltech.elukaar.camel.routes;

import ee.taltech.elukaar.camel.service.ElukaarService;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.ofbiz.service.LocalDispatcher;

public class ElukaarRoute extends BaseRoute {

    private ElukaarService elukaarService;


    public ElukaarRoute(LocalDispatcher localDispatcher) {
        super(localDispatcher);
        elukaarService = new ElukaarService(localDispatcher.getDelegator());
    }

    @Override
    public void configure() {
        restConfiguration("rest-api")
                .component("restlet")
                .host("localhost")
                .port("4567")
                .bindingMode(RestBindingMode.auto);

        rest("/api")
                .get("/visits")
                .id("api-users")
                .produces("application/json")
                .route()
                .bean(elukaarService, "getVisits")
                .endRest();
    }
}