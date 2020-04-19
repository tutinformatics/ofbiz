package ee.taltech.marketing.affiliate.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ee.taltech.marketing.affiliate.model.AffiliateDTO;
import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericPK;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.party.party.PartyServices;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.ServiceUtil;

import javax.ws.rs.ext.Provider;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import static ee.taltech.marketing.affiliate.model.AffiliateDTO.Status.*;

@Provider
public class PartyService {

    private Delegator delegator;
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static final String module = PartyServices.class.getName();
    private DispatchContext dispatchCtx;

    public PartyService(DispatchContext dpc) {
        dispatchCtx = dpc;
        delegator = dpc.getDelegator();
    }


    /**
     * Get unconfirmed affiliates (such that have no approval date)
     *
     * @return
     * @throws GenericEntityException
     */
    public List<AffiliateDTO> getUnconfirmedAffiliates() throws GenericEntityException {
        List<GenericValue> genericValues = EntityQuery.use(delegator).from("Affiliate").where("status", "PENDING").queryList();
        return genericValues.stream().map(x -> getAffiliateDTO((String) x.get("partyId"), false)).collect(Collectors.toList());
    }

    /**
     * Get all affiliates
     *
     * @return
     * @throws GenericEntityException
     */
    public List<AffiliateDTO> getAffiliates() throws GenericEntityException {
        List<GenericValue> genericValues = EntityQuery.use(delegator).from("Affiliate").queryList();
        return genericValues.stream().map(x -> getAffiliateDTO((String) x.get("partyId"), false)).collect(Collectors.toList());
    }

    /**
     * Get all affiliate
     *
     * @return
     * @throws GenericEntityException
     */
    public AffiliateDTO getAffiliate(Map<String, Object> data) throws GenericEntityException {
        return getAffiliateDTO((String) data.get("partyId"), true);
    }

    public AffiliateDTO approve(Map<String, Object> data) throws GenericEntityException {
        String partyId = (String) data.get("partyId");
        GenericValue genericValue = EntityQuery.use(delegator).from("Affiliate").where("partyId", partyId).queryOne();
        if (genericValue.get("status") == PENDING) {
            genericValue.set("dateTimeApproved", new Timestamp(System.currentTimeMillis()));

            List<GenericValue> affiliateCodes = getAffiliateCodes(data);
            if (affiliateCodes.isEmpty()) {
                GenericValue genericCode = delegator.makeValue("AffiliateCode", UtilMisc.toMap("partyId", partyId, "affiliateCodeId", delegator.getNextSeqId("AffiliateCode"), "isDefault", true));
                delegator.create(genericCode);
            }


            genericValue.set("status", ACTIVE);
            delegator.store(genericValue);
        }

        return getAffiliateDTO((String) genericValue.get("partyId"), false);
    }

    public AffiliateDTO disapprove(Map<String, Object> data) throws GenericEntityException {
        String partyId = (String) data.get("partyId");
        GenericValue genericValue = EntityQuery.use(delegator).from("Affiliate").where("partyId", partyId).queryOne();
        genericValue.set("dateTimeApproved", null);
        genericValue.set("status", DECLINED);
        delegator.store(genericValue);
        return getAffiliateDTO((String) genericValue.get("partyId"), false);
    }

    public GenericValue createAffiliateCode(Map<String, Object> data) throws GenericEntityException {
        String partyId = (String) data.get("partyId");
        checkApprovedAffiliate(partyId);
        GenericValue genericValue = delegator.makeValue("AffiliateCode", UtilMisc.toMap("partyId", partyId, "affiliateCodeId", delegator.getNextSeqId("AffiliateCode"), "isDefault", false));
        delegator.create(genericValue);
        return genericValue;
    }

    public List<GenericValue> getAffiliateCodes(Map<String, Object> data) throws GenericEntityException {
        String partyId = (String) data.get("partyId");
        checkApprovedAffiliate(partyId);
        List<GenericValue> genericValue = EntityQuery.use(delegator).from("AffiliateCode").where("partyId", partyId).queryList();
        return genericValue;
    }

    public GenericValue deleteAffiliateCodes(Map<String, Object> data) throws GenericEntityException {
        String partyId = (String) data.get("partyId");
        String affCode = (String) data.get("affiliateCodeId");
        checkApprovedAffiliate(partyId);
        GenericValue genericValue = EntityQuery.use(delegator).from("AffiliateCode").where("partyId", partyId, "affiliateCodeId", affCode).queryOne();
        if (genericValue.get("isDefault").equals("N")) {
            genericValue.remove();
        } else {
            throw new IllegalArgumentException("Code is default and cannot be deleted");
        }
        return genericValue;
    }


    /**
     * @return - status of operation
     */
    public AffiliateDTO createAffiliateForUserLogin(Map<String, Object> data) throws GenericEntityException {
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
                throw new IllegalArgumentException("User must be of PERSON group");
            }
        }

        GenericValue checkExistence = EntityQuery.use(delegator).from("Affiliate").where("partyId", userPartyId).queryOne();
        if (checkExistence != null) {
            throw new IllegalArgumentException("User already has applied for Affliate");
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
            GenericValue genericValue = EntityQuery.use(delegator).from("Affiliate").where("partyId", userPartyId).queryOne();
            genericValue.set("status", PENDING);
            delegator.store(genericValue);

            String rootPartyId = (String) data.get("rootPartyId");
            genericValue.set("RootPartyId", rootPartyId);
        } catch (GenericEntityException | NullPointerException e) {
            Debug.logWarning(e.getMessage(), module);
        }

        return getAffiliateDTO(userPartyId, false);
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

    public AffiliateDTO getAffiliateDTO(Map<String, Object> data) throws GenericEntityException {
        return getAffiliateDTO((String) data.get("partyId"), true);
    }

    public AffiliateDTO getAffiliateDTO(String partyId, boolean withSubAffiliates) {
        AffiliateDTO affiliateDTO = new AffiliateDTO();
        GenericValue person = getPerson(partyId);

        GenericValue affiliate = null;
        List<AffiliateDTO> subAffiliates = new ArrayList<>();
        try {
            affiliate = EntityQuery
                    .use(delegator)
                    .from("Affiliate")
                    .where("partyId", partyId)
                    .queryOne();

            if (withSubAffiliates) {
                List<GenericValue> genericSubAffiliates = EntityQuery
                        .use(delegator)
                        .from("Affiliate")
                        .where("RootPartyId", partyId)
                        .queryList();

                subAffiliates = genericSubAffiliates.stream().map(x -> getAffiliateDTO((String) x.get("partyId"), false)).collect(Collectors.toList());
            }
        } catch (GenericEntityException e) {
            throw new IllegalArgumentException("No such Affiliate found!");
        }

        affiliateDTO.setEmail(getEmail(partyId));
        affiliateDTO.setFirstName((String) person.get("firstName"));
        affiliateDTO.setLastName((String) person.get("lastName"));
        affiliateDTO.setStatus(AffiliateDTO.Status.valueOf((String) affiliate.get("status")));
        affiliateDTO.setDate((Timestamp) affiliate.get("dateTimeApproved"));
        affiliateDTO.setSubAffiliates(subAffiliates);

        return affiliateDTO;
    }

    private String getEmail(String partyId) {
        GenericValue partyContactMechPurpose = null;
        String email = "";
        try {
            partyContactMechPurpose = EntityQuery
                    .use(delegator)
                    .from("PartyContactMechPurpose")
                    .where("partyId", partyId, "contactMechPurposeTypeId", "PRIMARY_EMAIL")
                    .queryOne();

            GenericValue contactMech = EntityQuery
                    .use(delegator)
                    .from("ContactMech")
                    .where("contactMechId", partyContactMechPurpose.get("contactMechId"), "contactMechTypeId", "EMAIL_ADDRESS")
                    .queryOne();

            email = (String) contactMech.get("infoString");
        } catch (Exception e) {
//            e.printStackTrace();
        }

        return email;
    }
}
