package ee.taltech.services.rest.service;

import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.service.DispatchContext;

import java.util.List;

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
        return null;
    }
}
