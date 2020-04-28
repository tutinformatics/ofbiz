package org.apache.ofbiz.product.category;

import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

import java.util.Map;

public class BigBuyCategoryAdd {
    public static Map<String, Object> addCategoryAndProducts(DispatchContext ctx, Map<String, Object> context) {

        LocalDispatcher dispatcher = ctx.getDispatcher();
        String productCategoryTypeId = (String) context.get("productCategoryTypeId");
        context.put("productCategoryId", productCategoryTypeId);
        context.put("productCategoryTypeId", "CATALOG_CATEGORY");

        //productTypeId == GOOD

        Map<String,Object> success = ServiceUtil.returnSuccess();

        try {
            dispatcher.runSync("createProductCategory", context);
        } catch (GenericServiceException e){
            System.err.println(e.getMessage());
        }

        return success;
    }
}
