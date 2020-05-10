package org.apache.ofbiz.jersey.resource.vsbridge;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.taltech.servicesmgt.VsBridgeServices;
import org.apache.ofbiz.base.lang.JSON;
import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.base.util.collections.PagedList;
import org.apache.ofbiz.entity.*;
import org.apache.ofbiz.jersey.pojo.AuthenticationOutput;
import org.apache.ofbiz.jersey.resource.AuthServiceResource;
import org.apache.ofbiz.jersey.response.Error;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Provider
@Path("/vsbridge/V1/")
public class StaticResource {

    public static final String module = StaticResource.class.getName();

    private static final ObjectMapper mapper = new ObjectMapper();

    @Context
    private HttpServletRequest httpRequest;

    @Context
    private ServletContext servletContext;

    @Context
    private AuthServiceResource authServiceResource;

    /**
     * Renames json key names and returns a slightly different response than existing login endpoint.
     * @param jsonBody containing "username" and "password"
     * @return json with response "code" and "token"
     */
    @POST
    @Path("{a:user/login|auth/admin}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getToken(String jsonBody) {
        Response.ResponseBuilder builder = null;
        try {
            Map<Object, Object> map = mapper.readValue(jsonBody, Map.class);
            Response r = authServiceResource.loginUser(JSON.from(UtilMisc.toMap(
                    "userLoginId", map.get("username"),
                    "currentPassword", map.get("password")
            )).toString());

            if (r.getStatus() != 200) {
                return r;
            }
            builder = Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON_TYPE)
                    .entity(UtilMisc.toMap(
                            "code", 200,
                            "token", ((AuthenticationOutput) r.getEntity()).getToken()
                    ));
        } catch (IOException e) {
            Debug.logError(e.getMessage(), module);
            builder = Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON_TYPE)
                    .entity(new Error(400, "Bad request!", "Can't read json!"));
        }
        return builder.build();
    }

    @GET
    @Path("{name}/index")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPagedEntity(@PathParam(value = "name") String entityName,
            @QueryParam(value = "apiKey") String apiKey,
            @DefaultValue("5") @QueryParam(value = "pageSize") Integer pageSize,
            @DefaultValue("0") @QueryParam(value = "page") Integer page)
    {
        // TODO: Care about apiKey.

        Delegator delegator = (Delegator) servletContext.getAttribute("delegator");

        PagedList<GenericValue> resultPage = null;
        List<Map<String, Object>> result = null;

        switch (entityName) {
            case "attributes":
                resultPage = VsBridgeServices.getPagedList(delegator, "ProductFeatureCategory", page, pageSize);
                result = VsBridgeServices.convertFeaturesToVsAttributes(resultPage.getData());
                break;
            case "categories":
                resultPage = VsBridgeServices.getPagedList(delegator, "ProductCategory", page, pageSize);
                result = VsBridgeServices.convertCategoriesToVsCategories(resultPage.getData());
                break;
            case "products":
                resultPage = VsBridgeServices.getPagedList(delegator, "Product", page, pageSize);
                result = VsBridgeServices.convertProductsToVsProducts(resultPage.getData());
                break;
        }

        Response.ResponseBuilder builder = null;
        if (result != null) {
            builder = Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON_TYPE)
                    .entity(UtilMisc.toMap("code", 200, "result", result));
        } else {
            builder = Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON_TYPE)
                    .entity(new Error(400, "Bad request!", "Incorrect entity requested!"));
        }
        return builder.build();
    }
}
