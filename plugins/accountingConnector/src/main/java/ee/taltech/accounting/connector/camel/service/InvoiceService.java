package ee.taltech.accounting.connector.camel.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

    public String getInvoices() {
        List<GenericValue> orderItems;
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            orderItems = EntityQuery.use(delegator)
                    .from("Invoice")
                    .queryList();
            String json =  gson.toJson(orderItems);
            return json;
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return null;
    }
}
