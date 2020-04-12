package ee.taltech.services;

import org.apache.camel.Exchange;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.condition.EntityCondition;
import org.apache.ofbiz.entity.condition.EntityOperator;
import org.apache.ofbiz.entity.util.EntityFindOptions;
import org.apache.ofbiz.service.DispatchContext;

import java.util.List;

//CRM can only view invoices, we cannot update/delete/create new ones
public class InvoiceService {

    private DispatchContext dctx;
    private Delegator delegator;

    public InvoiceService(DispatchContext dctx) {
        this.dctx = dctx;
        delegator = dctx.getDelegator();
    }

    public List<GenericValue> get() {
        try {
            return delegator.findAll("Invoice", true);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<GenericValue> getByPartyId(Exchange exchange) {
        try {
            String partyId = Utils.getParamValueFromExchange("id", exchange);
            EntityCondition condition = EntityCondition.makeCondition(
                    "partyId", EntityOperator.LIKE, Utils.capitalize(partyId));
            return delegator.findList("Invoice", condition, null, List.of("invoiceDate"), new EntityFindOptions(), true);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return null;
    }


}
