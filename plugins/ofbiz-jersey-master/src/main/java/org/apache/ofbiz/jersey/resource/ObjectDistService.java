package org.apache.ofbiz.jersey.resource;

import org.apache.ofbiz.entity.util.ExtendedConverters;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.LocalDispatcher;

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

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getServiceNames() throws IOException {
        Response.ResponseBuilder builder = null;
        LocalDispatcher dispatcher = (LocalDispatcher) servletContext.getAttribute("dispatcher");
        DispatchContext dpc = dispatcher.getDispatchContext();
        builder = Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON).entity("Hello world");
        return builder.build();
    }

//    @GET
//    @Path("/{name}")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response getServiceDetails(@PathParam(value = "name") String serviceName) throws GenericServiceException {
//        Response.ResponseBuilder builder = null;
//        LocalDispatcher dispatcher = (LocalDispatcher) servletContext.getAttribute("dispatcher");
//        DispatchContext dpc = dispatcher.getDispatchContext();
//        Object obj = dpc.getModelService(serviceName).getModelParamList();
//        builder = Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON).entity(obj);
//        return builder.build();
//    }
//
//    @POST
//    @Path("/{name}")
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response callService(@PathParam(value = "name") String serviceName, String jsonBody) throws GenericServiceException, IOException {
//        Response.ResponseBuilder builder = null;
//        LocalDispatcher dispatcher = (LocalDispatcher) servletContext.getAttribute("dispatcher");
//        Delegator delegator = (Delegator) servletContext.getAttribute("delegator");
//
//        JSON body = JSON.from(jsonBody);
//
//        Map<String, Object> fieldMap;
//
//        fieldMap = UtilGenerics.<Map<String, Object>>cast(body.toObject(Map.class));
//
//        for (String key : fieldMap.keySet()) {
//            Object obj = fieldMap.get(key);
//            try {
//                Map<String, Object> test = UtilGenerics.<Map<String, Object>>cast(JSON.from(obj).toObject(Map.class));
//                if (test.containsKey("_ENTITY_NAME_")) {
//                    fieldMap.put(key, jsonToGenericConverter.convert(delegator.getDelegatorName(), JSON.from(obj)));
//                } else {
//                    fieldMap.put(key, test);
//                }
//            } catch (IOException | ConversionException ignored) {
//            }
//        }
//        try {
//            Map<String, ?> entity = dispatcher.runSync(serviceName, fieldMap);
//            builder = Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON).entity(entity);
//        } catch (GenericServiceException e)  {
//            e.printStackTrace();
//            Map<String, Object> errors = new HashMap<>();
//            errors.put("Error", e.getMessage());
//            builder = Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON).entity(errors);
//        }
//        return builder.build();
//    }

}