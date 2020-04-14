package ee.taltech.marketing.affiliate.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.party.party.PartyServices;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class PartyService {

    Delegator delegator;
    LocalDispatcher dispatcher;
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static final String module = PartyServices.class.getName();
    private DispatchContext dispatchCtx;

    public PartyService(DispatchContext dctx) {
        delegator = dctx.getDelegator();
        dispatchCtx = dctx;
    }


    /**
     * @param exchange - request that is wrapped by camel with additional information added during routing
     * @return JSON with all the parties found with a given partyId
     */
    public Map<String, Object> getPartyById(Map<String, Object> data) {
        String id = (String) data.get("id");

        DispatchContext myContext = new DispatchContext("myContext", null, dispatcher);
        Map<String, Object> context = new HashMap<>();
        context.put("idToFind", id);
        context.put("partyIdentificationTypeId", null);
        context.put("searchPartyFirst", null);
        context.put("searchAllIdContext", null);

        return PartyServices.findPartyById(myContext, context);
    }

    /**
     * Get unconfirmed affiliates (such that have no approval date)
     *
     * @return
     * @throws GenericEntityException
     */
    public List<GenericValue> getUnconfirmedAffiliates() throws GenericEntityException {
        List<GenericValue> genericValues = EntityQuery.use(delegator).from("Affiliate").where("dateTimeApproved", null).queryList();
        return genericValues.stream().map(x -> getPerson((String) x.get("partyId"))).collect(Collectors.toList());
    }

    /**
     * Get all affiliates
     *
     * @return
     * @throws GenericEntityException
     */
    public List<GenericValue> getAffiliates() throws GenericEntityException {
        List<GenericValue> genericValues = EntityQuery.use(delegator).from("Affiliate").queryList();
        return genericValues.stream().map(x -> getPerson((String) x.get("partyId"))).collect(Collectors.toList());
    }

    public GenericValue approve(Map<String, Object> data) throws GenericEntityException {
        String partyId = (String) data.get("partyId");
        GenericValue genericValue = EntityQuery.use(delegator).from("Affiliate").where("partyId", partyId).queryOne();
        genericValue.set("dateTimeApproved", new Timestamp(System.currentTimeMillis()));
        delegator.store(genericValue);
        return getPerson((String) genericValue.get("partyId"));
    }

    public GenericValue disapprove(Map<String, Object> data) throws GenericEntityException {
        String partyId = (String) data.get("partyId");
        GenericValue genericValue = EntityQuery.use(delegator).from("Affiliate").where("partyId", partyId).queryOne();
        genericValue.set("dateTimeApproved", null);
        delegator.store(genericValue);
        return getPerson((String) genericValue.get("partyId"));
    }

    public GenericValue createAffiliateCode(Map<String, Object> data) throws GenericEntityException {
        String partyId = (String) data.get("partyId");
        checkApprovedAffiliate(partyId);
        GenericValue genericValue = delegator.makeValue("AffiliateCode", UtilMisc.toMap("partyId", partyId, "affiliateCodeId", delegator.getNextSeqId("AffiliateCode")));
        delegator.create(genericValue);
        return genericValue;
    }

    public List<GenericValue> getAffiliateCodes(Map<String, Object> data) throws GenericEntityException {
        String partyId = (String) data.get("partyId");
        checkApprovedAffiliate(partyId);
        List<GenericValue> genericValue = EntityQuery.use(delegator).from("AffiliateCode").where("partyId", partyId).queryList();
        return genericValue;
    }


    /**
     * @return - status of operation
     */
    public GenericValue createAffiliateForUserLogin(Map<String, Object> data) throws GenericEntityException {
        Map<String, Object> affiliateCreateContext = new HashMap<>();

        //Retrieve UserLogin via userLoginId
        String userLoginId = (String) data.get("userLoginId");
        GenericValue currentUserLogin = ServiceUtil.getUserLogin(dispatchCtx, affiliateCreateContext, userLoginId);

        // check type of user's party via partyId
        String userPartyId = currentUserLogin.getString("partyId");
        Locale locale = Locale.ENGLISH;
        GenericValue userParty = EntityQuery
                .use(delegator)
                .from("Party")
                .where("partyId", userPartyId)
                .queryOne();


        if (userParty != null) {
            if (!"PERSON".equals(userParty.getString("partyTypeId"))) {
                // TODO throw error
//                return Map.of("status", "user already has party of another type");
            }
        }

        Map<String, Object> personCreateContext = new HashMap<>();
        personCreateContext.put("locale", locale);
        personCreateContext.put("userLogin", currentUserLogin);
        PartyServices.createPerson(dispatchCtx, personCreateContext);

        // create affiliate by for created/existing party
        affiliateCreateContext.put("partyId", userPartyId);
        affiliateCreateContext.put("locale", Locale.ENGLISH);
        PartyServices.createAffiliate(dispatchCtx, affiliateCreateContext);

        try {
            String rootPartyId = (String) data.get("rootPartyId");
            GenericValue genericValue = EntityQuery.use(delegator).from("Affiliate").where("partyId", userPartyId).queryOne();
            genericValue.set("RootPartyId", rootPartyId);
            delegator.store(genericValue);
        } catch (GenericEntityException | NullPointerException e) {
            Debug.logWarning(e.getMessage(), module);
        }

        return getPerson(userPartyId);
    }

    private void checkApprovedAffiliate(String partyId) throws GenericEntityException {
        GenericValue userParty = EntityQuery
                .use(delegator)
                .from("Affiliate")
                .where("partyId", partyId)
                .queryOne();
        if (userParty == null) {
            ServiceUtil.returnError("You are not an affiliate yet!");
        }
        boolean isApproved = userParty.get("dateTimeApproved") != null;
        if (!isApproved) {
            ServiceUtil.returnError("You are not approved yet!");
        }
    }


    private GenericValue getPerson(String partyId) {
        GenericValue person = null;
        try {
            person = EntityQuery
                    .use(delegator)
                    .from("Person")
                    .where("partyId", partyId)
                    .queryOne();
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }

        return person;
    }
}
