package ee.taltech.services.rest.service;

import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.party.contact.ContactHelper;
import org.apache.ofbiz.party.contact.ContactMechServices;
import org.apache.ofbiz.party.party.PartyServices;
import org.apache.ofbiz.party.party.PartyWorker;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.ServiceUtil;

import java.util.*;

public class ContactService {
    private Delegator delegator;
    GenericValue userLogin;
    GenericValue party;
    DispatchContext dctx;

    public ContactService(DispatchContext dctx) {
        this.dctx = dctx;
        delegator = dctx.getDelegator();

        // Temporary solution until authentication exists
        try {
            userLogin = PartyWorker.findPartyLatestUserLogin("DEMO_CUSTOMER", delegator);
            party = PartyWorker.findParty(delegator, "DEMO_CUSTOMER");
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
    }

    private Map<String, Object> loggedIn(Map<String, Object> data) {
        data.put("userLogin", userLogin);
        data.put("locale", Locale.getDefault());
        data.put("partyId", "DEMO_CUSTOMER");
        return data;
    }

    public GenericValue getPersonData() {
        return (GenericValue) PartyServices
                .getPerson(dctx, loggedIn(UtilMisc.toMap()))
                .getOrDefault("lookupPerson", GenericValue.NULL_VALUE);
    }

    public Map<String, Object> updatePersonData(Map<String, Object> data) {
        try {
            return dctx.getDispatcher().runSync("updatePerson", loggedIn(data));
        } catch (GenericServiceException e) {
            e.printStackTrace();
            return ServiceUtil.returnError(e.getMessage());
        }
    }

    public Collection<GenericValue> getPartyEmails() {
        return getContactMechsByType("EMAIL_ADDRESS");
    }

    public Map<String, Object> createPartyEmail(Map<String, Object> data) {
        try {
            return dctx.getDispatcher().runSync("createPartyEmailAddress", loggedIn(data));
        } catch (GenericServiceException e) {
            e.printStackTrace();
            return ServiceUtil.returnError(e.getMessage());
        }
    }

    public Map<String, Object> updatePartyEmail(Map<String, Object> data) {
        return ContactMechServices.updateEmailAddress(dctx, loggedIn(data));
    }

    public Collection<GenericValue> getContactMechs() {
        return ContactHelper.getContactMech(party, false);
    }

    public Collection<GenericValue> getContactMechsByType(String typeId) {
        return ContactHelper.getContactMechByType(party, typeId, false);
    }

    public Map<String, Object> deleteContactMech(Map<String, Object> data) {
        return ContactMechServices.deleteContactMech(dctx, loggedIn(data));
    }

    public List<GenericValue> getPartyTelephones() {
        try {
            Collection<GenericValue> mechs = getContactMechsByType("TELECOM_NUMBER");
            List<GenericValue> result = new ArrayList<>();
            for (GenericValue mech : mechs) {
                result.add(mech.getRelatedOne("TelecomNumber", false));
            }
            return result;
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<String, Object> createPartyTelephone(Map<String, Object> data) {
        return ContactMechServices.createTelecomNumber(dctx, loggedIn(data));
    }

    public Map<String, Object> updatePartyTelephone(Map<String, Object> data) {
        return ContactMechServices.updateTelecomNumber(dctx, loggedIn(data));
    }

    public List<GenericValue> getPostalAddresses() {
        try {
            Collection<GenericValue> mechs = getContactMechsByType("POSTAL_ADDRESS");
            List<GenericValue> result = new ArrayList<>();
            for (GenericValue mech : mechs) {
                result.add(mech.getRelatedOne("PostalAddress", false));
            }
            return result;
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<String, Object> createPostalAddress(Map<String, Object> data) {
        return ContactMechServices.createPostalAddress(dctx, loggedIn(data));
    }

    public Map<String, Object> updatePostalAddress(Map<String, Object> data) {
        return ContactMechServices.updatePostalAddress(dctx, loggedIn(data));
    }

    public Map<String, Object> getAllContactData() {
        return UtilMisc.toMap(
                "person", getPersonData(),
                "email", getPartyEmails(),
                "telephone", getPartyTelephones(),
                "address", getPostalAddresses()
        );
    }

}
