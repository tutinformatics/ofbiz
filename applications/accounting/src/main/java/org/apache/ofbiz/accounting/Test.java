package org.apache.ofbiz.accounting;

import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.ServiceUtil;

import java.util.Map;

public class Test {
    public static Map<String, Object> testTest(DispatchContext par1, Map<String, ?> par2) {
        Delegator delegator = par1.getDelegator();
        Map<String, Object> suc = ServiceUtil.returnSuccess();
        GenericValue gvVal = delegator.makeValue("TestEntity");
        gvVal.set("field1", par2.get("lol1"));
        gvVal.set("field2", par2.get("lol2"));
        try {
            gvVal.create();
        } catch (GenericEntityException e) {
            ServiceUtil.returnError("Error: " + e.toString());
        }
        suc.put("lol3", "Hello" + par2.get("lol1"));
        return suc;
    }
}
