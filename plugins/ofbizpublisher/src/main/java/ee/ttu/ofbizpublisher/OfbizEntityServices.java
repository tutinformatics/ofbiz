package ee.ttu.ofbizpublisher;

import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.ServiceUtil;

import java.util.Arrays;
import java.util.Map;

public class OfbizEntityServices {

    public static final String module = OfbizPublisherServices.class.getName();

    public Map<String, Object> createOfbizEntity(Delegator delegator, Map<String, ? extends Object> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        try {
            Debug.log("[DEBUG]: " + context.toString() + context.keySet() + Arrays.toString(context.values().toArray()));
            GenericValue ofbizEntity = delegator.makeValue("OfbizEntity");
            // Auto generating next sequence of OfbizPublisherId primary key
            ofbizEntity.setNextSeqId();
            // Setting up all non primary key field values from context map
            ofbizEntity.setNonPKFields(context);
            // Creating record in database for OfbizPublisher entity for prepared value
            ofbizEntity = delegator.create(ofbizEntity);
            result.put("OfbizEntityId", ofbizEntity.getString("OfbizEntityId"));

            Debug.log("Started OfbizEntityService...");
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            Debug.log("Starting OfbizEntityService Failed...");
            return ServiceUtil.returnError("Error in creating record in OfbizEntity entity ........" + module);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
