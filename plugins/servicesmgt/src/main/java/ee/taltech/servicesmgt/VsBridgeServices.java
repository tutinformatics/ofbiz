package ee.taltech.servicesmgt;

import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.base.util.collections.PagedList;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.transaction.GenericTransactionException;
import org.apache.ofbiz.entity.transaction.TransactionUtil;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.jersey.resource.ProjectResource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    public static List<Map<String, Object>> attributeToVsfAttribute(List<GenericValue> attributes) {
        // Todo: reduce spaghett
        List<Map<String, Object>> result = new ArrayList<>();
        for (GenericValue attribute : attributes) {
            try {
                List<GenericValue> relateds = attribute.getRelated("ProductFeature",null, null, false);
                List<Map<String, Object>> options = new ArrayList<>();
                for (GenericValue related : relateds) {
                    Object value = related.get("numberSpecified");
                    options.add(UtilMisc.toMap("label", related.get("description"),
                    "value", value == null ? related.get("defaultSequenceNum") : value // still null sometimes
                    ));
                }
                result.add(UtilMisc.toMap(
                        "id", attribute.get("productFeatureCategoryId"),
                        "attribute_code", ((String) attribute.get("description")).toLowerCase().replace(" ", "_"),
                        "frontend_label", attribute.get("description"),
                        "frontend_input", "select",
                        "is_user_defined", true,
                        "is_visible", true, // decide based on fromDate thruDate in ProductFeatureAppl?
                        "options", options
                ));
            } catch (GenericEntityException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static List<Map<String, Object>> categoryToVsfCategory(List<GenericValue> categories) {
        return UtilMisc.toList(UtilMisc.toMap("name", "category"));
    }

    public static List<Map<String, Object>> productToVsfProduct(List<GenericValue> products) {
        return UtilMisc.toList(UtilMisc.toMap("name", "product"));
    }
}
