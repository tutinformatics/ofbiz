package com.taltech.crm.services;


import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.service.DispatchContext;

import java.util.List;

public class OpportunityService {
    private DispatchContext dctx;
    private Delegator delegator;

    public OpportunityService(DispatchContext dctx) {
        this.dctx = dctx;
        delegator = dctx.getDelegator();
    }
    public List<GenericValue> getOpportunities() {
        try {
            return delegator.findAll("opportunity", true);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return null;
    }

}
