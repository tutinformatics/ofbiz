package ee.taltech;

import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.ServiceUtil;

import java.util.Map;

public class TestService {

    public static final String module = TestService.class.getName();

    public static Map<String, Object> createTestEntity(DispatchContext dctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        Delegator delegator = dctx.getDelegator();
        try {
            GenericValue testEntity = delegator.makeValue("testEntity");
            // Auto generating next sequence of id primary key
            testEntity.setNextSeqId();
            // Setting up all non primary key field values from context map
            testEntity.setNonPKFields(context);
            // Creating record in database for testEntity entity for prepared value
            testEntity = delegator.create(testEntity);
            result.put("id", testEntity.getString("id"));
            Debug.log("==========This is my first Java Service implementation in Apache OFBiz. testEntity record created successfully with id:"+testEntity.getString("id"));

        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError("Error in creating record in OfbizDemo entity ........" +module);
        }
        return result;
    }
}
