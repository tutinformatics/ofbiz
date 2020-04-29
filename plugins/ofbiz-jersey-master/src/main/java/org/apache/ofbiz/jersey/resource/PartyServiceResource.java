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
package org.apache.ofbiz.jersey.resource;

import ee.taltech.marketing.affiliate.model.AffiliateDTO;
import ee.taltech.marketing.affiliate.service.PartyService;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.ExtendedConverters;
import org.apache.ofbiz.jersey.util.JsonUtils;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.LocalDispatcher;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.ext.Provider;
import java.util.List;
import java.util.Map;

@Path("/parties")
@Provider
//@Secured
public class PartyServiceResource {

    public static final String MODULE = PartyServiceResource.class.getName();
    public static final ExtendedConverters.ExtendedJSONToGenericValue jsonToGenericConverter = new ExtendedConverters.ExtendedJSONToGenericValue();
    public static final ExtendedConverters.ExtendedGenericValueToJSON genericToJsonConverter = new ExtendedConverters.ExtendedGenericValueToJSON();


    @Context
    private HttpServletRequest httpRequest;

    @Context
    private ServletContext servletContext;


    private PartyService partyService;

    @PostConstruct
    public void init() {
        LocalDispatcher dispatcher = (LocalDispatcher) servletContext.getAttribute("dispatcher");
        DispatchContext dpc = dispatcher.getDispatchContext();
        partyService = new PartyService(dpc);
    }

    @GET
    @Path("/affiliates")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAffiliates() throws GenericEntityException {
        List<AffiliateDTO> entity = partyService.getAffiliates();
        ResponseBuilder builder = Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON).entity(entity);
        return builder.build();
    }

    @GET
    @Path("/unconfirmedAffiliates")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUnconfirmedAffiliates() throws GenericEntityException {
        List<AffiliateDTO> entity = partyService.getUnconfirmedAffiliates();
        ResponseBuilder builder = Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON).entity(entity);
        return builder.build();
    }

    @POST()
    @Path("/affiliate/create")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createAffiliates(String jsonBody) throws Exception {
        Map<String, Object> data = JsonUtils.parseJson(jsonBody);
        AffiliateDTO entity = partyService.createAffiliateForUserLogin(data);
        ResponseBuilder builder = Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON).entity(entity);
        return builder.build();
    }

    @POST()
    @Path("/get-party-id")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPartyId(String jsonBody) throws Exception {
        Map<String, Object> data = JsonUtils.parseJson(jsonBody);
        AffiliateDTO entity = partyService.getPartyIdForUserId(data);
        ResponseBuilder builder = Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON).entity(entity);
        return builder.build();
    }


    @POST()
    @Path("/affiliate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAffiliate(String jsonBody) throws Exception {
        Map<String, Object> data = JsonUtils.parseJson(jsonBody);
        AffiliateDTO entity = partyService.getAffiliate(data);
        ResponseBuilder builder = Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON).entity(entity);
        return builder.build();
    }

    @PUT()
    @Path("/affiliate/approve")
    @Produces(MediaType.APPLICATION_JSON)
    public Response approveAffiliate(String jsonBody) throws Exception {
        Map<String, Object> data = JsonUtils.parseJson(jsonBody);
        AffiliateDTO genericValue = partyService.approve(data);
        ResponseBuilder builder = Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON).entity(genericValue);
        return builder.build();
    }

    @PUT()
    @Path("/affiliate/disapprove")
    @Produces(MediaType.APPLICATION_JSON)
    public Response disapproveAffiliates(String jsonBody) throws Exception {
        Map<String, Object> data = JsonUtils.parseJson(jsonBody);
        AffiliateDTO entity = partyService.disapprove(data);
        ResponseBuilder builder = Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON).entity(entity);
        return builder.build();
    }

    @DELETE()
    @Path("/affiliate/code")
    @Produces(MediaType.APPLICATION_JSON)
    public Response approveAffiliates(String jsonBody) throws Exception {
        Map<String, Object> data = JsonUtils.parseJson(jsonBody);
        GenericValue entity = partyService.deleteAffiliateCodes(data);
        ResponseBuilder builder = Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON).entity(entity);
        return builder.build();
    }

    @POST()
    @Path("/affiliate/code")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createCode(String jsonBody) throws Exception {
        Map<String, Object> data = JsonUtils.parseJson(jsonBody);
        GenericValue entity = partyService.createAffiliateCode(data);
        ResponseBuilder builder = Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON).entity(entity);
        return builder.build();
    }


    @POST()
    @Path("/affiliate/codes")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCodes(String jsonBody) throws Exception {
        Map<String, Object> data = JsonUtils.parseJson(jsonBody);
        List<GenericValue> entity = partyService.getAffiliateCodes(data);
        ResponseBuilder builder = Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON).entity(entity);
        return builder.build();
    }
}
