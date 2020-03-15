package ee.taltech.services;

import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.service.DispatchContext;

import java.util.List;

public class ContactsListService {


    private DispatchContext dctx;
    private Delegator delegator;

    public ContactsListService(DispatchContext dctx) {
        this.dctx = dctx;
        delegator = dctx.getDelegator();
    }

    public List<GenericValue> getContactList() {
        try {
            return delegator.findAll("Person", true);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return null;
    }
}
