package ee.taltech.servicesmgt;

import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.collections.PagedList;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.transaction.GenericTransactionException;
import org.apache.ofbiz.entity.transaction.TransactionUtil;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.jersey.resource.ProjectResource;

public class VsBridgeServices {
    public static final String module = ProjectResource.class.getName();

    public static PagedList<GenericValue> getPagedList(Delegator delegator, String entity, Integer page, Integer pageSize) {
        boolean beganTransaction = false;
        PagedList<GenericValue> resultPage = null;
        try {
            beganTransaction = TransactionUtil.begin();
            resultPage = EntityQuery.use(delegator)
                    .from(entity)
                    .cursorScrollInsensitive()
                    .queryPagedList(page, pageSize);
            TransactionUtil.commit(beganTransaction);
            return resultPage;
        } catch (GenericEntityException e) {
            Debug.logError(e.getMessage(), module);
            try {
                TransactionUtil.rollback(beganTransaction, "Failed to get pagedlist.", e);
            } catch (GenericTransactionException gte2) {
                Debug.logError(gte2, "Unable to rollback transaction", module);
            }
        }
        return resultPage;
    }
}
