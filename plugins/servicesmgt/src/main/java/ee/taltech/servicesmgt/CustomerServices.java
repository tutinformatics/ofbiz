package ee.taltech.servicesmgt;

import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.party.contact.ContactHelper;
import org.apache.ofbiz.party.party.PartyServices;
import org.apache.ofbiz.party.party.PartyWorker;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.GenericServiceException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CustomerServices {
    private static GenericValue getParty(DispatchContext dctx, Map<String, Object> context) {
        try {
            return PartyWorker.findParty(dctx.getDelegator(), (String) context.get("partyId"));
        } catch (GenericEntityException e) {
            e.printStackTrace();
            return GenericValue.NULL_VALUE;
        }
    }

    private static GenericValue getUserLogin(DispatchContext dctx, Map<String, Object> context) {
        return PartyWorker.findPartyLatestUserLogin((String) context.get("partyId"), dctx.getDelegator());
    }

    private static Map<String, Object> runServiceLoggedIn(String serviceName, DispatchContext dctx, Map<String, Object> context) {
        context.put("userLogin", getUserLogin(dctx, context));
        try {
            return dctx.getDispatcher().runSync(serviceName, context);
        } catch (GenericServiceException e) {
            e.printStackTrace();
        }
        return context;
    }

    private static List<GenericValue> getRelatedContactMechs(Collection<GenericValue> mechs, String relation) {
        List<GenericValue> result = new ArrayList<>();
        for (GenericValue mech : mechs) {
            try {
                result.add(mech.getRelatedOne(relation, false));
            } catch (GenericEntityException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static Map<String, Object> getAllContactInfo(DispatchContext dctx, Map<String, Object> context) {
        GenericValue party = getParty(dctx, context);

        Object person = PartyServices.getPerson(dctx, context)
                .getOrDefault("lookupPerson", GenericValue.NULL_VALUE);

        Collection<GenericValue> emails = ContactHelper
                .getContactMechByType(party, "EMAIL_ADDRESS", false);

        Collection<GenericValue> addresses = getRelatedContactMechs(
                ContactHelper.getContactMechByType(party, "POSTAL_ADDRESS", false),
                "PostalAddress"
        );

        Collection<GenericValue> telephones = getRelatedContactMechs(
                ContactHelper.getContactMechByType(party, "TELECOM_NUMBER", false),
                "TelecomNumber"
        );

        return UtilMisc.toMap(
                "person", person,
                "emails", emails,
                "addresses", addresses,
                "telephones", telephones
        );
    }

    public static Map<String, Object> updatePersonTemp(DispatchContext dctx, Map<String, Object> context) {
        return runServiceLoggedIn("updatePerson", dctx, context);
    }

    public static Map<String, Object> createPartyEmailTemp(DispatchContext dctx, Map<String, Object> context) {
        return runServiceLoggedIn("createPartyEmailAddress", dctx, context);
    }

    public static Map<String, Object> updatePartyEmailTemp(DispatchContext dctx, Map<String, Object> context) {
        return runServiceLoggedIn("updatePartyEmailAddress", dctx, context);
    }

    public static Map<String, Object> createPartyTelecomNumberTemp(DispatchContext dctx, Map<String, Object> context) {
        return runServiceLoggedIn("createPartyTelecomNumber", dctx, context);
    }

    public static Map<String, Object> updatePartyTelecomNumberTemp(DispatchContext dctx, Map<String, Object> context) {
        return runServiceLoggedIn("createPartyTelecomNumber", dctx, context);
    }

    public static Map<String, Object> createPartyPostalAddressTemp(DispatchContext dctx, Map<String, Object> context) {
        return runServiceLoggedIn("createPartyPostalAddress", dctx, context);
    }

    public static Map<String, Object> updatePartyPostalAddressTemp(DispatchContext dctx, Map<String, Object> context) {
        return runServiceLoggedIn("updatePartyPostalAddress", dctx, context);
    }

    public static Map<String, Object> deletePartyContactMechTemp(DispatchContext dctx, Map<String, Object> context) {
        return runServiceLoggedIn("deletePartyContactMech", dctx, context);
    }
}
