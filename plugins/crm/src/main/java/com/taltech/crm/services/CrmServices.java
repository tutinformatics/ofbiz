package com.taltech.crm.services;
import java.util.List;
import java.util.Map;

import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.ServiceUtil;

public class CrmServices {

    public static final String module = CrmServices.class.getName();

    public static Map<String, Object> createContact(DispatchContext dctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        Delegator delegator = dctx.getDelegator();
        try {
            GenericValue contact = delegator.makeValue("Contact");
            // Auto generating next sequence of ofbizDemoId primary key
            contact.setNextSeqId();
            // Setting up all non primary key field values from context map
            contact.setNonPKFields(context);
            // Creating record in database for OfbizDemo entity for prepared value
            contact = delegator.create(contact);
            result.put("ofbizDemoId", contact.getString("contactId"));
            Debug.log("==========This is my first Java Service implementation in Apache OFBiz. Contact record created successfully with contactId:"+contact.getString("contactId"));
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError("Error in creating record in OfbizDemo entity ........" +module);
        }
        return result;
    }

    // this doesn't work, returns empty json for some reason
    public static Map<String, Object> getContacts(DispatchContext dctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        Delegator delegator = dctx.getDelegator();
        try {
            result.put("tere", "tere");
            List<GenericValue> contact = delegator.findAll("Contact", false);
//            Debug.log("listi sisu oli " + ofbizDemo.toString());
//            result.put("list", ofbizDemo.toString());
//            Debug.log("==========This is my first Java Service implementation in Apache OFBiz. OfbizDemo record created successfully with ofbizDemoId:"+ofbizDemo.getString("ofbizDemoId"));
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError("Error in creating record in OfbizDemo entity ........" +module);
        }
        return result;
    }
}
