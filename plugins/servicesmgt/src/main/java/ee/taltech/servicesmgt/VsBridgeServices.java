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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VsBridgeServices {
    public static final String module = VsBridgeServices.class.getName();

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

    private static List<Map<String, Object>> getAttributeOptions(GenericValue feature) {
        try {
            List<GenericValue> relatedList = feature.getRelated("ProductFeature",null, null, false);
            List<Map<String, Object>> options = new ArrayList<>();
            for (GenericValue related : relatedList) {
                Object value = related.get("numberSpecified");
                options.add(UtilMisc.toMap("label", related.get("description"),
                        "value", value == null ? related.get("defaultSequenceNum") : value // Todo: ensure some value exists
                ));
            }
            return options;
        } catch (GenericEntityException e) {
            Debug.logError(e.getMessage(), module);
            return null;
        }
    }

    public static List<Map<String, Object>> convertFeaturesToVsAttributes(List<GenericValue> features) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (GenericValue feature : features) {
            result.add(UtilMisc.toMap(
                    "id", feature.get("productFeatureCategoryId"),
                    "attribute_code", ((String) feature.get("description")).toLowerCase().replace(" ", "_"),
                    "frontend_label", feature.get("description"),
                    "frontend_input", "select",
                    "is_user_defined", true,
                    "is_visible", true, // decide based on fromDate thruDate in ProductFeatureAppl?
                    "options", VsBridgeServices.getAttributeOptions(feature)
            ));
        }
        return result;
    }

    public static List<Map<String, Object>> convertCategoriesToVsCategories(List<GenericValue> categories) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (GenericValue category : categories) {
            result.add(UtilMisc.toMap(
                    "id", category.get("productCategoryId"),
                    "parent_id", category.get("primaryParentCategoryId"),
                    "path", "ids/of/all/parents",
                    "name", category.get("categoryName"),
                    "url_key", "words-in-name-ID",
                    "url_path", "some/url/ending/with/url_key",
                    "is_active", true,
                    "position", 1, // possibly use some sequencenum?
                    "level", 2, // by default lvl 2 is in main menu
                    "product_count", 0,
                    "children_data", new ArrayList<>() // list of child categories with their children_data and so on
            ));
        }
        return result;
    }

    public static List<Map<String, Object>> convertProductsToVsProducts(List<GenericValue> products) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (GenericValue product : products) {
            result.add(UtilMisc.toMap(
                    "id", product.get("productId"),
                    "name", product.get("productName"),
                    "image", product.get("mediumImageUrl"),
                    "sku", product.get("productId"),
                    "url_path", "product/" + product.get("productId"),
                    "type_id", "simple",
                    "price", 10,
                    "status", 1,
                    "visbility", 4,
                    "category_ids", new ArrayList<>(),
                    "stock", UtilMisc.toMap("is_in_stock", true, "qty", 1)
            ));
        }
        return result;
    }
}
