package services;

import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.ServiceUtil;

import java.util.Arrays;
import java.util.Map;

public class OfbizSubscriberServices {

    public static final String module = OfbizSubscriberServices.class.getName();

    public Map<String, Object> createOfbizSubscriber(DispatchContext dctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        Delegator delegator = dctx.getDelegator();
        try {
            Debug.log("[DEBUG]: " + context.toString() + context.keySet() + Arrays.toString(context.values().toArray()));
            GenericValue ofbizSubscriber = delegator.makeValue("OfbizSubscriber");
            // Auto generating next sequence of OfbizSubscriberId primary key
            ofbizSubscriber.setNextSeqId();
            // Setting up all non primary key field values from context map
            ofbizSubscriber.setNonPKFields(context);
            // Creating record in database for OfbizSubscriber entity for prepared value
            ofbizSubscriber = delegator.create(ofbizSubscriber);
            result.put("OfbizSubscriberId", ofbizSubscriber.getString("OfbizSubscriberId"));

            Debug.log("Started OfbizSubscriberService...");
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            Debug.log("Starting OfbizSubscriberService Failed...");
            return ServiceUtil.returnError("Error in creating record in OfbizSubscriber entity ........" + module);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
