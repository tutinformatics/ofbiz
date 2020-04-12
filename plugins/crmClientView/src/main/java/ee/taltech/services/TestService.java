package ee.taltech.services;

import org.apache.camel.Exchange;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.condition.EntityCondition;
import org.apache.ofbiz.entity.condition.EntityOperator;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.DispatchContext;

import java.util.*;

public class TestService {

    private Delegator delegator;

    public TestService(DispatchContext dctx) {
        delegator = dctx.getDelegator();
    }

    public List<GenericValue> get() {
        try {
            return delegator.findAll("SalesOpportunity", true);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void create(Map<String, Object> data) {
        try {
            Optional<GenericValue> salesOpportunity = Utils.mapToGenericValue(delegator, "SalesOpportunity", data);
            if (salesOpportunity.isPresent()) {
                salesOpportunity.get().setNextSeqId();
                delegator.createOrStore(salesOpportunity.get());
                Map<String, Object> dataMap = new HashMap<>();
                dataMap.put("salesOpportunityId", salesOpportunity.get().get("salesOpportunityId"));
                dataMap.put("opportunityName", salesOpportunity.get().get("opportunityName"));
                dataMap.put("description", salesOpportunity.get().get("description"));
                dataMap.put("estimatedAmount", salesOpportunity.get().get("estimatedAmount"));
                dataMap.put("estimatedProbability", salesOpportunity.get().get("estimatedProbability"));
                Optional<GenericValue> saleRole = Utils.mapToGenericValue(delegator, "SalesOpportunity", dataMap);
                if (saleRole.isPresent()) {
                    delegator.createOrStore(saleRole.get());
                }
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
    }


    public void update(String saleId, Map<String, Object> data) {
        try {
            List<GenericValue> list = EntityQuery.use(delegator).from("SalesOpportunity").where("salesOpportunityId", saleId).queryList();
            for (GenericValue genericValue : list) {
                genericValue.setNonPKFields(data);
                genericValue.store();
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
    }

    public String delete(Exchange exchange) {
        try {
            String saleId = Utils.getParamValueFromExchange("id", exchange);
            EntityCondition condition = EntityCondition.makeCondition(
                    "salesOpportunityId", EntityOperator.EQUALS, saleId);
            delegator.removeByCondition("SalesOpportunity", condition);

            return "done";

        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return "failed";
    }

}
