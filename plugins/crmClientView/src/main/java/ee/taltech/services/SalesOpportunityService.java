package ee.taltech.services;

import org.apache.ofbiz.base.conversion.ConversionException;
import org.apache.ofbiz.base.lang.JSON;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.Converters;
import org.apache.ofbiz.service.DispatchContext;

import javax.ws.rs.core.Response;
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
    public Response createSale() {
        try {
//            delegator.create(new Converters.JSONToGenericValue().convert("", JSON.from(json)));
            delegator.create("SalesOpportunity", UtilMisc.toMap("salesOpportunityId","opportunityName","description", "estimatedAmount", "estimatedProbability"));
            return Response.ok().type("application/json").build();
        } catch (GenericEntityException e) {
            e.printStackTrace();
            return Response.serverError().entity("Error of some sort").build();
        }
    }
}
