package ee.taltech.services;

import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.service.DispatchContext;

import java.util.List;

public class SalesOpportunityService {

    private DispatchContext dctx;
    private Delegator delegator;

    public SalesOpportunityService(DispatchContext dctx) {
        this.dctx = dctx;
        delegator = dctx.getDelegator();
    }

    public List<GenericValue> getSalesOpportunityList() {
        try {
            return delegator.findAll("SalesOpportunity", true);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return null;
    }
}
