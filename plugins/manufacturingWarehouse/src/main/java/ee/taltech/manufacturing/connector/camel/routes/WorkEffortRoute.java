package ee.taltech.manufacturing.connector.camel.routes;

import ee.taltech.manufacturing.connector.camel.service.WorkEffortService;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.ofbiz.service.LocalDispatcher;

public class WorkEffortRoute extends BaseRoute {

    private WorkEffortService workEffortService;


    public WorkEffortRoute(LocalDispatcher localDispatcher) {
        super(localDispatcher);
        workEffortService = new WorkEffortService(localDispatcher.getDelegator());
    }

    @Override
    public void configure() {
        restConfiguration("rest-api")
                .component("restlet")
                .host("localhost")
                .port("4567")
                .bindingMode(RestBindingMode.auto);

        rest("/api")
                .get("/workefforts")
                .id("api-users")
                .produces("application/json")
                .route()
                .bean(workEffortService, "getWorkEfforts")
                .endRest();
    }
}
