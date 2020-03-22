package ee.taltech.services.rest.service;

import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.service.DispatchContext;

import java.util.*;

public class ProductService {
    private DispatchContext dctx;
    private Delegator delegator;

    public ProductService(DispatchContext dctx) {
        this.dctx = dctx;
        delegator = dctx.getDelegator();
    }

    public List<GenericValue> getProductList() {
        try {
            return delegator.findAll("Product", true);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public List<GenericValue> getProductById(String productId) {
        Map<String, String> key = new HashMap<>();
        key.put("productId", productId);
        try {
            return delegator.findByAnd("Product", key, Arrays.asList("productId"), true);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public List<GenericValue> getProductsByType(String typeId) {
        Map<String, String> key = new HashMap<>();
        key.put("productTypeId", typeId);
        try {
            return delegator.findByAnd("Product", key, Arrays.asList("productName"), true);
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
        Map<String, String> key = new HashMap<>();
        key.put("productId", productId);
        try {
            List<GenericValue> target = delegator.findByAnd("Product", key, Arrays.asList("productId"), false);
            for (GenericValue genericValue : target) {
                genericValue.setNonPKFields(data);
                genericValue.store();
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
    }


}
