package ee.taltech.services.route;

import ee.taltech.services.ProductService;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.ofbiz.service.LocalDispatcher;

public class ProductRoutes extends RouteBuilder {
    LocalDispatcher localDispatcher;
    ProductService productService;

    public ProductRoutes(LocalDispatcher localDispatcher) {
        this.localDispatcher = localDispatcher;
        productService = new ProductService(this.localDispatcher.getDispatchContext());
    }

    @Override
    public void configure() throws Exception {
        restConfiguration("rest-api")
                .component("spark-rest")
                .host("localhost")
                .port(7463)
                .bindingMode(RestBindingMode.json)
                .enableCORS(true)
                .corsHeaderProperty("Access-Control-Allow-Origin", "*");

        rest("/api").get("/product")
                .produces("application/json")
                .route()
                .bean(productService, "get")
                .endRest();

        rest("/api").get("/product/productName/{name}")
                .produces("application/json")
                .route()
                .bean(productService, "getByProductName")
                .endRest();

//        rest("/api").post("/product")
//                .route()
//                .bean(productService, "create")
//                .endRest();
//
//        rest("/api").put("/product")
//                .route()
//                .bean(productService, "update")
//                .endRest();
//
//        rest("/api").delete("/product")
//                .route()
//                .bean(productService, "delete")
//                .endRest();
    }
}
