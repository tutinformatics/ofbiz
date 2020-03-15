package ee.taltech.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.camel.Exchange;
import org.apache.camel.component.sparkrest.SparkMessage;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.condition.EntityCondition;
import org.apache.ofbiz.entity.condition.EntityOperator;
import org.apache.ofbiz.service.DispatchContext;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ContactsListService {


    private DispatchContext dctx;
    private Delegator delegator;
    private ObjectMapper objectMapper = new ObjectMapper();
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public ContactsListService(DispatchContext dctx) {
        this.dctx = dctx;
        delegator = dctx.getDelegator();
    }

    private String getParamValueFromExchange(String paramName, Exchange exchange) {
        SparkMessage msg = (SparkMessage) exchange.getIn();
        Map<String, String> params = msg.getRequest().params();
        String sparkParamName = ":" + paramName;
        return params.get(sparkParamName);
    }

    private <T> T getValueFromBody(Exchange exchange, Class<T> valueType) {
        SparkMessage msg = (SparkMessage) exchange.getIn();
        T o = null;
        try {
            o = objectMapper.readValue(msg.getBody().toString(), valueType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return o;
    }

    /*
    public String createContact(Exchange exchange) {
        Map<String, Object> context = new HashMap<>();
        AttributeWithId attributeWithId = getValueFromBody(exchange, AttributeWithId.class);
        context.put("partyId", attributeWithId.getPartyId());
        return gson.toJson(PartyServices.createAffiliate(dctx, context));
    }
    */

    public GenericValue createContact(Map<String, Object> data) {
        try {
            Optional<GenericValue> product = Converter.mapToGenericValue(delegator, "Product", data);
            if (product.isPresent()) {
                product.get().setNextSeqId();
                delegator.createOrStore(product.get());
                return product.get();
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<GenericValue> getContactList() {
        try {
            return delegator.findAll("Person", true);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteContact(Exchange exchange) {
        try {
            String name = getParamValueFromExchange("name", exchange);
            String capitalizedName = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();

            EntityCondition condition = EntityCondition.makeCondition(
                    "firstName", EntityOperator.EQUALS, name);
            //delegator.removeByAnd("Person",  UtilMisc.toMap("firstName", capitalizedName));
            delegator.removeByCondition("Person", condition);

        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
    }

    public List<GenericValue> getContactByFirstName(Exchange exchange) {
        try {
            String name = getParamValueFromExchange("name", exchange);
            //EntityEcaRuleRunner<?> ecaRunner = this.getEcaRuleRunner(modelEntity.getEntityName());
            String capitalizedName = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
            System.out.println(name);

            List<GenericValue> result = delegator.findByAnd("Person",  UtilMisc.toMap("firstName", capitalizedName),null , true);
            if (result.size() >= 1) {
                return result;
            }
            return null;
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return null;
    }

}
