package org.apache.ofbiz.jersey.resource;

import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.util.ExtendedConverters;
import ee.ttu.ofbizpublisher.model.PublisherDTO;
import ee.ttu.ofbizpublisher.services.PublisherService;
import ee.ttu.ofbizpublisher.model.SubscriberDTO;
import ee.ttu.ofbizpublisher.services.SubscriberService;
import org.apache.ofbiz.jersey.util.JsonUtils;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.LocalDispatcher;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Path("/objectdist")
@Provider
//@Secured
public class PublisherServiceResource {

    public static final String MODULE = PublisherServiceResource.class.getName();
    public static final ExtendedConverters.ExtendedJSONToGenericValue jsonToGenericConverter = new ExtendedConverters.ExtendedJSONToGenericValue();

    @Context
    private HttpServletRequest httpRequest;

    @Context
    private ServletContext servletContext;

    private PublisherService publisherService;
    private SubscriberService subscriberService;

    @PostConstruct
    public void init() {
        LocalDispatcher dispatcher = (LocalDispatcher) servletContext.getAttribute("dispatcher");
        DispatchContext dpc = dispatcher.getDispatchContext();
        publisherService = new PublisherService(dpc);
        subscriberService = new SubscriberService(dpc);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getServiceNames() throws IOException {
        Response.ResponseBuilder builder = null;
        LocalDispatcher dispatcher = (LocalDispatcher) servletContext.getAttribute("dispatcher");
        DispatchContext dpc = dispatcher.getDispatchContext();
        builder = Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON).entity("Hello world");
        return builder.build();
    }

    @GET
    @Path("/publishers")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPublishers() throws GenericEntityException {
        List<PublisherDTO> entity = publisherService.getPublishers();
        Response.ResponseBuilder builder = Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON).entity(entity);
        return builder.build();
    }

    @POST()
    @Path("/publishers/create")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createPublisher(String jsonBody) throws Exception {
        Map<String, Object> data = JsonUtils.parseJson(jsonBody);
        publisherService.createPublisher(data);
        Response.ResponseBuilder builder = Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON);
        return builder.build();
    }

    @GET
    @Path("/subscribers")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSubscribers() throws GenericEntityException {
        List<SubscriberDTO> entity = subscriberService.getSubscribers();
        Response.ResponseBuilder builder = Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON).entity(entity);
        return builder.build();
    }

    @POST()
    @Path("/subscribers/create")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createSubscriber(String jsonBody) throws Exception {
        Map<String, Object> data = JsonUtils.parseJson(jsonBody);
        subscriberService.createSubscriber(data);
        Response.ResponseBuilder builder = Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON);
        return builder.build();
    }
}