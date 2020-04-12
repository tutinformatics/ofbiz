package org.apache.ofbiz.jersey.resource;

import org.apache.ofbiz.base.conversion.ConversionException;
import org.apache.ofbiz.base.lang.JSON;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntity;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.model.ModelEntity;
import org.apache.ofbiz.entity.model.ModelReader;
import org.apache.ofbiz.entity.util.ExtendedConverters;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilProperties;
import org.apache.ofbiz.service.ServiceUtil;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

@Path("/marketdata")
@Provider

public class MarketdataResources {

    public static final String MODULE = GenericEntityResource.class.getName();
    public static final ExtendedConverters.ExtendedJSONToGenericValue jsonToGenericConverter
            = new ExtendedConverters.ExtendedJSONToGenericValue();


    @Context
    private HttpServletRequest httpRequest;

    @Context
    private ServletContext servletContext;

//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response getAllMarketdataEntities() throws GenericEntityException {
//        Response.ResponseBuilder builder = null;
//        Delegator delegator = (Delegator) servletContext.getAttribute("delegator");
//        ModelReader reader = delegator.getModelReader();
//        TreeSet<String> entities = new TreeSet<String>(reader.getEntityNames());
//        builder = Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON).entity(entities);
//        return builder.build();
//    }

//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response getMarketdataList(@QueryParam(value = "projectId") String companyId) {
//        LocalDispatcher dispatcher = (LocalDispatcher) servletContext.getAttribute("dispatcher");
//        Map<String, Object> result;
//
//        try {
//            result = dispatcher.runSync("getProjectTaskList",
//                    Collections.singletonMap("projectId", companyId));
//        } catch (GenericServiceException e) {
//            Debug.logError(e, "Exception thrown while running getProjectTaskList service: ", MODULE);
//            String errMsg = UtilProperties.getMessage("JerseyUiLabels", "api.error.get_task_list",
//                    httpRequest.getLocale());
//            throw new RuntimeException(errMsg);
//        }
//
//        if (ServiceUtil.isError(result)) {
//            ServiceUtil.returnError(ServiceUtil.getErrorMessage(result));
//        }
//
//        return Response
//                .status(Response.Status.OK)
//                .type(MediaType.APPLICATION_JSON)
//                .entity(result)
//                .build();
//    }

    // This returns all fields of MarketdataModel entity.
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllMarketdataEntities() throws GenericEntityException {
        Response.ResponseBuilder builder = null;
        Delegator delegator = (Delegator) servletContext.getAttribute("delegator");
        List<GenericValue> allEntries = delegator.findAll("MarketdataModel", true);
        //ModelEntity reader = delegator.getModelEntity("MarketdataModel");
        //List<String> fieldNames = reader.getAllFieldNames();
        builder = Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON).entity(allEntries);
        return builder.build();
    }


    @POST
    @Path("/{name}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addMarketdataEntity(@PathParam(value = "name") String entityName, String jsonBody)
            throws GenericEntityException, ConversionException {
        Response.ResponseBuilder builder;
        Delegator delegator = (Delegator) servletContext.getAttribute("delegator");
        GenericValue object = jsonToGenericConverter.convert(delegator.getDelegatorName(), entityName,
                JSON.from(jsonBody));
        ModelEntity model = delegator.getModelEntity(entityName);
        if (model.getPksSize() == 1) {
            // because can only sequence if one PK field
            if (object.get(model.getFirstPkFieldName()) == null) {
                object.setNextSeqId();
            }
        }
        delegator.create(object); // TODO: catch exception
        builder = Response.status(Response.Status.OK);
        return builder.build();
    }

}
