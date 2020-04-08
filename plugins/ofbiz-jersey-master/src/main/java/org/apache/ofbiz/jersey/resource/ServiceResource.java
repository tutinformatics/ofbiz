package org.apache.ofbiz.jersey.resource;

import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Path("/v1/services")
@Provider
//@Secured
public class ServiceResource {

	public static final String MODULE = ServiceResource.class.getName();

	@Context
	private HttpServletRequest httpRequest;

	@Context
	private ServletContext servletContext;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getServiceNames() throws IOException {
		Response.ResponseBuilder builder = null;
		LocalDispatcher dispatcher = (LocalDispatcher) servletContext.getAttribute("dispatcher");
		DispatchContext dpc = dispatcher.getDispatchContext();
		builder = Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON).entity(dpc.getAllServiceNames());
		return builder.build();
	}

	@GET
	@Path("/{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getServiceDetails(@PathParam(value = "name") String serviceName) throws GenericServiceException {
		Response.ResponseBuilder builder = null;
		LocalDispatcher dispatcher = (LocalDispatcher) servletContext.getAttribute("dispatcher");
		DispatchContext dpc = dispatcher.getDispatchContext();
		Object obj = dpc.getModelService(serviceName).getModelParamList();
		builder = Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON).entity(obj);
		return builder.build();
	}

}
