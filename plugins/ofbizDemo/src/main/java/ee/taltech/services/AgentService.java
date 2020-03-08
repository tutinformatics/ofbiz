package ee.taltech.services;

import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.service.DispatchContext;

import java.util.List;

public class AgentService {
    private DispatchContext dctx;
    private Delegator delegator;

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

}
