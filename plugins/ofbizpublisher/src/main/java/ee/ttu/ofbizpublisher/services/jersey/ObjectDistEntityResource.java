package ee.ttu.ofbizpublisher.services.jersey;

import org.apache.ofbiz.base.conversion.ConversionException;
import org.apache.ofbiz.base.lang.JSON;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.model.ModelEntity;
import org.apache.ofbiz.entity.model.ModelReader;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.entity.util.ExtendedConverters;
import org.apache.ofbiz.jersey.util.QueryParamStringConverter;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Path("/generic/v1/entities")
@Provider
//@Secured
public class ObjectDistEntityResource {

    public static final String MODULE = ObjectDistEntityResource.class.getName();
    public static final ExtendedConverters.ExtendedJSONToGenericValue jsonToGenericConverter = new ExtendedConverters.ExtendedJSONToGenericValue();
    public static final ExtendedConverters.ExtendedGenericValueToJSON genericToJsonConverter = new ExtendedConverters.ExtendedGenericValueToJSON();


    @Context
    private HttpServletRequest httpRequest;

    @Context
    private ServletContext servletContext;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEntityNames() throws GenericEntityException {
        ResponseBuilder builder = null;
        Delegator delegator = (Delegator) servletContext.getAttribute("delegator");
        ModelReader reader = delegator.getModelReader();
        TreeSet<String> entities = new TreeSet<String>(reader.getEntityNames());
        builder = Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON).entity(entities);
        return builder.build();
    }

    @GET
    @Path("/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEntity(@PathParam(value = "name") String entityName, @Context UriInfo allUri) throws GenericEntityException, ConversionException {
        ResponseBuilder builder = null;
        Map<String, List<String>> mpAllQueParams = allUri.getQueryParameters().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        List<String> depthStringList = mpAllQueParams.remove("_depth");
        Integer depth = 0;
        if (depthStringList != null && depthStringList.size() > 0) {
            depth = Integer.parseInt(depthStringList.get(0));
        }
        Delegator delegator = (Delegator) servletContext.getAttribute("delegator");
        ModelEntity model = delegator.getModelReader().getModelEntity(entityName);
        Map<String, Object> secondary = mpAllQueParams.entrySet().stream()
                .map(x -> new AbstractMap.SimpleEntry<>(x.getKey(), QueryParamStringConverter.convert(x.getValue().get(0), model.getField(x.getKey()).getType())))
                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
        List<GenericValue> allEntities = EntityQuery.use(delegator).from(entityName).where(secondary).queryList();
        builder = Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON).entity(genericToJsonConverter.convertListWithChildren(allEntities, depth).toString());
        return builder.build();
    }

    @POST
    @Path("/{name}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addEntity(@PathParam(value = "name") String entityName, String jsonBody) throws GenericEntityException, ConversionException {
        Response.ResponseBuilder builder;
        Delegator delegator = (Delegator) servletContext.getAttribute("delegator");
        GenericValue object = jsonToGenericConverter.convert(delegator.getDelegatorName(), entityName, JSON.from(jsonBody));
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

    @PUT
    @Path("/{name}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateEntity(@PathParam(value = "name") String entityName, String jsonBody) throws GenericEntityException, ConversionException {
        Response.ResponseBuilder builder;
        Delegator delegator = (Delegator) servletContext.getAttribute("delegator");
        GenericValue object = jsonToGenericConverter.convert(delegator.getDelegatorName(), entityName, JSON.from(jsonBody));
        GenericValue check = delegator.findOne(entityName, object.getPrimaryKey(), false);
        // if there indeed is an entity in db with such PKs
        if (check != null) {
            try {
                delegator.store(object);
            } catch (GenericEntityException e) {
                e.printStackTrace();
                // TODO: some error to json
            }
            builder = Response.status(Response.Status.OK);
        } else {
            builder = Response.status(Response.Status.BAD_REQUEST);
        }
        return builder.build();
    }
}