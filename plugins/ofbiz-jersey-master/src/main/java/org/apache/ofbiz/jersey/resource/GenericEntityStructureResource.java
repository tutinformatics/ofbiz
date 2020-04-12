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

import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.model.ModelEntity;
import org.apache.ofbiz.entity.model.ModelField;
import org.apache.ofbiz.entity.model.ModelReader;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.*;

@Path("/generic/v1/structure/entities")
@Provider
//@Secured
public class GenericEntityStructureResource {

	public static final String MODULE = GenericEntityStructureResource.class.getName();

	@Context
	private HttpServletRequest httpRequest;

	@Context
	private ServletContext servletContext;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response listEntities() throws IOException, GenericEntityException {
		ResponseBuilder builder = null;
		Delegator delegator = (Delegator) servletContext.getAttribute("delegator");
		ModelReader reader = delegator.getModelReader();
		TreeSet<String> entities = new TreeSet<String>(reader.getEntityNames());
		builder = Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON).entity(entities);
		return builder.build();
	}

	@GET
	@Path("/{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getEntity(@PathParam(value = "name") String entityName) throws IOException, GenericEntityException {
		ResponseBuilder builder = null;
		List<Map<String, Object>> response = new ArrayList<Map<String, Object>>();
		Delegator delegator = (Delegator) servletContext.getAttribute("delegator");
		ModelEntity entity = delegator.getModelEntity(entityName);
		List<String> fieldNames = entity.getAllFieldNames();
		fieldNames.forEach((fieldName) -> {
			ModelField field = entity.getField(fieldName);
			String fType = field.getType();
			boolean isPk = field.getIsPk();
			LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
			map.put("name", fieldName);
			map.put("type", fType);
			map.put("is_pk", isPk);
			response.add(map);
		});
		builder = Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON).entity(response);
		return builder.build();
	}

}
