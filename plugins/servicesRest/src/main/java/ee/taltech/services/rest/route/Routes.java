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

        rest("/contact")
                .produces("application/json")

                .get().to("bean:contactService?method=getAllContactData")

                .get("mech").to("bean:contactService?method=getContactMechs")
                .get("mech/{typeId}").to("bean:contactService?method=getContactMechsByType(${header.typeId})")
                .delete("mech").to("bean:contactService?method=deleteContactMech")

                .get("person").to("bean:contactService?method=getPersonData")
                .put("person").to("bean:contactService?method=updatePersonData")

                .get("email").to("bean:contactService?method=getPartyEmails")
                .post("email").to("bean:contactService?method=createPartyEmail")
                .put("email").to("bean:contactService?method=updatePartyEmail")

                .get("telephone").to("bean:contactService?method=getPartyTelephones")
                .post("telephone").to("bean:contactService?method=createPartyTelephone")
                .put("telephone").to("bean:contactService?method=updatePartyTelephone")

                .get("address").to("bean:contactService?method=getPostalAddresses")
                .post("address").to("bean:contactService?method=createPostalAddress")
                .put("address").to("bean:contactService?method=updatePostalAddress");
    }
}
