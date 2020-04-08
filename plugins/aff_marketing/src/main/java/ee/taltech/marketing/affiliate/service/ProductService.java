package ee.taltech.marketing.affiliate.service;

import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.DispatchContext;

import java.util.*;

public class ProductService {
    private Delegator delegator;

    public ProductService(DispatchContext dctx) {
        delegator = dctx.getDelegator();
    }

    public List<GenericValue> getProductList() {
        try {
            return EntityQuery.use(delegator)
                    .from("Product")
                    .queryList();
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public List<GenericValue> getProductById(String productId) {
        try {
            return EntityQuery.use(delegator)
                    .from("Product")
                    .where("productId", productId)
                    .queryList();
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public List<GenericValue> getProductsByParentType(String typeId) {
        try {
            // !TODO how to query using multiple tables?
            List<GenericValue> subTypes = EntityQuery.use(delegator)
                    .from("ProductType")
                    .where("parentTypeId", typeId)
                    .queryList();
            Set<GenericValue> result = new HashSet<>();
            for (GenericValue subType : subTypes) {
                result.addAll(
                        EntityQuery.use(delegator)
                                .from("Product")
                                .where("productTypeId", subType.get("productTypeId"))
                                .queryList()
                );
            }
            return new ArrayList<>(result);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public void addProduct(Map<String, Object> data) {
        try {
            Optional<GenericValue> product = Converter.mapToGenericValue(delegator, "Product", data);
            if (product.isPresent()) {
                delegator.createOrStore(product.get());
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
    }

    public void updateProduct(String productId, Map<String, Object> data) {
        try {
            List<GenericValue> target = EntityQuery.use(delegator)
                    .from("Product")
                    .where("productId", productId)
                    .queryList();
            for (GenericValue genericValue : target) {
                genericValue.setNonPKFields(data);
                genericValue.store();
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
    }


}
