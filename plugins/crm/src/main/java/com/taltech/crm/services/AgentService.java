package com.taltech.crm.services;

import com.taltech.crm.services.converter.Converter;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.Converters;
import org.apache.ofbiz.service.DispatchContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AgentService {

    private DispatchContext dctx;
    private Delegator delegator;
    public static final Converters.JSONToGenericValue jsonToGenericConverter = new Converters.JSONToGenericValue();

    public AgentService(DispatchContext dctx) {
        this.dctx = dctx;
        delegator = dctx.getDelegator();
    }

    public List<GenericValue> getAgents() {
        try {
            return delegator.findAll("agent", true);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void createAgent(Map<String, Object> data) {
        try {
            Optional<GenericValue> order = Converter.mapToGenericValue(delegator, "agent", data);
            if (order.isPresent()) {
                order.get().setNextSeqId();
                delegator.createOrStore(order.get());
                Map<String, Object> roleData = new HashMap<>();
                roleData.put("agentId", order.get().get("agentId"));
                roleData.put("customerId", order.get().get("customerId"));
                roleData.put("name", order.get().get("name"));
                roleData.put("telecomNumber", order.get().get("telecomNumber"));
                roleData.put("emailAddress", order.get().get("emailAddress"));
                Optional<GenericValue> orderRole = Converter.mapToGenericValue(delegator, "agent", roleData);
                if (orderRole.isPresent()) {
                    delegator.createOrStore(orderRole.get());
                }
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
    }
}
