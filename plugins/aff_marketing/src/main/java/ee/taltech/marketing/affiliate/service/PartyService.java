package ee.taltech.marketing.affiliate.service;

import ee.taltech.marketing.affiliate.model.AffiliateDTO;
import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.party.party.PartyServices;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.ServiceUtil;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import static ee.taltech.marketing.affiliate.model.AffiliateDTO.Status.*;

public class PartyService {

    public static final String module = PartyServices.class.getName();

    public Map<String, AffiliateDTO> approve(DispatchContext dctx, Map<String, ?> context) throws GenericEntityException {
        String partyId = (String) context.get("partyId");
        Delegator delegator = dctx.getDelegator();
        GenericValue genericValue = EntityQuery.use(delegator).from("Affiliate").where("partyId", partyId).queryOne();

        genericValue.set("dateTimeApproved", new Timestamp(System.currentTimeMillis()));

        List<GenericValue> affiliateCodes = getAffiliateCodes(dctx, context).get("affiliateDTOs");
        if (affiliateCodes.isEmpty()) {
            GenericValue genericCode = delegator.makeValue("AffiliateCode", UtilMisc.toMap("partyId", partyId, "affiliateCodeId", delegator.getNextSeqId("AffiliateCode"), "isDefault", true));
            delegator.create(genericCode);
        }

        genericValue.set("status", ACTIVE);
        delegator.store(genericValue);

        return Map.of("approvedPartner", getAffiliateDTO((String) genericValue.get("partyId"), false, delegator));
    }

    public Map<String, AffiliateDTO> disapprove(DispatchContext dctx, Map<String, ?> context) throws GenericEntityException {
        String partyId = (String) context.get("partyId");
        Delegator delegator = dctx.getDelegator();
        GenericValue genericValue = EntityQuery.use(delegator).from("Affiliate").where("partyId", partyId).queryOne();
        genericValue.set("dateTimeApproved", null);
        genericValue.set("status", DECLINED);
        delegator.store(genericValue);
        return Map.of("disapprovedPartner", getAffiliateDTO((String) genericValue.get("partyId"), false, dctx.getDelegator()));
    }

    public Map<String, GenericValue> createAffiliateCode(DispatchContext dctx, Map<String, ?> context) throws GenericEntityException {
        String partyId = (String) context.get("partyId");
        Delegator delegator = dctx.getDelegator();
        checkApprovedAffiliate(partyId, dctx.getDelegator());
        GenericValue genericValue = delegator.makeValue("AffiliateCode", UtilMisc.toMap("partyId", partyId, "affiliateCodeId", delegator.getNextSeqId("AffiliateCode"), "isDefault", false));
        delegator.create(genericValue);
        return Map.of("createdCode", genericValue);
    }

    public Map<String, GenericValue> deleteAffiliateCode(DispatchContext dctx, Map<String, ?> context) throws GenericEntityException {
        String partyId = (String) context.get("partyId");
        String affCode = (String) context.get("affiliateCodeId");
        Delegator delegator = dctx.getDelegator();
        checkApprovedAffiliate(partyId, dctx.getDelegator());
        GenericValue genericValue = EntityQuery.use(delegator).from("AffiliateCode").where("partyId", partyId, "affiliateCodeId", affCode).queryOne();
        if (genericValue.get("isDefault").equals("N")) {
            genericValue.remove();
        } else {
            throw new IllegalArgumentException("Code is default and cannot be deleted");
        }
        return Map.of("deletedCode", genericValue);
    }

    public Map<String, AffiliateDTO> createAffiliateForUserLogin(DispatchContext dctx, Map<String, ?> context) throws GenericEntityException {
        Map<String, Object> affiliateCreateContext = new HashMap<>();

        String userPartyId = (String) context.get("partyId");
        Delegator delegator = dctx.getDelegator();
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

        // create affiliate by for created/existing party
        affiliateCreateContext.put("partyId", userPartyId);
        affiliateCreateContext.put("locale", Locale.ENGLISH);
        PartyServices.createAffiliate(dctx, affiliateCreateContext);

        try {
            GenericValue genericValue = EntityQuery.use(delegator).from("Affiliate").where("partyId", userPartyId).queryOne();
            genericValue.set("status", PENDING);
            delegator.store(genericValue);

            String rootPartyId = (String) context.get("rootPartyId");
            genericValue.set("RootPartyId", rootPartyId);
        } catch (GenericEntityException | NullPointerException e) {
            Debug.logWarning(e.getMessage(), module);
        }

        return Map.of("createdAffiliate", getAffiliateDTO(userPartyId, false, dctx.getDelegator()));
    }

    public Map<String, Object> getPartyIdForUserId(DispatchContext dctx, Map<String, ?> context) {
        Map<String, Object> affiliateCreateContext = new HashMap<>();

        String userLoginId = (String) context.get("userLoginId");
        GenericValue currentUserLogin = ServiceUtil.getUserLogin(dctx, affiliateCreateContext, userLoginId);
        Map<String, Object> partyMap = new HashMap<>();
        partyMap.put("partyId", currentUserLogin.getString("partyId"));
        return partyMap;
    }

    public Map<String, List<GenericValue>> getAffiliateCodes(DispatchContext dctx, Map<String, ?> context) throws GenericEntityException {
        String partyId = (String) context.get("partyId");
        Delegator delegator = dctx.getDelegator();
        checkApprovedAffiliate(partyId, dctx.getDelegator());
        return Map.of("affiliateDTOs", EntityQuery.use(delegator).from("AffiliateCode").where("partyId", partyId).queryList());
    }

    private void checkApprovedAffiliate(String partyId, Delegator delegator) throws GenericEntityException {
        GenericValue userParty = EntityQuery
                .use(delegator)
                .from("Affiliate")
                .where("partyId", partyId)
                .queryOne();
        if (userParty == null) {
            ServiceUtil.returnError("You are not an affiliate yet!");
        }
        assert userParty != null;
        boolean isApproved = userParty.get("dateTimeApproved") != null;
        if (!isApproved) {
            ServiceUtil.returnError("You are not approved yet!");
        }
    }

    private GenericValue getPerson(String partyId, Delegator delegator) {
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

    public AffiliateDTO getAffiliateDTO(String partyId, boolean withSubAffiliates, Delegator delegator) {
        AffiliateDTO affiliateDTO = new AffiliateDTO();
        GenericValue person = getPerson(partyId, delegator);

        GenericValue affiliate;
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

                subAffiliates = genericSubAffiliates.stream().map(x -> getAffiliateDTO((String) x.get("partyId"), false, delegator)).collect(Collectors.toList());
            }
        } catch (GenericEntityException e) {
            throw new IllegalArgumentException("No such Affiliate found!");
        }

        affiliateDTO.setPartyId(partyId);
        affiliateDTO.setEmail(getEmail(partyId, delegator));
        affiliateDTO.setFirstName((String) person.get("firstName"));
        affiliateDTO.setLastName((String) person.get("lastName"));
        affiliateDTO.setStatus(AffiliateDTO.Status.valueOf((String) affiliate.get("status")));
        affiliateDTO.setDate((Timestamp) affiliate.get("dateTimeApproved"));
        affiliateDTO.setSubAffiliates(subAffiliates);

        return affiliateDTO;
    }

    private String getEmail(String partyId, Delegator delegator) {
        GenericValue partyContactMechPurpose;
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
