package com.taltech.crm.services;


import com.taltech.crm.services.converter.Converter;
import org.apache.camel.Exchange;
import org.apache.camel.component.sparkrest.SparkMessage;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.condition.EntityCondition;
import org.apache.ofbiz.entity.condition.EntityOperator;
import org.apache.ofbiz.service.DispatchContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CustomerService {
    private DispatchContext dctx;
    private Delegator delegator;

    public CustomerService(DispatchContext dctx) {
        this.dctx = dctx;
        delegator = dctx.getDelegator();
    }

    private String getParamValueFromExchange(String paramName, Exchange exchange) {
        SparkMessage msg = (SparkMessage) exchange.getIn();
        Map<String, String> params = msg.getRequest().params();
        String sparkParamName = ":" + paramName;
        return params.get(sparkParamName);
    }

    public List<GenericValue> getCustomers() {
        try {
            return delegator.findAll("customer", true);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void createCustomer(Map<String, Object> data) {
        try {
            Optional<GenericValue> order = Converter.mapToGenericValue(delegator, "customer", data);
            if (order.isPresent()) {
                order.get().setNextSeqId();
                delegator.createOrStore(order.get());
                Map<String, Object> roleData = new HashMap<>();
                roleData.put("customerId", order.get().get("customerId"));
                roleData.put("partyGroup", order.get().get("partyGroup"));
                roleData.put("postalAddress", order.get().get("postalAddress"));
                roleData.put("telecomNumber", order.get().get("telecomNumber"));
                roleData.put("emailAddress", order.get().get("emailAddress"));
                roleData.put("extension", order.get().get("extension"));
                roleData.put("partyId", order.get().get("partyId"));
                Optional<GenericValue> orderRole = Converter.mapToGenericValue(delegator, "customer", roleData);
                if (orderRole.isPresent()) {
                    delegator.createOrStore(orderRole.get());
                }
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
    }

    public void deleteCustomer(Exchange exchange) {
        try {
            String name = getParamValueFromExchange("customerId", exchange);
            String capitalizedName = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();

            EntityCondition condition = EntityCondition.makeCondition(
                    "customerId", EntityOperator.EQUALS, capitalizedName);
            //delegator.removeByAnd("Person",  UtilMisc.toMap("firstName", capitalizedName));
            delegator.removeByCondition("customer", condition);

        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
    }
}
