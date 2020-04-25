package org.apache.ofbiz.jersey.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ofbiz.base.crypto.HashCrypt;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.jersey.pojo.AuthenticationInput;
import org.apache.ofbiz.jersey.pojo.AuthenticationOutput;
import org.apache.ofbiz.jersey.response.Error;
import org.apache.ofbiz.security.SecurityUtil;
import org.apache.ofbiz.service.LocalDispatcher;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;

@Path("/auth/v1/")
@Provider
public class AuthServiceResource {

    @Context
    private ServletContext servletContext;

    private static final ObjectMapper mapper = new ObjectMapper();

    @POST
    @Path("login")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response loginUser(String jsonBody) {
        Response.ResponseBuilder builder = null;

        try {
            Delegator delegator = (Delegator) servletContext.getAttribute("delegator");

            AuthenticationInput user = mapper.readValue(jsonBody, AuthenticationInput.class);

            if (user.getCurrentPasswordVerify() == null) {
                user.setCurrentPasswordVerify(user.getCurrentPassword());
            } else {
                assert user.getCurrentPassword().equals(user.getCurrentPasswordVerify());
            }

            GenericValue userLogin = EntityQuery.use(delegator)
                    .from("UserLogin")
                    .where("userLoginId", user.getUserLoginId())
                    .queryOne();

            if (!HashCrypt.comparePassword(userLogin.getString("currentPassword"), "", user.getCurrentPassword())) {
                throw new Exception();
            }

            String jwtToken = SecurityUtil.generateJwtToAuthenticateUserLogin(delegator, user.getUserLoginId());

            AuthenticationOutput output = new AuthenticationOutput(jwtToken, user.getUserLoginId());

            builder = Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON_TYPE).entity(output);
        } catch (Exception e) {
            builder = Response.status(Response.Status.UNAUTHORIZED).type(MediaType.APPLICATION_JSON_TYPE).entity(new Error(401, "Bad login!", "Wrong username or password!"));
        }

        return builder.build();
    }

    @POST
    @Path("register")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerUser(String jsonBody) {
        Response.ResponseBuilder builder = null;

        try {
            Delegator delegator = (Delegator) servletContext.getAttribute("delegator");
            LocalDispatcher dispatcher = (LocalDispatcher) servletContext.getAttribute("dispatcher");

            AuthenticationInput user = mapper.readValue(jsonBody, AuthenticationInput.class);

            if (user.getCurrentPasswordVerify() == null) {
                user.setCurrentPasswordVerify(user.getCurrentPassword());
            } else {
                assert user.getCurrentPassword().equals(user.getCurrentPasswordVerify());
            }

            HashMap<String, Object> result = mapper.convertValue(user, HashMap.class);

            Map<String, ?> entity = dispatcher.runSync("createPersonAndUserLogin", result);

            if (entity.get("responseMessage").equals("error")) {
                throw new RuntimeException();
            }

            String jwtToken = SecurityUtil.generateJwtToAuthenticateUserLogin(delegator, user.getUserLoginId());

            AuthenticationOutput output = new AuthenticationOutput(jwtToken, user.getUserLoginId());

            builder = Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON_TYPE).entity(output);
        } catch (Exception e) {
            builder = Response.status(Response.Status.UNAUTHORIZED).type(MediaType.APPLICATION_JSON_TYPE).entity(new Error(403, "Bad register!", "Invalid input!"));
        }

        return builder.build();
    }

}
