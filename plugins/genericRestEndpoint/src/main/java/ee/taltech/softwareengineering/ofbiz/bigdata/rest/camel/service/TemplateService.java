package ee.taltech.softwareengineering.ofbiz.bigdata.rest.camel.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.camel.Exchange;
import org.apache.ofbiz.base.conversion.ConversionException;
import org.apache.ofbiz.base.lang.JSON;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.model.ModelEntity;
import org.apache.ofbiz.entity.model.ModelField;
import org.apache.ofbiz.entity.model.ModelReader;
import org.apache.ofbiz.entity.util.Converters;
import org.apache.ofbiz.entity.util.EntityQuery;

import javax.ws.rs.core.Response;
import java.util.*;
import java.util.stream.Collectors;

public class TemplateService {

    public static final String module = TemplateService.class.getName();
    // there probably is a slightly better way to do them
    public static final Converters.JSONToGenericValue jsonToGenericConverter = new Converters.JSONToGenericValue();
    public static final Converters.GenericValueToJSON genericToJsonConverter = new Converters.GenericValueToJSON();
    private static final ObjectMapper mapper = new ObjectMapper();
    public static Map<String, String> entityMap;
    protected static ModelReader modelReader;
    Delegator delegator;


    public TemplateService(Delegator delegator) throws GenericEntityException {
        this.delegator = delegator;
        modelReader = delegator.getModelReader();
        entityMap = modelReader.getEntityCache().entrySet().stream().collect(Collectors.toMap(x -> x.getKey().toLowerCase(), Map.Entry::getKey));
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
                json.append(",\n");
            } else {
                json.append("}");
            }
        }
        return json.toString();
    }


    /**
     * Generate GraphQL Schema
     * <p>
     * modelReader.getEntityCache().entrySet() - entity set
     *
     * @return
     */
    public Object getSchema(Exchange exchange) {
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
                        .append(generateDepthOneJsonFromModelEntity(map.getValue()));

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

    /**
     * Example service of how to use a GET request.
     *
     * @return JSON String of Invoice list
     * @author probably Tavo
     * @see framework/entity/src/main/java/org/apache/ofbiz/entity/util/Converters.java
     */
    public String getInvoices() {
        // list of GenericValues, which are generic versions of entities
        List<GenericValue> orderItems = new ArrayList<>();
        // Gson for converting to json, you can also use built-in genericToJsonConverter, but in that case
        // a convertNoName method must be used to not get "_DELEGATOR_NAME_" and "_ENTITY_NAME_" fields put onto them
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try {
            // EntityQuery is a wrapper class for delegator to be able to build nice queries for entities.
            // The delegator determines which database to connect to, it is defined in the plugin ofbiz-component.xml.
            // If none is defined there, the "default" will be used.
            // It isn't something you have to pay attention to, but just know that creating a delegator for "default"
            // configuration manually is not a good idea. If you need a delegator, you should be able to get it from
            // somewhere, localdispatcher in this case in templateRoute class.
            orderItems = EntityQuery.use(delegator)
                    .from("Invoice")    // "Invoice" is name of the entity defined in datamodel component under applications/
                    .queryList();       // execute
        } catch (GenericEntityException e) {
            e.printStackTrace();
            GenericValue error = new GenericValue();
            error.put("Error", e);
            orderItems.add(error);
        }
        // convert to json and send them off
        return gson.toJson(orderItems);
    }

    public String getAll(Exchange exchange) {
        String entity = exchange.getIn().getHeader("entity").toString();
        entity = entityMap.get(entity);
        if (entity == null) {
            return "{\n    \"error\": \"No such entity.\"\n}";
        }

        List<GenericValue> orderItems = new ArrayList<>();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

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
        return gson.toJson(orderItems);
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

    /**
     * POST example
     *
     * @param json String form of an entity
     * @return response to say if success or not
     */
    public Response createInvoice(String json) {
        try {
            // uses custom method in the converter class that takes in delegator name, entity name and json
            // and spits out a GenericValue.
            // The converter "default" method with just GenericValue input wants the object to contain
            // _ENTITY_NAME_ and _DELEGATOR_NAME_ fields to be able to do the conversion.
            GenericValue object = jsonToGenericConverter.convert(delegator.getDelegatorName(), "Invoice", JSON.from(json));
            // incrementing the primary key ID, ofbiz takes care of it if PK is just one field
            object.setNextSeqId();
            // uses delegator's create() method that takes in a GenericValue and saves it into DB
            // it knows where to save it because genericvalue object knows what entity it is and what delegator it must use
            delegator.create(object);
            return Response.ok().type("application/json").build();
        } catch (GenericEntityException | ConversionException e) {
            e.printStackTrace();
            return Response.serverError().entity("Error of some sort").build();
        }

//		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
//		LocalDispatcher dispatcher = ServiceDispatcher.getLocalDispatcher("default", delegator);
    }
}
