package ee.taltech.services.rest.service;

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

    public GenericValue getProductById(String productId) {
        try {
            return EntityQuery.use(delegator)
                    .from("Product")
                    .where("productId", productId)
                    .queryOne();
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return null;
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
        GenericValue toAdd = delegator.makeValue("Product", data);
        if (!data.containsKey("productId")) {
            toAdd.setNextSeqId();
        }
        try {
            delegator.create(toAdd);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
    }

    public void updateProduct(String productId, Map<String, Object> data) {
        try {
            GenericValue target = EntityQuery.use(delegator).from("Product").where("productId", productId).queryOne();
            target.setNonPKFields(data);
            target.store();
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
    }


}
