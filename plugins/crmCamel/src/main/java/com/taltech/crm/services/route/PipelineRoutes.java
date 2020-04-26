package com.taltech.crm.services.route;

import com.taltech.crm.services.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.ofbiz.service.LocalDispatcher;

public class PipelineRoutes extends RouteBuilder {
    private LocalDispatcher localDispatcher;

    private PipelineService pipelineService;

    public PipelineRoutes(LocalDispatcher localDispatcher) {
        this.localDispatcher = localDispatcher;
        pipelineService = new PipelineService(this.localDispatcher.getDispatchContext());
    }

    @Override
    public void configure() throws Exception {
        restConfiguration("rest-api")
                .component("spark-rest")
                .host("localhost")
                .port(7463)
                .bindingMode(RestBindingMode.json)
                .enableCORS(true)
                .corsHeaderProperty("Access-Control-Allow-Origin","*");;

        rest("/api").post("/pipeline")
                .route()
                .bean(pipelineService, "createPipeline")
                .endRest();

        rest("/api")
                .get("/pipelines")
                .produces("application/json")
                .route()
                .bean(pipelineService, "getPipelines()")
                .endRest();

    }
}
