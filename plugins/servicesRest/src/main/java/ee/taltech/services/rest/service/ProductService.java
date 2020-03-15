package ee.taltech.services.rest.service;

import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.model.ModelEntity;
import org.apache.ofbiz.entity.model.ModelRelation;
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
        try {
            return delegator.findByAnd("Product",
                    Map.of("productId", productId),
                    List.of("productId"),
                    true);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public GenericValue addProduct(Map<String, Object> data) {
        try {
            Optional<GenericValue> product = Converter.mapToGenericValue(delegator, "Product", data);
            if (product.isPresent()) {
                product.get().setNextSeqId();
                delegator.createOrStore(product.get());
                return product.get();
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteProduct(String productId) {
        try {
            List<GenericValue> result =  delegator.findByAnd("Product",
                    Map.of("productId", productId),
                    List.of("productId"),
                    true);
            // !TODO Doesn't work with foreign keys
            delegator.removeByAnd("Product", Map.of("productId", productId));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
    }


}
