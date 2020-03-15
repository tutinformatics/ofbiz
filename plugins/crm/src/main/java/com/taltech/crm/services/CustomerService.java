package com.taltech.crm.services;


import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.service.DispatchContext;

import java.util.List;

public class CustomerService {
    private DispatchContext dctx;
    private Delegator delegator;

    public CustomerService(DispatchContext dctx) {
        this.dctx = dctx;
        delegator = dctx.getDelegator();
    }
    public List<GenericValue> getCustomers() {
        try {
            return delegator.findAll("customer", true);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return null;
    }
}
