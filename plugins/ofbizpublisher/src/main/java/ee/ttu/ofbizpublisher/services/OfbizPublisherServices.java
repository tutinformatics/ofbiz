package ee.ttu.ofbizpublisher.services;

import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.ServiceUtil;

import java.util.Arrays;
import java.util.Map;

public class OfbizPublisherServices {

    public static final String module = OfbizPublisherServices.class.getName();

    public Map<String, Object> createOfbizPublisher(DispatchContext dctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        Delegator delegator = dctx.getDelegator();
        try {
            Debug.log("[DEBUG]: " + context.toString() + context.keySet() + Arrays.toString(context.values().toArray()));
            GenericValue ofbizPublisher = delegator.makeValue("OfbizPublisher");
            // Auto generating next sequence of OfbizPublisherId primary key
            ofbizPublisher.setNextSeqId();
            // Setting up all non primary key field values from context map
            ofbizPublisher.setNonPKFields(context);
            // Creating record in database for OfbizPublisher entity for prepared value
            ofbizPublisher = delegator.create(ofbizPublisher);
            result.put("OfbizPublisherId", ofbizPublisher.getString("OfbizPublisherId"));

            Debug.log("Started OfbizPublisherService...");
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            Debug.log("Starting OfbizPublisherService Failed...");
            return ServiceUtil.returnError("Error in creating record in OfbizPublisher entity ........" + module);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
