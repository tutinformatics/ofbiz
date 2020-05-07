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

import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.jersey.annotation.Secured;
import org.apache.ofbiz.jersey.util.ApiUtil;
import org.apache.ofbiz.security.SecurityUtil;
import org.apache.ofbiz.webapp.control.JWTManager;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;

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
import java.io.IOException;
import java.util.Map;

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
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        Delegator delegator = (Delegator) servletContext.getAttribute("delegator");

        if (!IS_DEV) {
            if (!isTokenBasedAuthentication(authorizationHeader)) {
                abortWithUnauthorized(requestContext, false, "Unauthorized: Access is denied due to invalid or absent Authorization header");
                return;
            }
        }

        try {
            String jwtToken;

            if (IS_DEV) {
                jwtToken = SecurityUtil.generateJwtToAuthenticateUserLogin(delegator, "admin");
            } else {
                jwtToken = JWTManager.getHeaderAuthBearerToken(httpRequest); // GET FROM HEADER
            }

            JwtConsumer jwtConsumer = new JwtConsumerBuilder()
                    .setSkipSignatureVerification()
                    .build();

            JwtClaims jwtClaims = jwtConsumer.processToClaims(jwtToken);
            Map<String, Object> claims = jwtClaims.getClaimsMap();

            assert SecurityUtil.authenticateUserLoginByJWT(delegator, String.valueOf(claims.get("userLoginId")), jwtToken);

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

    private GenericValue extractUserLoginFromJwtClaim(Delegator delegator, Map<String, Object> claims) {
        String userLoginId = (String) claims.get("userLoginId");
        if (UtilValidate.isEmpty(userLoginId)) {
            Debug.logWarning("No userLoginId found in the JWT token.", MODULE);
            return null;
        }
        GenericValue userLogin = null;
        try {
            userLogin = EntityQuery.use(delegator).from("UserLogin").where("userLoginId", userLoginId).queryOne();
            if (UtilValidate.isEmpty(userLogin)) {
                Debug.logWarning("There was a problem with the JWT token. Could not find provided userLogin " + userLoginId, MODULE);
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, "Unable to get UserLogin information from JWT Token: " + e.getMessage(), MODULE);
        }
        return userLogin;
    }

}
