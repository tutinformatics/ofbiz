package org.apache.ofbiz.jersey.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ofbiz.base.util.UtilGenerics;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.condition.EntityCondition;
import org.apache.ofbiz.entity.condition.EntityConditionList;
import org.apache.ofbiz.entity.model.ModelEntity;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.entity.util.EntityUtilProperties;
import org.apache.ofbiz.entity.util.ExtendedConverters;
import org.apache.ofbiz.jersey.annotation.Secured;
import org.apache.ofbiz.jersey.pojo.EntityQueryInput;
import org.apache.ofbiz.jersey.response.Error;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import static org.apache.ofbiz.base.util.UtilGenerics.checkMap;

@Path("/generic/v1/entityquery/")
@Provider
@Secured
public class GenericEntityQueryResource {

	public static final String MODULE = GenericEntityResource.class.getName();
	public static final ExtendedConverters.ExtendedJSONToGenericValue jsonToGenericConverter = new ExtendedConverters.ExtendedJSONToGenericValue();
	public static final ExtendedConverters.ExtendedGenericValueToJSON genericToJsonConverter = new ExtendedConverters.ExtendedGenericValueToJSON();
	private static ObjectMapper mapper = new ObjectMapper();
	@Context
	private HttpServletRequest httpRequest;
	@Context
	private ServletContext servletContext;

	@POST
	@Path("/{name}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response queryEntities(@PathParam(value = "name") String entityName, String jsonBody) {
		Response.ResponseBuilder builder;
		Delegator delegator = (Delegator) servletContext.getAttribute("delegator");

		EntityQueryInput query;
		try {
			query = mapper.readValue(jsonBody, EntityQueryInput.class);
		} catch (JsonProcessingException e) {
			Error error = new Error(400, "Bad Request", "Error converting body to required form.");
			builder = Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON_TYPE).entity(error);
			return builder.build();
		}

		EntityConditionList<EntityCondition> conditionList;
		try {
			conditionList = getConditionList(entityName, query.toMap());
		} catch (GenericServiceException e) {
			Error error = new Error(500, "Internal Server Error", "Error forming a condition list from given parameters.");
			builder = Response.status(Response.Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_JSON_TYPE).entity(error);
			return builder.build();
		}
		List<GenericValue> completeList;
		try {
			completeList = EntityQuery.use(delegator).from(entityName).where(conditionList).queryList();
		} catch (GenericEntityException e) {
			Error error = new Error(500, "Internal Server Error", "Error searching for top level entities.");
			builder = Response.status(Response.Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_JSON_TYPE).entity(error);
			return builder.build();
		}

		List<Map<String, ?>> filteredRel;
		try {
			filteredRel = recursiveFind(query, entityName, delegator, completeList);
		} catch (GenericServiceException e) {
			Error error = new Error(500, "Internal Server Error", "Error getting condition list for a lower level entity.");
			builder = Response.status(Response.Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_JSON_TYPE).entity(error);
			return builder.build();
		} catch (GenericEntityException e) {
			Error error = new Error(500, "Internal Server Error", "Error getting related objects.");
			builder = Response.status(Response.Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_JSON_TYPE).entity(error);
			return builder.build();
		}

		builder = Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON_TYPE).entity(filteredRel);
		return builder.build();
	}

	private List<Map<String, ?>> recursiveFind(EntityQueryInput queryInput, String entityName, Delegator delegator, List<GenericValue> values) throws GenericServiceException, GenericEntityException {
		List<Map<String, ?>> queryResponse = new ArrayList<>();
		EntityConditionList<EntityCondition> conditionList = getConditionList(entityName, queryInput.toMap());
		ModelEntity modelEntity = delegator.getModelEntity(entityName);

		List<String> singularRelationName = queryInput.getEntityRelations().keySet().stream().filter(x -> x.startsWith("_toOne_")).map(x -> x.replace("_toOne_", "")).collect(Collectors.toList());
		List<String> multitudeRelationName = queryInput.getEntityRelations().keySet().stream().filter(x -> x.startsWith("_toMany_")).map(x -> x.replace("_toMany_", "")).collect(Collectors.toList());
		for (GenericValue val : values) {
			if (conditionList == null || conditionList.entityMatches(val)) {
				Map<String, Object> relationBuilder =queryInput.getFieldList().size() > 0 ? val.getFields(queryInput.getFieldList()) : val.getAllFields();

				boolean skip = false;

				for (String singularRel : singularRelationName) {
					String fullName = "_toOne_" + singularRel;
					List<GenericValue> rels = val.getRelated(singularRel, null, null, false);
					EntityQueryInput relQueryInput = queryInput.getEntityRelations().get(fullName);
					List<Map<String, ?>> filteredRel = recursiveFind(relQueryInput, modelEntity.getRelation(singularRel).getRelEntityName(), delegator, rels);
					if (queryInput.getAreRelationResultsMandatory() && filteredRel.size() == 0) {
						skip = true;
						break;
					} else if (filteredRel.size() > 0) {
						relationBuilder.put(fullName, filteredRel.get(0));
					}
				}

				if (skip) continue;

				for (String multitudeRel : multitudeRelationName) {
					String fullName = "_toMany_" + multitudeRel;
					List<GenericValue> rels = val.getRelated(multitudeRel, null, null, false);
					EntityQueryInput relQueryInput = queryInput.getEntityRelations().get(fullName);
					List<Map<String, ?>> filteredRel = recursiveFind(relQueryInput, modelEntity.getRelation(multitudeRel).getRelEntityName(), delegator, rels);
					if (queryInput.getAreRelationResultsMandatory() && filteredRel.size() == 0) {
						skip = true;
						break;
					} else {
						relationBuilder.put(fullName, filteredRel);
					}
				}

				if (skip) continue;

				queryResponse.add(relationBuilder);
			}
		}
		return queryResponse;

	}

	private EntityConditionList<EntityCondition> getConditionList(String entityName, Map<String, ?> query) throws GenericServiceException {

		LocalDispatcher dispatcher = (LocalDispatcher) servletContext.getAttribute("dispatcher");
		Delegator delegator = (Delegator) servletContext.getAttribute("delegator");

		// copied from prepareFind service
		String orderBy = (String) query.get("orderBy");
		Map<String, ?> inputFields = checkMap(query.get("inputFields"), String.class, Object.class); // Input
		String noConditionFind = (String) query.get("noConditionFind");
		String distinct = (String) query.get("distinct");
		List<String> fieldList = UtilGenerics.cast(query.get("fieldList"));
		GenericValue userLogin = (GenericValue) query.get("userLogin");
		Locale locale = (Locale) query.get("locale");

		if (UtilValidate.isEmpty(noConditionFind)) {
			// try finding in inputFields Map
			noConditionFind = (String) inputFields.get("noConditionFind");
		}
		if (UtilValidate.isEmpty(noConditionFind)) {
			// Use configured default
			noConditionFind = EntityUtilProperties.getPropertyValue("widget", "widget.defaultNoConditionFind", delegator);
		}
		String filterByDate = (String) query.get("filterByDate");
		if (UtilValidate.isEmpty(filterByDate)) {
			// try finding in inputFields Map
			filterByDate = (String) inputFields.get("filterByDate");
		}
		Timestamp filterByDateValue = (Timestamp) query.get("filterByDateValue");
		String fromDateName = (String) query.get("fromDateName");
		if (UtilValidate.isEmpty(fromDateName)) {
			// try finding in inputFields Map
			fromDateName = (String) inputFields.get("fromDateName");
		}
		String thruDateName = (String) query.get("thruDateName");
		if (UtilValidate.isEmpty(thruDateName)) {
			// try finding in inputFields Map
			thruDateName = (String) inputFields.get("thruDateName");
		}

		Integer viewSize = (Integer) query.get("viewSize");
		Integer viewIndex = (Integer) query.get("viewIndex");
		Integer maxRows = null;
		if (viewSize != null && viewIndex != null) {
			maxRows = viewSize * (viewIndex + 1);
		}

		Map<String, Object> prepareResult = null;

		prepareResult = dispatcher.runSync("prepareFind", UtilMisc.toMap("entityName", entityName, "orderBy", orderBy,
				"inputFields", inputFields, "filterByDate", filterByDate, "noConditionFind", noConditionFind,
				"filterByDateValue", filterByDateValue, "userLogin", userLogin, "fromDateName", fromDateName, "thruDateName", thruDateName,
				"locale", query.get("locale"), "timeZone", query.get("timeZone")));

		return UtilGenerics.cast(prepareResult.get("entityConditionList"));
	}
}
