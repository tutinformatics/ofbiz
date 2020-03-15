package ee.taltech.marketing.affiliate.connector.camel.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ee.taltech.marketing.affiliate.connector.camel.restResponse.AttributeWithId;
import org.apache.camel.Exchange;
import org.apache.camel.component.sparkrest.SparkMessage;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.party.party.PartyServices;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.GenericDispatcherFactory;
import org.apache.ofbiz.service.LocalDispatcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PartyService {

    Delegator delegator;
    GenericDispatcherFactory genericDispatcherFactory;
    LocalDispatcher dispatcher;
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private DispatchContext myContext;
    private ObjectMapper objectMapper = new ObjectMapper();


    public PartyService(Delegator delegator) {
        this.delegator = delegator;
        this.genericDispatcherFactory = new GenericDispatcherFactory();
        this.dispatcher = genericDispatcherFactory.createLocalDispatcher("myDispatcher", delegator);
        this.myContext = new DispatchContext("myContext", null, dispatcher);
    }


    /**
     * @param exchange - request that is wrapped by camel with additional information added during routing
     * @return JSON with all the parties found with a given partyId
     */
    public String getPartyById(Exchange exchange) {
        String id = getParamValueFromExchange("id", exchange);

        DispatchContext myContext = new DispatchContext("myContext", null, dispatcher);
        Map<String, Object> context = new HashMap<>();
        context.put("idToFind", id);
        context.put("partyIdentificationTypeId", null);
        context.put("searchPartyFirst", null);
        context.put("searchAllIdContext", null);

        return gson.toJson(PartyServices.findPartyById(myContext, context));
    }

    public String createAffiliate(Exchange exchange) {
        Map<String, Object> context = new HashMap<>();
        AttributeWithId attributeWithId = getValueFromBody(exchange, AttributeWithId.class);
        context.put("partyId", attributeWithId.getPartyId());
        return gson.toJson(PartyServices.createAffiliate(myContext, context));
    }

    // an alternative way of fetching data
    public String getParties() {
        List<GenericValue> parties = new ArrayList<>();
        try {
            parties = EntityQuery.use(delegator)
                    .from("Party")
                    .queryList();
        } catch (GenericEntityException e) {
            e.printStackTrace();
            GenericValue error = new GenericValue();
            error.put("Error", e);
            parties.add(error);
        }
        return gson.toJson(parties);
    }


    /**
     * handled map = Map<ParameterName, ParameterValue>
     * this map can be found by the following path:
     * exchange -> in -> request -> params
     *
     * @param paramName - name of parameter provided with query in requested url
     * @param exchange  - request wrapped by camel
     * @return value of parameter
     */
    private String getParamValueFromExchange(String paramName, Exchange exchange) {
        SparkMessage msg = (SparkMessage) exchange.getIn();
        Map<String, String> params = msg.getRequest().params();
        String sparkParamName = ":" + paramName;
        return params.get(sparkParamName);
    }

    /**
     * handled map = Map<ParameterName, ParameterValue>
     * this map can be found by the following path:
     * exchange -> in -> request -> params
     *
     * @param exchange  - request wrapped by camel
     * @return value of field
     */
    private <T> T getValueFromBody(Exchange exchange, Class<T> valueType) {
        SparkMessage msg = (SparkMessage) exchange.getIn();
        Object o = null;
        try {
            o = objectMapper.readValue(msg.getBody().toString(), valueType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (T) o;
    }



}
