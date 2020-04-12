package ee.taltech.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.camel.Exchange;
import org.apache.camel.component.sparkrest.SparkMessage;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.condition.EntityCondition;
import org.apache.ofbiz.entity.condition.EntityOperator;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.DispatchContext;

import java.io.IOException;
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

    public void createContact(Map<String, Object> data) {
        try {
            Optional<GenericValue> contactList = Utils.mapToGenericValue(delegator, "PersonData", data);
            if (contactList.isPresent()) {
                contactList.get().setNextSeqId();
                delegator.createOrStore(contactList.get());
                Map<String, Object> dataMap = new HashMap<>();
                dataMap.put("partyId", contactList.get().get("partyId"));
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
