package org.apache.ofbiz.jersey.resource;

import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.util.ExtendedConverters;
import org.apache.ofbiz.jersey.resource.ofbizpublisher.ObfizPublisherDTO;
import org.apache.ofbiz.jersey.resource.ofbizpublisher.PublisherService;
import org.apache.ofbiz.jersey.resource.ofbizsubscriber.ObfizSubscriberDTO;
import org.apache.ofbiz.jersey.resource.ofbizsubscriber.SubscriberService;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.LocalDispatcher;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.List;

@Path("/objectdist/v1/services")
@Provider
//@Secured
public class ObjectDistService {

    public static final String MODULE = ObjectDistService.class.getName();
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
        List<ObfizPublisherDTO> entity = publisherService.getPublishers();
        Response.ResponseBuilder builder = Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON).entity(entity);
        return builder.build();
    }

    @GET
    @Path("/subscribers")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSubscribers() throws GenericEntityException {
        List<ObfizSubscriberDTO> entity = subscriberService.getSubscribers();
        Response.ResponseBuilder builder = Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON).entity(entity);
        return builder.build();
    }
}