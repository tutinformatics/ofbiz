package org.apache.ofbiz.jersey.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ofbiz.common.authentication.api.AuthenticatorException;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.jersey.pojo.AuthenticationInput;
import org.apache.ofbiz.jersey.pojo.TokenAuthenticationInput;
import org.apache.ofbiz.jersey.response.Error;
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

import static org.apache.ofbiz.jersey.util.ApiUtil.*;

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
		Response.ResponseBuilder builder;

		try {
			Delegator delegator = (Delegator) servletContext.getAttribute("delegator");

			AuthenticationInput user = mapper.readValue(jsonBody, AuthenticationInput.class);

			authenticateUserLogin(delegator, user);

			builder = Response
					.status(Response.Status.OK)
					.type(MediaType.APPLICATION_JSON_TYPE)
					.entity(getAuthenticationOutput(user));

		} catch (Exception e) {

			builder = Response
					.status(Response.Status.UNAUTHORIZED)
					.type(MediaType.APPLICATION_JSON_TYPE)
					.entity(new Error(401, "Bad auth!", e.getMessage()));
		}

		return builder.build();
	}

	@POST
	@Path("token")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response refreshToken(String jsonBody) {
		Response.ResponseBuilder builder;

		try {

			TokenAuthenticationInput token = mapper.readValue(jsonBody, TokenAuthenticationInput.class);

			AuthenticationInput user = mapper.convertValue(getInnerClaimsFromJwt(token.getToken()), AuthenticationInput.class);

			builder = Response
					.status(Response.Status.OK)
					.type(MediaType.APPLICATION_JSON_TYPE)
					.entity(getAuthenticationOutput(user));

		} catch (Exception e) {

			builder = Response
					.status(Response.Status.UNAUTHORIZED)
					.type(MediaType.APPLICATION_JSON_TYPE)
					.entity(new Error(401, "Bad auth!", e.getMessage()));
		}

		return builder.build();
	}


	@POST
	@Path("register")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response registerUser(String jsonBody) {
		Response.ResponseBuilder builder;

		try {
			Delegator delegator = (Delegator) servletContext.getAttribute("delegator");
			LocalDispatcher dispatcher = (LocalDispatcher) servletContext.getAttribute("dispatcher");

			AuthenticationInput user = mapper.readValue(jsonBody, AuthenticationInput.class);

			fillMissingFields(user);

			if (dispatcher.runSync("createPersonAndUserLogin", mapper.convertValue(user, HashMap.class))
					.get("responseMessage").equals("error")) {
				throw new RuntimeException();
			}

			builder = Response
					.status(Response.Status.OK)
					.type(MediaType.APPLICATION_JSON_TYPE)
					.entity(getAuthenticationOutput(user));

		} catch (Exception e) {
			e.printStackTrace();
			builder = Response
					.status(Response.Status.UNAUTHORIZED)
					.type(MediaType.APPLICATION_JSON_TYPE)
					.entity(new Error(401, "Bad auth!", e.getMessage()));
		}

		return builder.build();
	}

	private void fillMissingFields(AuthenticationInput user) throws AuthenticatorException {
		if (user.getCurrentPasswordVerify() == null) {
			user.setCurrentPasswordVerify(user.getCurrentPassword());
		} else {
			if (!user.getCurrentPassword().equals(user.getCurrentPasswordVerify())) {
				throw new AuthenticatorException();
			}
		}
	}

}
