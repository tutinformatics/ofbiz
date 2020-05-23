package ee.taltech.softwareengineering.ofbiz.bigdata.rest.camel.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.camel.Exchange;
import org.apache.ofbiz.base.conversion.ConversionException;
import org.apache.ofbiz.base.lang.JSON;
import org.apache.ofbiz.base.util.UtilGenerics;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.model.ModelEntity;
import org.apache.ofbiz.entity.model.ModelField;
import org.apache.ofbiz.entity.model.ModelReader;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.entity.util.ExtendedConverters;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.GenericDispatcherFactory;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class TemplateService {

    public static final String module = TemplateService.class.getName();
    // There probably is a slightly better way to do them
    public static final ExtendedConverters.ExtendedJSONToGenericValue jsonToGenericConverter = new ExtendedConverters.ExtendedJSONToGenericValue();
    public static final ExtendedConverters.ExtendedGenericValueToJSON genericToJsonConverter = new ExtendedConverters.ExtendedGenericValueToJSON();
    public static Map<String, String> entityMap;
    public static Map<String, String> serviceMap;
    protected static ModelReader modelReader;
    private Delegator delegator;
    private LocalDispatcher dispatcher;
    private DispatchContext dpc;


    public TemplateService(Delegator delegator) throws GenericEntityException {
        this.delegator = delegator;
        dispatcher = new GenericDispatcherFactory().createLocalDispatcher("genericRestEndpointDispatcher", delegator);
        modelReader = delegator.getModelReader();
        entityMap = modelReader.getEntityCache().entrySet().stream().collect(Collectors.toMap(x -> x.getKey().toLowerCase(), Map.Entry::getKey));
        dpc = dispatcher.getDispatchContext();
        serviceMap = dpc.getAllServiceNames().stream().collect(Collectors.toMap(String::toLowerCase, x -> x));
    }


    public String generateDepthOneTempJsonFromModelEntity(ModelEntity value, HashSet<String> knownKeys) {
//        if (knownKeys.contains(value.getPackageName())) {
        return generateDepthOneJsonFromModelEntity(value);
//        } else {
//            knownKeys.add(value.getPackageName());
//            return "{\"" +
//                    value.getPackageName() +
//                    "\":\".\"" +
//                    "}";
//        }
    }


    public String generateDepthOneJsonFromModelEntity(ModelEntity value) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        for (Iterator<ModelField> it = value.getFieldsIterator(); it.hasNext(); ) {
            ModelField field = it.next();
            json
                    .append("\"")
                    .append(field.getColName())
                    .append("\":\"")
                    .append(field.getType())
                    .append("\"");

            if (it.hasNext()) {
                json.append(",");
            } else {
                json.append("}");
            }
        }
        return json.toString();
    }


    public String generateDepthTwoJsonFromModelEntity(ModelEntity value) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        HashSet<String> knownKeys = new HashSet<>();
        for (Iterator<ModelField> it = value.getFieldsIterator(); it.hasNext(); ) {
            ModelField field = it.next();
            json
                    .append("\"")
                    .append(field.getColName())
                    .append("\":")
                    .append(generateDepthOneTempJsonFromModelEntity(field.getModelEntity(), knownKeys));

            if (it.hasNext()) {
                json.append(",");
            } else {
                json.append("}");
            }
        }
        return json.toString();
    }


    public Object getJSONSchemas(Exchange exchange) {
        try {
            Set<Map.Entry<String, ModelEntity>> entries = modelReader.getEntityCache().entrySet();
            StringBuilder json = new StringBuilder();
            json.append("{");
            for (Iterator<Map.Entry<String, ModelEntity>> iterator = new ArrayList<>(entries).iterator(); iterator.hasNext(); ) {
                Map.Entry<String, ModelEntity> map = iterator.next();
                json
                        .append("\"")
                        .append(map.getKey())
                        .append("\":")
                        .append(generateDepthTwoJsonFromModelEntity(map.getValue()));

                if (iterator.hasNext()) {
                    json.append(",\n");
                } else {
                    json.append("}");
                }

            }

            return json.toString();
        } catch (Exception e) {
            return e.getMessage();
        }
    }


    public String getAll(Exchange exchange) {
        String entity = exchange.getIn().getHeader("entity").toString();
        entity = entityMap.get(entity);
        if (entity == null) {
            return "{\n    \"error\": \"No such entity.\"\n}";
        }

        List<GenericValue> orderItems = new ArrayList<>();
        try {
            orderItems = EntityQuery.use(delegator)
                    .from(entity)    // "Invoice" is name of the entity defined in datamodel component under applications/
                    .queryList();       // execute
        } catch (GenericEntityException e) {
            e.printStackTrace();
            GenericValue error = new GenericValue();
            error.put("Error", e);
            orderItems.add(error);
        }
        // convert to json and send them off
//		return gson.toJson(orderItems);
        try {
            return genericToJsonConverter.convertListWithChildren(orderItems).toString();
        } catch (ConversionException e) {
            e.printStackTrace();
            return ":(";
        }
    }

    public Response insert(Exchange exchange) {
        String entity = exchange.getIn().getHeader("entity").toString();
        entity = entityMap.get(entity);
        if (entity == null) {
            return Response.serverError().entity("Error of some sort").build();
        }

        try {
            GenericValue object = jsonToGenericConverter.convert(delegator.getDelegatorName(), entity, JSON.from(exchange.getIn().getBody().toString()));
            object.setNextSeqId();
            delegator.create(object);
            return Response.ok().type("application/json").build();
        } catch (GenericEntityException | ConversionException e) {
            e.printStackTrace();
            return Response.serverError().entity("Error of some sort").build();
        }
    }

    public String service(Exchange exchange) {
        String serviceName = serviceMap.get(exchange.getIn().getHeader("service").toString());
        if (serviceName == null) {
            return "{\n    \"error\": \"No such service name.\"\n}";
        }
        JSON o = JSON.from(exchange.getIn().getBody().toString());
        Map<String, Object> fieldMap;
        try {
            fieldMap = UtilGenerics.<Map<String, Object>>cast(o.toObject(Map.class));
        } catch (IOException e) {
            return "{\n    \"error\": \"Conversion to Map failed.\"\n}";
        }
        for (String key : fieldMap.keySet()) {
            Object obj = fieldMap.get(key);
            try {
                Map<String, Object> test = UtilGenerics.<Map<String, Object>>cast(JSON.from(obj).toObject(Map.class));
                if (test.containsKey("_ENTITY_NAME_")) {
                    fieldMap.put(key, jsonToGenericConverter.convert(delegator.getDelegatorName(), JSON.from(obj)));
                } else {
                    fieldMap.put(key, test);
                }
            } catch (IOException | ConversionException ignored) {
            }
        }
        try {
            return JSON.from(dispatcher.runSync(serviceName, fieldMap)).toString();
        } catch (GenericServiceException e) {
            return "{\n    \"error\": \"Service method exception.\"\n}";
        } catch (IOException | NullPointerException e) {
            return "{\n    \"error\": \"Service result conversion to JSON failed.\"\n}";
        }
    }
}
