/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package org.apache.ofbiz.jersey.providers;

import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.jersey.annotation.Secured;
import org.apache.ofbiz.jersey.pojo.AuthenticationInput;
import org.apache.ofbiz.jersey.util.ApiUtil;
import org.apache.ofbiz.webapp.control.JWTManager;

import javax.annotation.Priority;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.util.Map;

import static org.apache.ofbiz.jersey.util.ApiUtil.*;

@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

	private static final String MODULE = AuthenticationFilter.class.getName();
	private static final Boolean IS_DEV = true;

	@Context
	private ResourceInfo resourceInfo;

	@Context
	private HttpServletRequest httpRequest;

	@Context
	private ServletContext servletContext;

	private static final String AUTHENTICATION_SCHEME = "Bearer";
	private static final String REALM = "OFBiz";

	@Override
	public void filter(ContainerRequestContext requestContext) {
		String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
		Delegator delegator = (Delegator) servletContext.getAttribute("delegator");

		if (!IS_DEV) {
			if (!isTokenBasedAuthentication(authorizationHeader)) {
				abortWithUnauthorized(requestContext, false, "Unauthorized: Access is denied due to invalid or absent Authorization header");
				return;
			}
		}

		try {
			String jweToken;

			if (IS_DEV) {
				jweToken = generateAdminToken(delegator);
			} else {
				jweToken = JWTManager.getHeaderAuthBearerToken(httpRequest); // GET FROM HEADER
			}

			Map<String, Object> claims = getClaimsFromToken(getBodyFromJWE(jweToken));

			AuthenticationInput user = AuthenticationInput.builder()
					.userLoginId(String.valueOf(claims.get("userLoginId")))
					.currentPassword(String.valueOf(claims.get("currentPassword")))
					.build();

			authenticateUserLogin(delegator, user);

		} catch (Exception e) {
			abortWithUnauthorized(requestContext, true, "Access Denied: User does not exist in the system");
		}
	}

	/**
	 * @param authorizationHeader
	 * @return
	 */
	private boolean isTokenBasedAuthentication(String authorizationHeader) {
		return authorizationHeader != null && authorizationHeader.toLowerCase().startsWith(AUTHENTICATION_SCHEME.toLowerCase() + " ");
	}

	/**
	 * @param requestContext
	 */
	private void abortWithUnauthorized(ContainerRequestContext requestContext, boolean isAuthHeaderPresent, String message) {
		if (!isAuthHeaderPresent) {
			requestContext.abortWith(ApiUtil.errorResponse(Response.Status.UNAUTHORIZED.getStatusCode(), Response.Status.UNAUTHORIZED.getReasonPhrase(), message)
					.header(HttpHeaders.WWW_AUTHENTICATE, AUTHENTICATION_SCHEME + " realm=\"" + REALM + "\"").build());
		} else {
			requestContext.abortWith(ApiUtil.errorResponse(Response.Status.FORBIDDEN.getStatusCode(), Response.Status.FORBIDDEN.getReasonPhrase(), message).build());
		}

	}

}
