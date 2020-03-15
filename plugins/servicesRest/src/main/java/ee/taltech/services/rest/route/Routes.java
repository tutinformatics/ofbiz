package ee.taltech.services.rest.route;

import ee.taltech.services.rest.service.ProductService;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.ofbiz.service.LocalDispatcher;

public class Routes extends RouteBuilder {
    LocalDispatcher localDispatcher;

    private ProductService productService;

    public Routes(LocalDispatcher localDispatcher) {
        this.localDispatcher = localDispatcher;
        productService = new ProductService(this.localDispatcher.getDispatchContext());
    }

    @Override
    public void configure() throws Exception {
        restConfiguration("rest-api")
                .component("spark-rest")
                .host("localhost")
                .port(9898)
                .bindingMode(RestBindingMode.json)
                .enableCORS(true)
                .corsHeaderProperty("Access-Control-Allow-Origin","*");

        rest("/api/products")
                .get("/list")
                .produces("application/json")
                .route()
                .bean(productService, "getProductList")
                .endRest();

        rest("/api/products")
                .post("/add")
                .consumes("application/json")
                .produces("application/json")
                .route()
                .bean(productService, "addProduct")
                .endRest();

        rest("/api/products")
                .get("/id/{productId}")
                .produces("application/json")
                .route()
                .bean(productService, "getProductById(${header.productId})")
                .endRest();

        rest("/api/products")
                .delete("/id/{productId}")
                .route()
                .bean(productService, "deleteProduct(${header.productId})")
                .endRest();
    }
}
