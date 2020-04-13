package org.apache.ofbiz.jersey.resource;

import org.apache.ofbiz.base.conversion.ConversionException;
import org.apache.ofbiz.base.util.*;
import org.apache.ofbiz.base.lang.JSON;
import org.apache.ofbiz.entity.model.ModelEntity;
import org.apache.ofbiz.entity.*;
import org.apache.ofbiz.entity.condition.EntityCondition;
import org.apache.ofbiz.entity.condition.EntityOperator;
import org.apache.ofbiz.entity.util.ExtendedConverters;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import java.util.List;
import java.util.Map;

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

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllMarketdataEntities() throws GenericEntityException {
        Response.ResponseBuilder builder = null;
        Delegator delegator = (Delegator) servletContext.getAttribute("delegator");
        List<GenericValue> allEntries = delegator.findAll("MarketdataModel", true);
        builder = Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON).entity(allEntries);
        return builder.build();
    }

    @DELETE
    @Path("/{companyId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteEntry(@PathParam(value = "companyId") String companyId) throws GenericEntityException {
        Response.ResponseBuilder builder = null;
        Delegator delegator = (Delegator) servletContext.getAttribute("delegator");

        EntityCondition condition = EntityCondition.makeCondition("companyId", EntityOperator.EQUALS, companyId);

        Map primaryKey = UtilMisc.toMap("companyId", companyId);

        GenericValue result = delegator.findOne("MarketdataModel", primaryKey, false);
        result.remove();

        builder = Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON).entity(result);
        return builder.build();
    }

    @POST
    @Path("/{companyName}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addMarketdataEntity(@PathParam(value = "companyName") String entityName, String jsonBody)
            throws GenericEntityException, ConversionException {
        Response.ResponseBuilder builder = null;
        Delegator delegator = (Delegator) servletContext.getAttribute("delegator");
        GenericValue object = jsonToGenericConverter.convert(delegator.getDelegatorName(), entityName,
                JSON.from(jsonBody));
        ModelEntity model = delegator.getModelEntity(entityName);
        if (model.getPksSize() == 1) {
            if (object.get(model.getFirstPkFieldName()) == null) {
                object.setNextSeqId();
            }
        }
        delegator.create(object);
        builder = Response.status(Response.Status.OK);
        return builder.build();
    }


//    @POST
//    @Consumes(MediaType.APPLICATION_JSON)
//    public Response addMarketdataEntity(String jsonBody)
//            throws GenericEntityException, ConversionException {
//        String entityName = "MarketdataModel";
//        Response.ResponseBuilder builder;
//        Delegator delegator = (Delegator) servletContext.getAttribute("delegator");
//        GenericValue object = jsonToGenericConverter.convert(delegator.getDelegatorName(), entityName,
//                JSON.from(jsonBody));
//        ModelEntity model = delegator.getModelEntity(entityName);
//        if (model.getPksSize() == 1) {
//            // because can only sequence if one PK field
//            if (object.get(model.getFirstPkFieldName()) == null) {
//                object.setNextSeqId();
//            }
//        }
//
//
//        delegator.create(object); // TODO: catch exception
//        builder = Response.status(Response.Status.OK);
//        return builder.build();
//    }

}
