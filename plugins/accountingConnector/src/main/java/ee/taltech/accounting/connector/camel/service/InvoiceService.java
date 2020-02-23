package ee.taltech.accounting.connector.camel.service;

import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.ServiceUtil;

import java.util.List;
import java.util.Map;

public class InvoiceService {

    public static final String module = InvoiceService.class.getName();

    public Map<String, Object> getInvoices(DispatchContext dctx, Map<String, ?> context) {
        Delegator delegator = dctx.getDelegator();
        try {
            List<GenericValue> orderItems = EntityQuery.use(delegator)
                    .from("Invoice")
                    .queryList();
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }

        return ServiceUtil.returnSuccess();
    }
}
