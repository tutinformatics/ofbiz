package ee.taltech.services;

import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.service.DispatchContext;

import java.util.List;

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

}
