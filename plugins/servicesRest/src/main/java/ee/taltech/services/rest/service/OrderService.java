package ee.taltech.services.rest.service;

import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.DispatchContext;

import java.util.*;

public class OrderService {
    private Delegator delegator;

    public OrderService(DispatchContext dctx) {
        delegator = dctx.getDelegator();
    }

    public List<GenericValue> getOrdersByParty(String partyId) {
        try {
            List<GenericValue> orderIds = EntityQuery.use(delegator)
                    .from("OrderRole")
                    .where("partyId", partyId)
                    .queryList();
            Set<GenericValue> result = new HashSet<>();
            for (GenericValue orderId : orderIds) {
                result.addAll(
                        EntityQuery.use(delegator)
                                .from("OrderHeader")
                                .where("orderId", orderId.get("orderId"))
                                .queryList()
                );
            }
            return new ArrayList<>(result);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public void createOrder(String partyId, Map<String, Object> data) {
/*        List<String> productIds = new ArrayList<>();
        if (data.containsKey("productIds")) {
            productIds = (ArrayList<String>) data.get("productIds");
            data.remove("productIds");
        }*/
        try {
            Optional<GenericValue> order = Converter.mapToGenericValue(delegator, "OrderHeader", data);
            if (order.isPresent()) {
                order.get().setNextSeqId();
                delegator.createOrStore(order.get());

                Map<String, Object> roleData = new HashMap<>();
                roleData.put("orderId", order.get().get("orderId"));
                roleData.put("partyId", partyId);
                roleData.put("roleTypeId", "END_USER_CUSTOMER");
                Optional<GenericValue> orderRole = Converter.mapToGenericValue(delegator, "OrderRole", roleData);

                if (orderRole.isPresent()) {
                    delegator.createOrStore(orderRole.get());
                }
/*              !TODO doesnt work, Cannot setNextSeqId for entity [OrderItem] that does not have a single primary key field
                for (String productId : productIds) {
                    Map<String, Object> itemData = new HashMap<>();
                    itemData.put("productId", productId);
                    itemData.put("orderId", order.get().get("orderId"));
                    Optional<GenericValue> orderItem = Converter.mapToGenericValue(delegator, "OrderItem", itemData);
                    if(orderItem.isPresent()) {
                        orderItem.get().setNextSeqId();
                        delegator.createOrStore(orderItem.get());
                    }
                }*/
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
    }

    public GenericValue getOrderById(String orderId) {
        try {
            return EntityQuery.use(delegator)
                    .from("OrderHeader")
                    .where("orderId", orderId)
                    .queryOne();
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<GenericValue> updateOrder(String orderId, Map<String, Object> data) {
        try {
            List<GenericValue> target = EntityQuery.use(delegator)
                    .from("OrderHeader")
                    .where("orderId", orderId)
                    .queryList();
            for (GenericValue genericValue : target) {
                genericValue.setNonPKFields(data);
                genericValue.store();
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
