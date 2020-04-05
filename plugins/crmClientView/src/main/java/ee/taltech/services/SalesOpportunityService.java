package ee.taltech.services;

import org.apache.camel.Exchange;
import org.apache.camel.component.sparkrest.SparkMessage;
import org.apache.ofbiz.base.conversion.ConversionException;
import org.apache.ofbiz.base.lang.JSON;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.condition.EntityCondition;
import org.apache.ofbiz.entity.condition.EntityOperator;
import org.apache.ofbiz.entity.util.Converters;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.DispatchContext;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.*;

public class SalesOpportunityService {

    private DispatchContext dctx;
    private Delegator delegator;

    public SalesOpportunityService(DispatchContext dctx) {
        this.dctx = dctx;
        delegator = dctx.getDelegator();
    }
    public static final Converters.JSONToGenericValue convert = new Converters.JSONToGenericValue();

    public List<GenericValue> getSalesOpportunityList() {
        try {
            return delegator.findAll("SalesOpportunity", true);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void createSaleOpportunity(Map<String, Object> data) {
        try {
            Optional<GenericValue> salesOpportunity = mapToGenericValue(delegator, "SalesOpportunity", data);
            if (salesOpportunity.isPresent()) {
                salesOpportunity.get().setNextSeqId();
                delegator.createOrStore(salesOpportunity.get());
                Map<String, Object> role = new HashMap<>();
                role.put("salesOpportunityId", salesOpportunity.get().get("salesOpportunityId"));
                role.put("opportunityName", salesOpportunity.get().get("opportunityName"));
                role.put("description", salesOpportunity.get().get("description"));
                role.put("estimatedAmount", salesOpportunity.get().get("estimatedAmount"));
                role.put("estimatedProbability", salesOpportunity.get().get("estimatedProbability"));
                Optional<GenericValue> saleRole = mapToGenericValue(delegator, "SalesOpportunity", role);
                if (saleRole.isPresent()) {
                    delegator.createOrStore(saleRole.get());
                }
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
    }


    public void updateSaleOpportunity(String saleId, Map<String, Object> data) {
        try {
            List<GenericValue> upd = EntityQuery.use(delegator).from("SalesOpportunity").where("salesOpportunityId", saleId).queryList();
            for (GenericValue genericValue : upd) {
                genericValue.setNonPKFields(data);
                genericValue.store();
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
    }

    public String deleteSaleOpportunity(Exchange exchange) {
        try {
            String saleId = getParamValueFromExchange("id", exchange);
            EntityCondition condition = EntityCondition.makeCondition(
                    "salesOpportunityId", EntityOperator.EQUALS, saleId);
            delegator.removeByCondition("SalesOpportunity", condition);

            return "done";

        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return "failed";
    }
    public static Optional<GenericValue> mapToGenericValue(Delegator delegator, String entityName, Map<String, Object> data) {
        data.put("_DELEGATOR_NAME_", delegator.getDelegatorName());
        data.put("_ENTITY_NAME_", entityName);
        try {
            return Optional.of(convert.convert(JSON.from(data)));
        } catch (ConversionException | IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
    private String getParamValueFromExchange(String paramName, Exchange exchange) {
        SparkMessage msg = (SparkMessage) exchange.getIn();
        Map<String, String> params = msg.getRequest().params();
        String sparkParamName = ":" + paramName;
        return params.get(sparkParamName);
    }





}
