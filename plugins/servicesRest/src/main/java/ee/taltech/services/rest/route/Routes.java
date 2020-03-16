package ee.taltech.services.rest.route;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.ofbiz.service.LocalDispatcher;

public class Routes extends RouteBuilder {
    LocalDispatcher localDispatcher;

    public Routes(LocalDispatcher localDispatcher) {
        this.localDispatcher = localDispatcher;
    }

    @Override
    public void configure() throws Exception {
        restConfiguration("rest-api")
                .component("spark-rest")
                .host("localhost")
                .port(9898)
                .contextPath("/api")
                .bindingMode(RestBindingMode.json)
                .enableCORS(true)
                .corsHeaderProperty("Access-Control-Allow-Origin","*");

        rest("/products")
                .produces("application/json")
                .get()
                    .to("bean:productService?method=getProductList")
                .post()
                    .consumes("application/json")
                    .to("bean:productService?method=addProduct")
                .get("{id}")
                    .to("bean:productService?method=getProductById(${header.id})")
                .delete("{id}")
                    .to("bean:productService?method=deleteProduct(${header.id})");
    }
}
