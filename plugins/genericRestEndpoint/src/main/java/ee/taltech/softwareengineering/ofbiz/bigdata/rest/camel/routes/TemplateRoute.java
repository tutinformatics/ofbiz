package ee.taltech.softwareengineering.ofbiz.bigdata.rest.camel.routes;

import ee.taltech.softwareengineering.ofbiz.bigdata.rest.camel.service.TemplateService;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.service.LocalDispatcher;

public class TemplateRoute extends BaseRoute {

	private TemplateService templateService;

	// localdispatcher is fed into the service by ofbiz, it has some info the plugin can use, like delegator
	// it can also be used to call other ofbiz services by string
	public TemplateRoute(LocalDispatcher localDispatcher) throws GenericEntityException {
		super(localDispatcher);
		templateService = new TemplateService(localDispatcher.getDelegator());
	}

	@Override
	public void configure() {
		// makes all of it accessible on localhost:4567
		restConfiguration("rest-api")
				.component("restlet")
				.host("localhost")
				.port("4567")
				.bindingMode(RestBindingMode.auto);

		rest("/api/v1/entity")
				.get("/{entity}")
				.id("test-get-entity")
				.produces("application/json")
				.route()
				.bean(templateService, "getAll")
				.endRest();

		rest("/api/v1/graphql")
				.get("/{entity}")
				.id("test-graphql-endpoints")
				.produces("application/json")
				.route()
				.bean(templateService, "getGraphQLSchemas")
				.endRest();

		rest("/api/v1/entity")
				.post("/{entity}")
				.id("insert-entity")
				.produces("application/json")
				.route()
				.bean(templateService, "insert")
				.endRest();

		rest("/api/v1/service")
				.post("/{service}")
				.id("call-service")
				.produces("application/json")
				.route()
				.bean(templateService, "service")
				.endRest();
	}
}

