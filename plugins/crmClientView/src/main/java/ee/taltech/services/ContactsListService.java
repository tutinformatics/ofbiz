package ee.taltech.services;

import org.apache.ofbiz.base.util.UtilMisc;
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

    public List<GenericValue> getContactByFirstName(String name) {
        try {
            //EntityEcaRuleRunner<?> ecaRunner = this.getEcaRuleRunner(modelEntity.getEntityName());
            String capitalizedName = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
            System.out.println(name);
            List<GenericValue> result = delegator.findByAnd("Person",  UtilMisc.toMap("firstName", capitalizedName),null , true);
            if (result.size() >= 1) {
                return result;
            }
            return null;
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return null;
    }

}
