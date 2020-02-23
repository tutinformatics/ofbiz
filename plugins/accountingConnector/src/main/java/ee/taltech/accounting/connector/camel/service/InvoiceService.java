package ee.taltech.accounting.connector.camel.service;

import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;

import java.util.List;

public class InvoiceService {

    Delegator delegator;

    public InvoiceService(Delegator delegator) {
        this.delegator = delegator;
    }

    public static final String module = InvoiceService.class.getName();

    //@Deprecated
    /*public Map<String, Object> getInvoices(DispatchContext dctx, Map<String, ?> context) {
        Delegator delegator = dctx.getDelegator();
        try {
            List<GenericValue> orderItems = EntityQuery.use(delegator)
                    .from("Invoice")
                    .queryList();
            System.out.println(orderItems);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }

        return ServiceUtil.returnSuccess();
    }*/

    public List<?> getInvoices() {
        try {
            List<GenericValue> orderItems = EntityQuery.use(delegator)
                    .from("Invoice")
                    .queryList();
            return orderItems;
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }

        return null; //TODO: Error to json
    }
}
