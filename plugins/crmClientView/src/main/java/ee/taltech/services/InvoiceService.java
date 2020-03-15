package ee.taltech.services;

import org.apache.camel.Exchange;
import org.apache.camel.component.sparkrest.SparkMessage;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.condition.EntityCondition;
import org.apache.ofbiz.entity.condition.EntityOperator;
import org.apache.ofbiz.service.DispatchContext;

import java.util.List;
import java.util.Map;

public class InvoiceService {


    private DispatchContext dctx;
    private Delegator delegator;

    public InvoiceService(DispatchContext dctx) {
        this.dctx = dctx;
        delegator = dctx.getDelegator();
    }

    public List<GenericValue> getContactList() {
        try {
            return delegator.findAll("Invoice", true);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String deleteInvoice(Exchange exchange) {
        try {
            String invoiceId = getParamValueFromExchange("id", exchange);
            EntityCondition condition = EntityCondition.makeCondition(
                    "invoiceId", EntityOperator.EQUALS, invoiceId);
            delegator.removeByCondition("Invoice", condition);

            return "200";

        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return "failed";
    }

    private String getParamValueFromExchange(String paramName, Exchange exchange) {
        SparkMessage msg = (SparkMessage) exchange.getIn();
        Map<String, String> params = msg.getRequest().params();
        String sparkParamName = ":" + paramName;
        return params.get(sparkParamName);
    }
}
