package ee.taltech.accounting.connector.camel.routes;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;
import aQute.bnd.annotation.component.Reference;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.sparkrest.SparkComponent;
import org.apache.camel.core.osgi.OsgiDefaultCamelContext;
import org.apache.camel.impl.DefaultCamelContextNameStrategy;
import org.apache.camel.model.rest.RestBindingMode;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configures and holds the Camel context and routes.
 */
@Component
public class Routes {

    private static final Logger LOG = LoggerFactory.getLogger(Routes.class);

    private ServiceRegistration<CamelContext> serviceRegistration;
    private CamelContext camelContext;

    private Users users;

    @Reference
    public void setUsers(final Users users) {
        this.users = users;
    }

    @Activate
    public void activate(BundleContext ctx) throws Exception {

        LOG.info("Routes activating.");

        camelContext = new OsgiDefaultCamelContext(ctx);

        camelContext.setNameStrategy(new DefaultCamelContextNameStrategy("rest-api"));

        camelContext.addComponent("spark-rest", new SparkComponent());
        camelContext.addRoutes(createRouteBuilder());

        camelContext.start();

        serviceRegistration = ctx.registerService(CamelContext.class, camelContext, null);

        LOG.info("Routes activated.");
    }

    @Deactivate
    public final void deactivate() throws Exception {
        LOG.info("Routes dactivating.");
        camelContext.stop();
        serviceRegistration.unregister();

        camelContext = null;
        serviceRegistration = null;
        LOG.info("Routes dactivated.");
    }

    protected final RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {

                restConfiguration("rest-api")
                        .component("spark-rest")
                        .host("localhost")
                        .port(9000)
                        .bindingMode(RestBindingMode.json);

                rest("/api")
                        .get("/users")
                        .id("api-users")
                        .produces("application/json")
                        .route()
                        .bean(users, "listAllUsers")
                        .endRest()

                        .get("/users/{name}")
                        .id("api-user-by-name")
                        .produces("application/json")
                        .route()
                        .bean(users, "getUser(${header.name})")
                        .endRest()

                        .post("/users")
                        .id("api-new-user")
                        .consumes("application/json")
                        .type(User.class)
                        .route()
                        .bean(users, "addUser")
                        .endRest()

                        .delete("/users/{name}")
                        .id("delete-user-by-name")
                        .route()
                        .bean(users, "deleteUser(${header.name})")
                        .endRest();
            }
        };
    }
}