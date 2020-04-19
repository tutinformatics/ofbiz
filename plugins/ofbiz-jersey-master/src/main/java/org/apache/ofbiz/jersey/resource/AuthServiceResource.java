package org.apache.ofbiz.jersey.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.catalina.CredentialHandler;
import org.apache.ofbiz.base.crypto.HashCrypt;
import org.apache.ofbiz.catalina.container.HashedCredentialHandler;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.jersey.pojo.AuthenticationInput;
import org.apache.ofbiz.jersey.pojo.AuthenticationOutput;
import org.apache.ofbiz.jersey.response.Error;
import org.apache.ofbiz.security.SecurityUtil;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Path("/auth/v1/")
@Provider
public class AuthServiceResource {

    @Context
    private ServletContext servletContext;

    private static final ObjectMapper mapper = new ObjectMapper();

    @POST
    @Path("")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response queryEntities(String jsonBody) {
        Response.ResponseBuilder builder = null;

        try {
            Delegator delegator = (Delegator) servletContext.getAttribute("delegator");

            AuthenticationInput user = mapper.readValue(jsonBody, AuthenticationInput.class);


            GenericValue userLogin = EntityQuery.use(delegator)
                    .from("UserLogin")
                    .where("userLoginId", user.getUsername())
                    .queryOne();

            if (!HashCrypt.comparePassword(userLogin.getString("currentPassword"), "", user.getPassword())) {
                throw new Exception();
            }

            String jwtToken = SecurityUtil.generateJwtToAuthenticateUserLogin(delegator, user.getUsername());

            AuthenticationOutput output = new AuthenticationOutput(jwtToken, user.getUsername());

            builder = Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON_TYPE).entity(output);
        } catch (Exception e) {
            builder = Response.status(Response.Status.UNAUTHORIZED).type(MediaType.APPLICATION_JSON_TYPE).entity(new Error(401, "Bad login!", "Wrong username or password!"));
        }

        return builder.build();
    }

}
