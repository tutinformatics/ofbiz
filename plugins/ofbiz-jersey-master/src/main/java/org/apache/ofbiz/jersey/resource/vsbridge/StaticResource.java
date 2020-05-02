package org.apache.ofbiz.jersey.resource.vsbridge;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ofbiz.base.lang.JSON;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.jersey.pojo.AuthenticationOutput;
import org.apache.ofbiz.jersey.resource.AuthServiceResource;
import org.apache.ofbiz.jersey.resource.ProjectResource;
import org.apache.ofbiz.jersey.response.Error;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.Map;

@Provider
@Path("/vsbridge/")
public class StaticResource {

    public static final String module = ProjectResource.class.getName();

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
    @Path("{a:user\\/login|auth\\/admin}")
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
            e.printStackTrace();
            builder = Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON_TYPE)
                    .entity(new Error(400, "Bad request!", "Can't read json!"));
        }
        return builder.build();
    }
}
