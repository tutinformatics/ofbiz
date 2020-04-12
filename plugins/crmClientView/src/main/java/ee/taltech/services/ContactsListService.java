package ee.taltech.services;

import org.apache.camel.Exchange;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.condition.EntityCondition;
import org.apache.ofbiz.entity.condition.EntityOperator;
import org.apache.ofbiz.entity.util.EntityFindOptions;
import org.apache.ofbiz.service.DispatchContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ContactsListService {


    private DispatchContext dctx;
    private Delegator delegator;



    public ContactsListService(DispatchContext dctx) {
        this.dctx = dctx;
        delegator = dctx.getDelegator();
    }
    public List<GenericValue> getContactList() {
        try {
            return delegator.findAll("PersonData", true);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return null;
    }
    public String getByName(Exchange exchange) {
        try {
            String invoiceId = Utils.getParamValueFromExchange("name", exchange);
            EntityCondition condition = EntityCondition.makeCondition(
                    "firstName", EntityOperator.EQUALS, invoiceId);
            List<GenericValue> result = delegator.findList("PersonData", condition, null, List.of("creationDate"), new EntityFindOptions(), true);
            if (result.size() > 0) {

                return String.valueOf(result.get(0));
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return null;
    }
    public String getById(Exchange exchange) {
        try {
            String invoiceId = Utils.getParamValueFromExchange("id", exchange);
            EntityCondition condition = EntityCondition.makeCondition(
                    "memberId", EntityOperator.EQUALS, invoiceId);
            List<GenericValue> result = delegator.findList("PersonData", condition, null, List.of("firstName"), new EntityFindOptions(), true);
            if (result.size() > 0) {
                return String.valueOf(result.get(0));
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void createContact(Map<String, Object> data) {
        try {
            Optional<GenericValue> contactList = Utils.mapToGenericValue(delegator, "PersonData", data);
            if (contactList.isPresent()) {
                contactList.get().setNextSeqId();
                delegator.createOrStore(contactList.get());
                Map<String, Object> dataMap = new HashMap<>();
                dataMap.put("partyId", contactList.get().get("partyId"));
                dataMap.put("memberId", contactList.get().get("memberId"));
                dataMap.put("firstName", contactList.get().get("firstName"));
                dataMap.put("lastName", contactList.get().get("lastName"));
                Optional<GenericValue> saleRole = Utils.mapToGenericValue(delegator, "PersonData", dataMap);
                if (saleRole.isPresent()) {
                    delegator.createOrStore(saleRole.get());
                }
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
    }





}
