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

public class OpportunityService {
    private DispatchContext dctx;
    private Delegator delegator;

    public OpportunityService(DispatchContext dctx) {
        this.dctx = dctx;
        delegator = dctx.getDelegator();
    }

    private String getParamValueFromExchange(String paramName, Exchange exchange) {
        SparkMessage msg = (SparkMessage) exchange.getIn();
        Map<String, String> params = msg.getRequest().params();
        String sparkParamName = ":" + paramName;
        return params.get(sparkParamName);
    }

    public List<GenericValue> getOpportunities() {
        try {
            return delegator.findAll("opportunity", true);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void deleteOpportunity(Exchange exchange) {
        try {
            String name = getParamValueFromExchange("opportunityId", exchange);
            String capitalizedName = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();

            EntityCondition condition = EntityCondition.makeCondition(
                    "opportunityId", EntityOperator.EQUALS, capitalizedName);
            //delegator.removeByAnd("Person",  UtilMisc.toMap("firstName", capitalizedName));
            delegator.removeByCondition("opportunity", condition);

        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
    }

    public void createOpportunity(Map<String, Object> data) {
        try {
            Optional<GenericValue> order = Converter.mapToGenericValue(delegator, "opportunity", data);
            if (order.isPresent()) {
                order.get().setNextSeqId();
                delegator.createOrStore(order.get());
                Map<String, Object> roleData = new HashMap<>();
                roleData.put("opportunityId", order.get().get("opportunityId"));
                roleData.put("pipelineId", order.get().get("pipelineId"));
                roleData.put("customerId", order.get().get("customerId"));
                roleData.put("contactId", order.get().get("contactId"));
                roleData.put("agentId", order.get().get("agentId"));
                roleData.put("name", order.get().get("name"));
                roleData.put("description", order.get().get("description"));
                roleData.put("stage", order.get().get("stage"));
                Optional<GenericValue> orderRole = Converter.mapToGenericValue(delegator, "opportunity", roleData);
                if (orderRole.isPresent()) {
                    delegator.createOrStore(orderRole.get());
                }
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
    }

}
