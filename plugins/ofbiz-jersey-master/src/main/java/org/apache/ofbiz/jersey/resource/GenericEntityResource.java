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

import ee.taltech.marketing.affiliate.service.PartyService;
import org.apache.ofbiz.base.conversion.ConversionException;
import org.apache.ofbiz.base.lang.JSON;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.model.ModelEntity;
import org.apache.ofbiz.entity.model.ModelReader;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.entity.util.ExtendedConverters;
import org.apache.ofbiz.jersey.annotation.Secured;
import org.apache.ofbiz.jersey.response.Error;
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
import java.util.*;
import java.util.stream.Collectors;

@Path("/generic/v1/entities")
@Provider
@Secured
public class GenericEntityResource {

	public static final String MODULE = GenericEntityResource.class.getName();
	public static final ExtendedConverters.ExtendedJSONToGenericValue jsonToGenericConverter = new ExtendedConverters.ExtendedJSONToGenericValue();
	public static final ExtendedConverters.ExtendedGenericValueToJSON genericToJsonConverter = new ExtendedConverters.ExtendedGenericValueToJSON();


	@Context
	private HttpServletRequest httpRequest;

	@Context
	private ServletContext servletContext;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getEntityNames() {
		ResponseBuilder builder;
		Delegator delegator = (Delegator) servletContext.getAttribute("delegator");
		ModelReader reader = delegator.getModelReader();

		Set<String> entityNames;
		try {
			entityNames = reader.getEntityNames();
		} catch (GenericEntityException e) {
			Error error = new Error(500, "Internal Server Error", "Error fetching existing entity names.");
			builder = Response.status(Response.Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_JSON_TYPE).entity(error);
			e.printStackTrace();
			return builder.build();
		}

		TreeSet<String> entities = new TreeSet<String>(entityNames);
		builder = Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON).entity(entities);
		return builder.build();
	}

	@GET
	@Path("/{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getEntity(@PathParam(value = "name") String entityName, @Context UriInfo allUri) {
		ResponseBuilder builder;
		Map<String, List<String>> mpAllQueParams = allUri.getQueryParameters().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		List<String> depthStringList = mpAllQueParams.remove("_depth");
		int depth = 0;
		if (depthStringList != null && depthStringList.size() > 0) {
			depth = Integer.parseInt(depthStringList.get(0));
		}
		Delegator delegator = (Delegator) servletContext.getAttribute("delegator");

		ModelEntity model;
		try {
			model = delegator.getModelReader().getModelEntity(entityName);
		} catch (GenericEntityException e) {
			Error error = new Error(400, "Bad Request", "Error fetching model for given entity name.");
			builder = Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON_TYPE).entity(error);
			return builder.build();
		}

		Map<String, Object> secondary = mpAllQueParams.entrySet().stream()
				.map(x -> new AbstractMap.SimpleEntry<>(x.getKey(), QueryParamStringConverter.convert(x.getValue().get(0), model.getField(x.getKey()).getType())))
				.collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
		List<GenericValue> allEntities;

		try {
			allEntities = EntityQuery.use(delegator).from(entityName).where(secondary).queryList();
		} catch (GenericEntityException e) {
			Error error = new Error(400, "Bad Request", "Error searching for entities on given parameters.");
			builder = Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON_TYPE).entity(error);
			e.printStackTrace();
			return builder.build();
		}

		JSON json;
		try {
			json = genericToJsonConverter.convertListWithChildren(allEntities, depth);
		} catch (ConversionException e) {
			Error error = new Error(500, "Internal Server Error", "Error converting result to JSON.");
			builder = Response.status(Response.Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_JSON_TYPE).entity(error);
			e.printStackTrace();
			return builder.build();
		}

		builder = Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON).entity(json.toString());
		return builder.build();
	}

	// TODO: recursive addition
	@POST
	@Path("/{name}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addEntity(@PathParam(value = "name") String entityName, String jsonBody) {
		Response.ResponseBuilder builder;
		Delegator delegator = (Delegator) servletContext.getAttribute("delegator");

		GenericValue object;
		try {
			object = jsonToGenericConverter.convert(delegator.getDelegatorName(), entityName, JSON.from(jsonBody));
		} catch (ConversionException e) {
			Error error = new Error(400, "Bad Request", "Error converting request body to Java object.");
			builder = Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON_TYPE).entity(error);
			e.printStackTrace();
			return builder.build();
		}

		ModelEntity model = delegator.getModelEntity(entityName);
		if (model.getPksSize() == 1) {
			// because can only sequence if one PK field
			if (object.get(model.getFirstPkFieldName()) == null) {
				object.setNextSeqId();
			}
		}
		GenericValue gv;
		try {
			gv = delegator.create(object);
		} catch (GenericEntityException e) {
			Error error = new Error(500, "Internal Server Error", "Error saving entity.");
			builder = Response.status(Response.Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_JSON_TYPE).entity(error);
			e.printStackTrace();
			return builder.build();
		}

		try {
			builder = Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON_TYPE).entity(genericToJsonConverter.convert(gv).toString());
		} catch (ConversionException e) {
			Error error = new Error(500, "Internal Server Error", "Error converting entity to JSON.");
			builder = Response.status(Response.Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_JSON_TYPE).entity(error);
			e.printStackTrace();
			return builder.build();
		}

		return builder.build();
	}

	@PUT
	@Path("/{name}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateEntity(@PathParam(value = "name") String entityName, String jsonBody) {
		Response.ResponseBuilder builder;
		Delegator delegator = (Delegator) servletContext.getAttribute("delegator");

		GenericValue object;
		try {
			object = jsonToGenericConverter.convert(delegator.getDelegatorName(), entityName, JSON.from(jsonBody));
		} catch (ConversionException e) {
			Error error = new Error(400, "Bad Request", "Error converting request body to Java object.");
			builder = Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON_TYPE).entity(error);
			e.printStackTrace();
			return builder.build();
		}

		GenericValue check;
		try {
			check = delegator.findOne(entityName, object.getPrimaryKey(), false);
		} catch (GenericEntityException e) {
			Error error = new Error(400, "Bad Request", "Error performing search based on given primary keys.");
			builder = Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON_TYPE).entity(error);
			e.printStackTrace();
			return builder.build();
		}

		// if there indeed is an entity in db with such PKs
		if (check != null) {
			try {
				delegator.store(object);
			} catch (GenericEntityException e) {
				Error error = new Error(500, "Internal Server Error", "Error storing updated entity.");
				builder = Response.status(Response.Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_JSON_TYPE).entity(error);
				e.printStackTrace();
				return builder.build();
			}
			builder = Response.status(Response.Status.OK);
		} else {
			Error error = new Error(400, "Bad Request", "No matching entity found for given primary keys.");
			builder = Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON_TYPE).entity(error);
		}

		return builder.build();
	}


	@DELETE
	@Path("/{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteEntity(@PathParam(value = "name") String entityName, @Context UriInfo allUri) {
		ResponseBuilder builder;
		Map<String, List<String>> mpAllQueParams = allUri.getQueryParameters().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		Delegator delegator = (Delegator) servletContext.getAttribute("delegator");
		ModelEntity model;
		try {
			model = delegator.getModelReader().getModelEntity(entityName);
		} catch (GenericEntityException e) {
			Error error = new Error(400, "Bad Request", "Error fetching model for given entity name.");
			builder = Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON_TYPE).entity(error);
			e.printStackTrace();
			return builder.build();
		}
		Map<String, Object> searchParams = mpAllQueParams.entrySet().stream()
				.map(x -> new AbstractMap.SimpleEntry<>(x.getKey(), QueryParamStringConverter.convert(x.getValue().get(0), model.getField(x.getKey()).getType())))
				.collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));

		List<GenericValue> allEntities;
		try {
			allEntities = EntityQuery.use(delegator).from(entityName).where(searchParams).queryList();
		} catch (GenericEntityException e) {
			Error error = new Error(400, "Bad Request", "Error searching entities based on given parameters.");
			builder = Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON_TYPE).entity(error);
			e.printStackTrace();
			return builder.build();
		}

		if (allEntities.size() > 1) {
			builder = Response.status(Response.Status.BAD_REQUEST);
		}
		else if (allEntities.size() == 0) {
			builder = Response.status(Response.Status.NOT_FOUND);
		}
		else {
			try {
				delegator.removeValue(allEntities.get(0));
			} catch (GenericEntityException e) {
				Error error = new Error(500, "Internal Server Error", "Error deleting entity.");
				builder = Response.status(Response.Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_JSON_TYPE).entity(error);
				e.printStackTrace();
				return builder.build();
			}
			JSON json;
			try {
				json = genericToJsonConverter.convertWithChildren(allEntities.get(0));
			} catch (ConversionException e) {
				Error error = new Error(500, "Internal Server Error", "Error converting GenericValue to Json.");
				builder = Response.status(Response.Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_JSON_TYPE).entity(error);
				e.printStackTrace();
				return builder.build();
			}
			builder = Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON).entity(json.toString());
		}
		return builder.build();
	}
}
