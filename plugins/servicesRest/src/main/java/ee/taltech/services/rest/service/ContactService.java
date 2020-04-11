package ee.taltech.services.rest.service;

import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.party.contact.ContactMechServices;
import org.apache.ofbiz.service.DispatchContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ContactService {
    private Delegator delegator;
    GenericValue userLogin;
    DispatchContext dctx;

    public ContactService(DispatchContext dctx) {
        this.dctx = dctx;
        delegator = dctx.getDelegator();

        // Temporary solution until authentication exists
        try {
            userLogin = EntityQuery.use(delegator).from("UserLogin").where("userLoginId", "DEMO_CUSTOMER").queryOne();
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
    }

    public List<GenericValue> getContactMechs() {
        try {
            return EntityQuery.use(delegator).from("PartyContactMech").queryList();
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public Map<String, Object> addTelecomNumber(Map<String, Object> data) {
        /*
            <field name="countryCode" type="very-short"></field>
            <field name="areaCode" type="very-short"></field>
            <field name="contactNumber" type="short-varchar"></field>
            <field name="askForName" type="name"></field>
         */
        data.put("userLogin", userLogin);
        data.put("locale", Locale.getDefault());
        return ContactMechServices.createTelecomNumber(dctx, data);
    }
}
