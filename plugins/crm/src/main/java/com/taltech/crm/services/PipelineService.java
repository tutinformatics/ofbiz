package com.taltech.crm.services;

import com.taltech.crm.services.converter.Converter;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.service.DispatchContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PipelineService {
    private DispatchContext dctx;
    private Delegator delegator;

    public PipelineService(DispatchContext dctx) {
        this.dctx = dctx;
        delegator = dctx.getDelegator();
    }
    public List<GenericValue> getPipelines() {
        try {
            return delegator.findAll("pipeline", true);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void createPipeline(Map<String, Object> data) {
        try {
            Optional<GenericValue> order = Converter.mapToGenericValue(delegator, "pipeline", data);
            if (order.isPresent()) {
                order.get().setNextSeqId();
                delegator.createOrStore(order.get());
                Map<String, Object> roleData = new HashMap<>();
                roleData.put("pipelineId", order.get().get("pipelineId"));
                roleData.put("userId", order.get().get("userId"));
                Optional<GenericValue> orderRole = Converter.mapToGenericValue(delegator, "pipeline", roleData);
                if (orderRole.isPresent()) {
                    delegator.createOrStore(orderRole.get());
                }
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
    }

}
