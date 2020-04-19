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
package org.apache.ofbiz.graphql.filter;

import graphql.ExecutionResultImpl;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.kickstart.execution.GraphQLObjectMapper;
import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.graphql.GraphQLErrorType;
import org.apache.ofbiz.graphql.config.OFBizGraphQLObjectMapperConfigurer;
import org.apache.ofbiz.security.SecurityUtil;
import org.apache.ofbiz.webapp.control.JWTManager;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;


public class AuthenticationFilter implements Filter {

    private static final Boolean IS_DEV = true;

    private static final String MODULE = AuthenticationFilter.class.getName();
    private final GraphQLObjectMapper mapper;
    private static final String AUTHENTICATION_SCHEME = "Bearer";
    private static final String REALM = "OFBiz-GraphQl";
    private static final String INTROSPECTION_QUERY_PATH = "/schema.json";

    {
        mapper = GraphQLObjectMapper.newBuilder().withObjectMapperConfigurer(new OFBizGraphQLObjectMapperConfigurer()).build();
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String authorizationHeader = httpRequest.getHeader(HttpHeaders.AUTHORIZATION);
        ServletContext servletContext = request.getServletContext();
        Delegator delegator = (Delegator) servletContext.getAttribute("delegator");

        if (!IS_DEV) {
            if (!isTokenBasedAuthentication(authorizationHeader)) {
                abortWithUnauthorized(httpResponse, false, "Authentication Required");
                return;
            }
        }

        String jwtToken;
        try {
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
            abortWithUnauthorized(httpResponse, true, "Access Denied: User does not exist in the system");
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }

    /**
     * @param request
     * @return
     */
    private boolean isIntrospectionQuery(HttpServletRequest request) {
        String path = Optional.ofNullable(request.getPathInfo()).orElseGet(request::getServletPath).toLowerCase();
        return path.contentEquals(INTROSPECTION_QUERY_PATH);
    }

    /**
     * @param requestContext
     * @throws IOException
     */
    private void abortWithUnauthorized(HttpServletResponse httpResponse, boolean isAuthHeaderPresent, String message) throws IOException {
        httpResponse.reset();
        httpResponse.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
        if (!isAuthHeaderPresent) {
            httpResponse.addHeader(HttpHeaders.WWW_AUTHENTICATE, AUTHENTICATION_SCHEME + " realm=\"" + REALM + "\"");
        }
        httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        GraphQLError error = GraphqlErrorBuilder.newError().message(message, (Object[]) null).errorType(GraphQLErrorType.AuthenticationError).build();
        ExecutionResultImpl result = new ExecutionResultImpl(error);
        mapper.serializeResultAsJson(httpResponse.getWriter(), result);

    }

    /**
     * /**
     *
     * @param authorizationHeader
     * @return
     */
    private boolean isTokenBasedAuthentication(String authorizationHeader) {
        return authorizationHeader != null && authorizationHeader.toLowerCase().startsWith(AUTHENTICATION_SCHEME.toLowerCase() + " ");
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
