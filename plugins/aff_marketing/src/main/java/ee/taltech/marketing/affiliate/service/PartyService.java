package ee.taltech.marketing.affiliate.service;

import ee.taltech.marketing.affiliate.model.AffiliateDTO;
import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilDateTime;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.DelegatorFactory;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.party.party.PartyServices;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceContainer;
import org.apache.ofbiz.service.ServiceUtil;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import static ee.taltech.marketing.affiliate.model.AffiliateDTO.Status.*;

public class PartyService {

    private static boolean init = false;

    public static final String module = PartyServices.class.getName();

    public PartyService() {
        if (!init) {
            Delegator delegator = DelegatorFactory.getDelegator("default");
            LocalDispatcher dispatcher = ServiceContainer.getLocalDispatcher("aff-dispatcher", delegator);
            try {
                generateAffPromoPerCategory(dispatcher.getDispatchContext(), new HashMap<>());
            } catch (GenericEntityException e) {
                e.printStackTrace();
            }
            init = true;
        }
    }

    public Map<String, AffiliateDTO> approve(DispatchContext dctx, Map<String, ?> context) throws GenericEntityException {
        String partyId = (String) context.get("partyId");
        Delegator delegator = dctx.getDelegator();
        GenericValue genericValue = EntityQuery.use(delegator).from("Affiliate").where("partyId", partyId).queryOne();

        genericValue.set("dateTimeApproved", new Timestamp(System.currentTimeMillis()));

        genericValue.set("status", ACTIVE);
        delegator.store(genericValue);

        List<GenericValue> affiliateCodes = getAffiliateCodes(dctx, context).get("affiliateDTOs");
        if (affiliateCodes.isEmpty()) {
            GenericValue genericCode = delegator.makeValue("AffiliateCode", UtilMisc.toMap("partyId", partyId, "affiliateCodeId", delegator.getNextSeqId("AffiliateCode"), "isDefault", true));
            delegator.create(genericCode);
        }


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

    public Map<String, Object> generateAffPromoPerCategory(DispatchContext dctx, Map<String, ?> context) throws GenericEntityException {
        Delegator delegator = dctx.getDelegator();
        List<String> tags = new ArrayList<>();
        List<GenericValue> categories = EntityQuery.use(delegator).from("ProductCategory").queryList();
        for (GenericValue category : categories) {
           if (EntityQuery.use(delegator).from("ProductPromo").where("promoName", "AffiliateDiscount", "promoText", category.getString("productCategoryId")).queryList().get(0) == null) {
               String promoId = delegator.getNextSeqId("ProductPromo");
               GenericValue genericValue = delegator.makeValue("ProductPromo", UtilMisc.toMap("productPromoId", promoId, "userEntered", "Y", "showToCustomer", "N", "requireCode", "Y", "promoName", "AffiliateDiscount", "promoText", category.get("productCategoryId"), "useLimitPerOrder", 1));
               delegator.create(genericValue);

               String promoRuleId = delegator.getNextSeqId("ProductPromoRule");
               genericValue = delegator.makeValue("ProductPromoRule", UtilMisc.toMap("productPromoId", promoId, "productPromoRuleId", promoRuleId, "ruleName", "AffiliateDiscount"));
               delegator.create(genericValue);

               String promoActionId = delegator.getNextSeqId("ProductPromoAction");
               genericValue = delegator.makeValue("ProductPromoAction", UtilMisc.toMap("productPromoId", promoId, "productPromoRuleId", promoRuleId, "productPromoActionSeqId", promoActionId, "productPromoActionEnumId", "PROMO_ORDER_PERCENT", "amount", "20", "orderAdjustmentTypeId", "PROMOTION_ADJUSTMENT"));
               delegator.create(genericValue);

               String promoCondId = delegator.getNextSeqId("ProductPromoCond");
               genericValue = delegator.makeValue("ProductPromoCond", UtilMisc.toMap("productPromoId", promoId, "productPromoRuleId", promoRuleId, "productPromoCondSeqId", promoCondId));
               delegator.create(genericValue);

               genericValue = delegator.makeValue("ProductPromoCategory", UtilMisc.toMap("productPromoId", promoId, "productPromoRuleId", promoRuleId, "productPromoCondSeqId", promoCondId, "productPromoActionSeqId", promoActionId, "productCategoryId", category.get("productCategoryId"), "andGroupId", "_NA_", "includeSubCategories",
                       "Y"));
               delegator.create(genericValue);

               // todo get store id
               genericValue = delegator.makeValue("ProductStorePromoAppl", UtilMisc.toMap("productStoreId", "9000", "productPromoId", promoId, "fromDate", UtilDateTime.nowTimestamp()));
               delegator.create(genericValue);
           }
        }

        return Map.of("values", tags);
    }

    public Map<String, Object> createAffiliateCode(DispatchContext dctx, Map<String, ?> context) throws GenericEntityException {
        GenericValue discountCode = createDiscountCode(dctx, context);

        String partyId = (String) context.get("partyId");
        String categoryId = (String) context.get("productCategoryId");
        Delegator delegator = dctx.getDelegator();
        checkApprovedAffiliate(partyId, dctx.getDelegator());
        GenericValue genericValue = delegator.makeValue(
                "AffiliateCode",
                UtilMisc.toMap(
                        "partyId", partyId,
                        "affiliateCodeId", discountCode.get("productPromoCodeId"),
                        "isDefault", false,
                        "categoryId", categoryId),
                categoryId);
        delegator.create(genericValue);


        return Map.of("createdCode", genericValue);
    }

    public GenericValue createDiscountCode(DispatchContext dctx, Map<String, ?> context) throws GenericEntityException {
        Delegator delegator = dctx.getDelegator();

        GenericValue promoForCategory = getPromoForCategory(dctx, context);

        String productPromoCodeId = delegator.getNextSeqId("ProductPromoCode");
        GenericValue promoCode = delegator.makeValue("ProductPromoCode", UtilMisc.toMap("productPromoCodeId", productPromoCodeId, "productPromoId", promoForCategory.get("productPromoId"), "userEntered", "Y", "requireEmailOrParty", "N"));
        delegator.create(promoCode);

        return promoCode;
    }

    public GenericValue getPromoForCategory(DispatchContext dctx, Map<String, ?> context) throws GenericEntityException {
        Delegator delegator = dctx.getDelegator();
        String productCategoryId = (String) context.get("productCategoryId");

        GenericValue promoForCategory = EntityQuery.use(delegator).from("ProductPromo").where("promoName", "AffiliateDiscount", "promoText", productCategoryId).queryOne();

        if (promoForCategory == null) {
            promoForCategory = createPromoForCategory(dctx, context);
        }

        return promoForCategory;
    }

    public String generateTag(String tag, Map<String, String> params) {
        StringBuilder stringParams = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            stringParams.append(String.format("%s=\"%s\" ", entry.getKey(), entry.getValue()));
        }
        return String.format("<%s %s/>", tag, stringParams);
    }

    public GenericValue createPromoForCategory(DispatchContext dctx, Map<String, ?> context) throws GenericEntityException {
        Delegator delegator = dctx.getDelegator();
        String productCategoryId = (String) context.get("productCategoryId");
        GenericValue promoValue = null;
        try {

            GenericValue productCategory = EntityQuery.use(delegator).from("ProductCategory").where("productCategoryId", productCategoryId).queryOne();

            if (productCategory == null) {
                throw new IllegalArgumentException("productCategoryId " + productCategoryId + " does not exist");
            }

            String promoId = delegator.getNextSeqId("ProductPromo");
            promoValue = delegator.makeValue("ProductPromo", UtilMisc.toMap("productPromoId", promoId, "userEntered", "Y", "showToCustomer", "Y", "requireCode", "Y", "promoName", "AffiliateDiscount", "promoText", productCategoryId, "useLimitPerOrder", 1));
            delegator.create(promoValue);

            String promoRuleId = delegator.getNextSeqId("ProductPromoRule");
            GenericValue genericValue = delegator.makeValue("ProductPromoRule", UtilMisc.toMap("productPromoId", promoId, "productPromoRuleId", promoRuleId, "ruleName", "AffiliateDiscount"));
            delegator.create(genericValue);

            String promoActionId = delegator.getNextSeqId("ProductPromoAction");
            genericValue = delegator.makeValue("ProductPromoAction", UtilMisc.toMap("productPromoId", promoId, "productPromoRuleId", promoRuleId, "productPromoActionSeqId", promoActionId, "productPromoActionEnumId", "PROMO_ORDER_PERCENT", "amount", "20", "orderAdjustmentTypeId", "PROMOTION_ADJUSTMENT"));
            delegator.create(genericValue);

            String promoCondId = delegator.getNextSeqId("ProductPromoCond");
            genericValue = delegator.makeValue("ProductPromoCond", UtilMisc.toMap("productPromoId", promoId, "productPromoRuleId", promoRuleId, "productPromoCondSeqId", promoCondId));
            delegator.create(genericValue);

            genericValue = delegator.makeValue("ProductPromoCategory", UtilMisc.toMap("productPromoId", promoId, "productPromoRuleId", promoRuleId, "productPromoCondSeqId", promoCondId, "productPromoActionSeqId", promoActionId, "productCategoryId", productCategoryId, "andGroupId", "_NA_", "includeSubCategories",
                    "Y"));
            delegator.create(genericValue);

            // todo get store id
            genericValue = delegator.makeValue("ProductStorePromoAppl", UtilMisc.toMap("productStoreId", "9000", "productPromoId", promoId, "fromDate", UtilDateTime.nowTimestamp()));
            delegator.create(genericValue);
        } catch (Exception e) {
            Debug.logWarning(e.getMessage(), module);
        }

        return promoValue;
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


    public Map<String, AffiliateDTO> disableAffiliate(DispatchContext dctx, Map<String, ?> context) throws GenericEntityException {
        String partyId = (String) context.get("partyId");
        List<GenericValue> affCodes = getAffiliateCodes(dctx, context).get("affiliateDTOs");
        Delegator delegator = dctx.getDelegator();
        checkApprovedAffiliate(partyId, dctx.getDelegator());
        for (GenericValue affCode : affCodes) {
            String currentCode = (String) affCode.get("affiliateCodeId");
            GenericValue genericCode = EntityQuery.use(delegator).from("AffiliateCode").where("partyId", partyId, "affiliateCodeId", currentCode).queryOne();
            genericCode.remove();
        }
        return disapprove(dctx, context);
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

            String affCode = (String) context.get("affCode");
            List<GenericValue> affiliateCodes = EntityQuery.use(delegator).from("AffiliateCode").queryList();
            String rootPartyId = (String) affiliateCodes.stream().filter(affDto -> affDto.get("affiliateCodeId").equals(affCode)).findFirst().orElse(null).get("partyId");

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

    public Map<String, String> getAffiliateStatus(DispatchContext dctx, Map<String, ?> context) throws GenericEntityException {
        String partyId = (String) context.get("partyId");
        GenericValue affiliate = EntityQuery
                .use(dctx.getDelegator())
                .from("Affiliate")
                .where("partyId", partyId)
                .queryOne();
        if (affiliate != null) {
            return Map.of("status", affiliate.getString("status"));
        } else {
            return Map.of("status", "NOT-PARTNER");
        }
    }

    private void checkApprovedAffiliate(String partyId, Delegator delegator) throws GenericEntityException {
        GenericValue userParty = EntityQuery
                .use(delegator)
                .from("Affiliate")
                .where("partyId", partyId)
                .queryOne();
        if (userParty == null) {
            throw new IllegalArgumentException("You are not an affiliate yet!");
        }

        boolean isApproved = userParty.get("dateTimeApproved") != null;
        if (!isApproved) {
            throw new IllegalArgumentException("You are not approved yet!");

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
