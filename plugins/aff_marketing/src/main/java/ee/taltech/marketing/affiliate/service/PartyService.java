package ee.taltech.marketing.affiliate.service;

import ee.taltech.marketing.affiliate.model.AffiliateDTO;
import ee.taltech.marketing.affiliate.model.SimpleDiscountDTO;
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

/**
 * Service for providing affiliate marketing functionailty.
 * Upon initialization creates ProductPromo for every category, that affiliate discount codes depend on.
 * If categories are already created, do nothing.
 */
public class PartyService {

    // Entity names
    private static final String PRODUCT_CATEGORY = "ProductCategory";
    private static final String PRODUCT_PROMO = "ProductPromo";
    private static final String AFFILIATE_DISCOUNT = "AffiliateDiscount";
    private static final String AFFILIATE = "Affiliate";
    private static final String AFFILIATE_CODE = "AffiliateCode";
    private static final String PRODUCT_PROMO_ACTION = "ProductPromoAction";


    // Entity fields
    private static final String PARTY_ID = "partyId";
    private static final String DATE_TIME_APPROVED = "dateTimeApproved";
    private static final String STATUS = "status";
    private static final String AFFILIATE_DTOS = "affiliateDTOs";
    private static final String IS_DEFAULT = "isDefault";
    private static final String PRODUCT_CATEGORY_ID = "productCategoryId";
    private static final String AFFILIATE_CODE_ID = "affiliateCodeId";
    private static final String PRODUCT_PROMO_CODE_ID = "productPromoCodeId";
    private static final String AFFILIATE_COMMISSION = "affiliateCommission";
    private static final String PRODUCT_PROMO_ID = "productPromoId";
    private static final String PROMO_TEXT = "promoText";
    private static final String DISCOUNT = "discount";
    private static final String AMOUNT = "amount";
    private static final String PRODUCT_PROMO_RULE_ID = "productPromoRuleId";
    private static final String SETTINGS_TYPE = "settingsType";
    private static final String COMMISSION_FREQUENCY = "commissionFrequency";
    private static final String CODE_COOKIE_DURATION = "codeCookieDuration";
    private static final String MULTI_LEVEL_AFFILIATION = "multiLevelAffiliation";
    private static final String DEFAULT_DISCOUNT_CATEGORY = "CATALOG1";
    private static final String PROMO_NAME = "promoName";


    private static boolean initialized = false;
    private static final int DEFAULT_AFFILIATE_COMMISSION = 5;

    public static final String MODULE = PartyServices.class.getName();

    /**
     * Upon initialization creates ProductPromo for every category, that affiliate discount codes depend on.
     * If categories are already created, do nothing.
     */
    public PartyService() {

        if (!initialized) {
            try {
                Delegator delegator = DelegatorFactory.getDelegator("default");
                LocalDispatcher dispatcher = ServiceContainer.getLocalDispatcher("aff-dispatcher", delegator);

                List<GenericValue> categories = EntityQuery.use(delegator).from(PRODUCT_CATEGORY).queryList();
                List<GenericValue> productPromos = EntityQuery.use(delegator).from(PRODUCT_PROMO).where(PROMO_NAME, AFFILIATE_DISCOUNT).queryList();

                if (categories.size() > productPromos.size()) {
                    generateAffPromoPerCategory(dispatcher.getDispatchContext(), new HashMap<>());
                }

                initialized = true;
            } catch (GenericEntityException e) {
                Debug.logError(e, MODULE);
            }
        }


    }

    /**
     * Approves an affiliate. Creates new default affiliate code with {@link #DEFAULT_DISCOUNT_CATEGORY}
     * Sets status to {@link AffiliateDTO.Status#ACTIVE} and approved time to current millis.
     * Approves regardless current status.
     *
     * @param dctx    DispatchContext
     * @param context requires {@link #PARTY_ID}
     * @return Affiliate information without sub-affiliates
     * @throws GenericEntityException in case such user does not exist
     * @see AffiliateDTO
     */
    public Map<String, AffiliateDTO> approve(DispatchContext dctx, Map<String, ?> context) throws GenericEntityException {
        String partyId = (String) context.get(PARTY_ID);
        Delegator delegator = dctx.getDelegator();
        GenericValue genericValue = EntityQuery.use(delegator).from(AFFILIATE).where(PARTY_ID, partyId).queryOne();

        genericValue.set(DATE_TIME_APPROVED, new Timestamp(System.currentTimeMillis()));

        genericValue.set(STATUS, ACTIVE);
        delegator.store(genericValue);

        List<GenericValue> affiliateCodes = getAffiliateCodes(dctx, context).get(AFFILIATE_DTOS);
        if (affiliateCodes.isEmpty()) {
            Map<String, Object> codeContext = new HashMap<>(context);
            codeContext.put(IS_DEFAULT, true);
            codeContext.put(PRODUCT_CATEGORY_ID, DEFAULT_DISCOUNT_CATEGORY);
            createAffiliateCode(dctx, codeContext);
        }

        return Map.of("approvedPartner", getAffiliateDTO((String) genericValue.get(PARTY_ID), false, delegator));
    }

    /**
     * Disapproves an affiliate.
     * Sets status to {@link AffiliateDTO.Status#DECLINED} and approved time to null.
     * Disapproves regardless current status.
     *
     * @param dctx    DispatchContext
     * @param context requires {@link #PARTY_ID}
     * @return Affiliate information without sub-affiliates
     * @throws GenericEntityException in case such user does not exist
     * @see AffiliateDTO
     */
    public Map<String, AffiliateDTO> disapprove(DispatchContext dctx, Map<String, ?> context) throws GenericEntityException {
        String partyId = (String) context.get(PARTY_ID);
        Delegator delegator = dctx.getDelegator();
        GenericValue genericValue = EntityQuery.use(delegator).from(AFFILIATE).where(PARTY_ID, partyId).queryOne();
        genericValue.set(DATE_TIME_APPROVED, null);
        genericValue.set(STATUS, DECLINED);
        delegator.store(genericValue);
        return Map.of("disapprovedPartner", getAffiliateDTO((String) genericValue.get(PARTY_ID), false, dctx.getDelegator()));
    }

    /**
     * Creates Affiliate Promo for every {@link #PRODUCT_CATEGORY}.
     *
     * @param dctx    DispatchContext
     * @param context does not take any parameters
     * @throws GenericEntityException PRODUCT_CATEGORY entity does not exist
     * @see #getAndCreateIfNeededPromoForCategory(DispatchContext, Map)
     */
    private void generateAffPromoPerCategory(DispatchContext dctx, Map<String, ?> context) throws GenericEntityException {
        Delegator delegator = dctx.getDelegator();
        List<String> tags = new ArrayList<>();
        List<GenericValue> categories = EntityQuery.use(delegator).from(PRODUCT_CATEGORY).queryList();
        for (GenericValue category : categories) {
            Map<String, Object> categoryContext = new HashMap<>();
            categoryContext.put(PRODUCT_CATEGORY_ID, category.get(PRODUCT_CATEGORY_ID));
            getAndCreateIfNeededPromoForCategory(dctx, categoryContext);
        }
    }

    /**
     * Creates {@link #PRODUCT_PROMO} for given {@link #PRODUCT_CATEGORY_ID} if needed
     *
     * @param dctx    DispatchContext
     * @param context requires PRODUCT_CATEGORY_ID
     * @return Affiliate ProductPromo for respective PRODUCT_CATEGORY_ID
     * @throws GenericEntityException in case such category does not exist
     */
    public GenericValue getAndCreateIfNeededPromoForCategory(DispatchContext dctx, Map<String, ?> context) throws GenericEntityException {
        Delegator delegator = dctx.getDelegator();
        String productCategoryId = (String) context.get(PRODUCT_CATEGORY_ID);

        GenericValue promoForCategory = EntityQuery.use(delegator).from(PRODUCT_PROMO).where(PROMO_NAME, AFFILIATE_DISCOUNT, PROMO_TEXT, productCategoryId).queryOne();

        if (promoForCategory == null) {
            promoForCategory = createPromoForCategory(dctx, context);
        }

        return promoForCategory;
    }

    /**
     * Creates {@link #AFFILIATE_CODE} for respective {@link #PARTY_ID}
     *
     * @param dctx    DispatchContext
     * @param context requires PRODUCT_CATEGORY_ID, optional {@link #IS_DEFAULT} to make code default,
     * @return created AffiliateCode
     * @throws GenericEntityException in case such category does not exist
     */
    public Map<String, Object> createAffiliateCode(DispatchContext dctx, Map<String, ?> context) throws GenericEntityException {
        GenericValue discountCode = createDiscountCode(dctx, context);

        String partyId = (String) context.get(PARTY_ID);
        Delegator delegator = dctx.getDelegator();
        checkApprovedAffiliate(partyId, dctx.getDelegator());
        boolean isDefault = context.get(IS_DEFAULT) != null ? (Boolean) context.get(IS_DEFAULT) : false;
        GenericValue genericValue = delegator.makeValue(AFFILIATE_CODE, UtilMisc.toMap(PARTY_ID, partyId, AFFILIATE_CODE_ID, discountCode.get(PRODUCT_PROMO_CODE_ID), IS_DEFAULT, isDefault, PRODUCT_CATEGORY_ID, context.get(PRODUCT_CATEGORY_ID)));
        delegator.create(genericValue);

        Timestamp tm = new Timestamp(2208981600000L);

        GenericValue commission = EntityQuery.use(delegator).from(PRODUCT_CATEGORY).where(PRODUCT_CATEGORY_ID, context.get(PRODUCT_CATEGORY_ID)).queryOne();

        if (commission.getString(AFFILIATE_COMMISSION) == null) {
            commission.set(AFFILIATE_COMMISSION, DEFAULT_AFFILIATE_COMMISSION);
            delegator.store(commission);
        }

        GenericValue agreement = delegator.makeValue("Agreement", UtilMisc.toMap("agreementId", discountCode.get(PRODUCT_PROMO_CODE_ID), "partyIdFrom", "admin", "agreementDate", tm, "agreementTypeId", "COMMISSION_AGREEMENT", "partyIdTo", partyId, AFFILIATE_CODE_ID, discountCode.get(PRODUCT_PROMO_CODE_ID), PRODUCT_CATEGORY_ID, context.get(PRODUCT_CATEGORY_ID)));
        delegator.create(agreement);
        return Map.of("createdCode", genericValue);
    }

    /**
     * Sets affiliate commission rate for given {@link #PRODUCT_CATEGORY_ID}
     *
     * @param dctx    DispatchContext
     * @param context requires PRODUCT_CATEGORY_ID, {@link #AFFILIATE_COMMISSION}
     * @return changed ProductCategory entity
     * @throws GenericEntityException in case such category does not exist
     */
    public Map<String, GenericValue> setCommission(DispatchContext dctx, Map<String, ?> context) throws GenericEntityException {
        String productCategory = (String) context.get(PRODUCT_CATEGORY_ID);
        Delegator delegator = dctx.getDelegator();
        GenericValue genericValue = EntityQuery.use(delegator).from(PRODUCT_CATEGORY).where(PRODUCT_CATEGORY_ID, productCategory).queryOne();
        genericValue.set(AFFILIATE_COMMISSION, context.get(AFFILIATE_COMMISSION));
        delegator.store(genericValue);
        return Map.of("productCategory", genericValue);
    }

    /**
     * Creates ProductPromoCode for respective {@link #PRODUCT_CATEGORY_ID}
     *
     * @param dctx    DispatchContext
     * @param context requires PRODUCT_CATEGORY_ID, {@link #AFFILIATE_COMMISSION}
     * @return created discount code entity
     * @throws GenericEntityException in case such category does not exist
     */
    public GenericValue createDiscountCode(DispatchContext dctx, Map<String, ?> context) throws GenericEntityException {
        Delegator delegator = dctx.getDelegator();

        GenericValue promoForCategory = getAndCreateIfNeededPromoForCategory(dctx, context);

        String productPromoCodeId = delegator.getNextSeqId("ProductPromoCode");
        GenericValue promoCode = delegator.makeValue("ProductPromoCode", UtilMisc.toMap(PRODUCT_PROMO_CODE_ID, productPromoCodeId, PRODUCT_PROMO_ID, promoForCategory.get(PRODUCT_PROMO_ID), "userEntered", "Y", "requireEmailOrParty", "N"));
        delegator.create(promoCode);

        return promoCode;
    }


    /**
     * Get affiliate discounts information
     *
     * @param dctx    DispatchContext
     * @param context does not require any parameters
     * @return list of {@link SimpleDiscountDTO}
     * @throws GenericEntityException in case {@link #PRODUCT_PROMO} does not exist
     */
    public Map<String, List<SimpleDiscountDTO>> getAffiliateDiscounts(DispatchContext dctx, Map<String, ?> context) throws GenericEntityException {
        Delegator delegator = dctx.getDelegator();
        List<GenericValue> productPromos = EntityQuery.use(delegator).from(PRODUCT_PROMO).where(PROMO_NAME, AFFILIATE_DISCOUNT).queryList();

        List<SimpleDiscountDTO> discounts = new ArrayList<>();

        for (GenericValue productPromo : productPromos) {
            discounts.add(getAffiliateDiscount(dctx, new HashMap<>(Map.of(PRODUCT_CATEGORY_ID, productPromo.get(PROMO_TEXT)))).get(DISCOUNT));
        }

        return new HashMap<>(Map.of("discounts", discounts));
    }

    /**
     * Get affiliate discount for respective {@link #PRODUCT_CATEGORY_ID}
     *
     * @param dctx    DispatchContext
     * @param context requires PRODUCT_CATEGORY_ID
     * @return respective {@link SimpleDiscountDTO}
     * @throws GenericEntityException in case such category does not exist
     */
    public Map<String, SimpleDiscountDTO> getAffiliateDiscount(DispatchContext dctx, Map<String, ?> context) throws GenericEntityException {
        Delegator delegator = dctx.getDelegator();

        GenericValue productPromo = EntityQuery.use(delegator).from(PRODUCT_PROMO).where(PROMO_NAME, AFFILIATE_DISCOUNT, PROMO_TEXT, context.get(PRODUCT_CATEGORY_ID)).queryOne();
        GenericValue productPromoAction = EntityQuery.use(delegator).from(PRODUCT_PROMO_ACTION).where(PRODUCT_PROMO_ID, productPromo.get(PRODUCT_PROMO_ID)).queryOne();

        return new HashMap<>(Map.of(DISCOUNT, new SimpleDiscountDTO((String) productPromo.get(PRODUCT_PROMO_ID), (String) productPromo.get(PROMO_TEXT), productPromoAction.getDouble(AMOUNT))));
    }

    /**
     * Set affiliate discount {@link #AMOUNT} for respective respective {@link #PRODUCT_CATEGORY_ID}
     *
     * @param dctx    DispatchContext
     * @param context requires PRODUCT_CATEGORY_ID, AMOUNT
     * @return updated {@link SimpleDiscountDTO}
     * @throws GenericEntityException in case such category does not exist
     */
    public Map<String, SimpleDiscountDTO> setAffiliateDiscount(DispatchContext dctx, Map<String, ?> context) throws GenericEntityException {
        Delegator delegator = dctx.getDelegator();
        GenericValue productPromo = EntityQuery.use(delegator).from(PRODUCT_PROMO).where(PROMO_NAME, AFFILIATE_DISCOUNT, PROMO_TEXT, context.get(PRODUCT_CATEGORY_ID)).queryOne();
        GenericValue productPromoAction = EntityQuery.use(delegator).from(PRODUCT_PROMO_ACTION).where(PRODUCT_PROMO_ID, productPromo.get(PRODUCT_PROMO_ID)).queryOne();

        productPromoAction.set(AMOUNT, context.get(AMOUNT));
        delegator.store(productPromoAction);

        return new HashMap<>(Map.of(DISCOUNT, new SimpleDiscountDTO((String) productPromo.get(PRODUCT_PROMO_ID), (String) productPromo.get(PROMO_TEXT), productPromoAction.getDouble(AMOUNT))));
    }

    /**
     * Creates {@link #PRODUCT_PROMO} for given {@link #PRODUCT_CATEGORY_ID}
     *
     * @param dctx    DispatchContext
     * @param context requires PRODUCT_CATEGORY_ID
     * @return Affiliate ProductPromo for respective PRODUCT_CATEGORY_ID
     * @throws GenericEntityException in case such category does not exist
     */
    public GenericValue createPromoForCategory(DispatchContext dctx, Map<String, ?> context) {
        Delegator delegator = dctx.getDelegator();
        String productCategoryId = (String) context.get(PRODUCT_CATEGORY_ID);
        GenericValue promoValue = null;
        try {
            GenericValue productCategory = EntityQuery.use(delegator).from(PRODUCT_CATEGORY).where(PRODUCT_CATEGORY_ID, productCategoryId).queryOne();

            if (productCategory == null) {
                throw new IllegalArgumentException("productCategoryId " + productCategoryId + " does not exist");
            }

            String promoId = delegator.getNextSeqId(PRODUCT_PROMO);
            promoValue = delegator.makeValue(PRODUCT_PROMO, UtilMisc.toMap(PRODUCT_PROMO_ID, promoId, "userEntered", "Y", "showToCustomer", "Y", "requireCode", "Y", PROMO_NAME, AFFILIATE_DISCOUNT, PROMO_TEXT, productCategoryId, "useLimitPerOrder", 1));
            delegator.create(promoValue);

            String promoRuleId = delegator.getNextSeqId("ProductPromoRule");
            GenericValue genericValue = delegator.makeValue("ProductPromoRule", UtilMisc.toMap(PRODUCT_PROMO_ID, promoId, PRODUCT_PROMO_RULE_ID, promoRuleId, "ruleName", AFFILIATE_DISCOUNT));
            delegator.create(genericValue);

            String promoActionId = delegator.getNextSeqId(PRODUCT_PROMO_ACTION);
            genericValue = delegator.makeValue(PRODUCT_PROMO_ACTION, UtilMisc.toMap(PRODUCT_PROMO_ID, promoId, PRODUCT_PROMO_RULE_ID, promoRuleId, "productPromoActionSeqId", promoActionId, "productPromoActionEnumId", "PROMO_ORDER_PERCENT", AMOUNT, "20", "orderAdjustmentTypeId", "PROMOTION_ADJUSTMENT"));
            delegator.create(genericValue);

            String promoCondId = delegator.getNextSeqId("ProductPromoCond");
            genericValue = delegator.makeValue("ProductPromoCond", UtilMisc.toMap(PRODUCT_PROMO_ID, promoId, PRODUCT_PROMO_RULE_ID, promoRuleId, "productPromoCondSeqId", promoCondId));
            delegator.create(genericValue);

            genericValue = delegator.makeValue("ProductPromoCategory", UtilMisc.toMap(PRODUCT_PROMO_ID, promoId, PRODUCT_PROMO_RULE_ID, promoRuleId, "productPromoCondSeqId", promoCondId, "productPromoActionSeqId", promoActionId, PRODUCT_CATEGORY_ID, productCategoryId, "andGroupId", "_NA_", "includeSubCategories",
                    "Y"));
            delegator.create(genericValue);

            // todo get store id
            genericValue = delegator.makeValue("ProductStorePromoAppl", UtilMisc.toMap("productStoreId", "9000", PRODUCT_PROMO_ID, promoId, "fromDate", UtilDateTime.nowTimestamp()));
            delegator.create(genericValue);
        } catch (Exception e) {
            Debug.logWarning(e.getMessage(), MODULE);
        }

        return promoValue;
    }

    /**
     * Delete {@link #AFFILIATE_CODE} for respective {@link #PARTY_ID} and {@link #AFFILIATE_CODE_ID}
     *
     * @param dctx    DispatchContext
     * @param context requires PARTY_ID, AFFILIATE_CODE_ID
     * @return deleted AFFILIATE_CODE
     * @throws GenericEntityException if AFFILIATE_CODE was not found
     */
    public Map<String, GenericValue> deleteAffiliateCode(DispatchContext dctx, Map<String, ?> context) throws GenericEntityException {
        String partyId = (String) context.get(PARTY_ID);
        String affCode = (String) context.get(AFFILIATE_CODE_ID);
        Delegator delegator = dctx.getDelegator();
        checkApprovedAffiliate(partyId, dctx.getDelegator());
        GenericValue genericValue = EntityQuery.use(delegator).from(AFFILIATE_CODE).where(PARTY_ID, partyId, AFFILIATE_CODE_ID, affCode).queryOne();
        if (genericValue.get(IS_DEFAULT).equals("N")) {
            GenericValue agreement = EntityQuery.use(delegator).from("Agreement").where(AFFILIATE_CODE_ID, affCode).queryOne();
            agreement.remove();
            genericValue.remove();
        } else {
            throw new IllegalArgumentException("Code is default and cannot be deleted");
        }
        return Map.of("deletedCode", genericValue);
    }

    /**
     * Disapproves and deletes all affiliate codes for respective {@link #PARTY_ID}
     *
     * @param dctx    DispatchContext
     * @param context requires PARTY_ID
     * @return disapproved {@link AffiliateDTO}
     * @throws GenericEntityException if such affiliate was not found
     * @see #disapprove(DispatchContext, Map)
     */
    public Map<String, AffiliateDTO> disableAffiliate(DispatchContext dctx, Map<String, ?> context) throws GenericEntityException {
        String partyId = (String) context.get(PARTY_ID);
        List<GenericValue> affCodes = getAffiliateCodes(dctx, context).get(AFFILIATE_DTOS);
        Delegator delegator = dctx.getDelegator();
        checkApprovedAffiliate(partyId, dctx.getDelegator());
        for (GenericValue affCode : affCodes) {
            String currentCode = (String) affCode.get(AFFILIATE_CODE_ID);
            GenericValue genericCode = EntityQuery.use(delegator).from(AFFILIATE_CODE).where(PARTY_ID, partyId, AFFILIATE_CODE_ID, currentCode).queryOne();
            genericCode.remove();
        }
        return disapprove(dctx, context);
    }

    /**
     * Creates affiliate for respective {@link #PARTY_ID} and sets status to {@link AffiliateDTO.Status#PENDING}
     *
     * @param dctx    DispatchContext
     * @param context requires PARTY_ID
     * @return created {@link AffiliateDTO}
     * @throws GenericEntityException if such person was not found
     */
    public Map<String, AffiliateDTO> createAffiliateForUserLogin(DispatchContext dctx, Map<String, ?> context) throws GenericEntityException {
        Map<String, Object> affiliateCreateContext = new HashMap<>();

        String userPartyId = (String) context.get(PARTY_ID);
        Delegator delegator = dctx.getDelegator();
        GenericValue userParty = EntityQuery
                .use(delegator)
                .from("Party")
                .where(PARTY_ID, userPartyId)
                .queryOne();


        if (userParty != null && !"PERSON".equals(userParty.getString("partyTypeId"))) {
            throw new IllegalArgumentException("User must be of PERSON group");
        }

        GenericValue checkExistence = EntityQuery.use(delegator).from(AFFILIATE).where(PARTY_ID, userPartyId).queryOne();
        if (checkExistence != null) {
            if (AffiliateDTO.Status.DECLINED.toString().equals(checkExistence.get(STATUS))) {
                checkExistence.set(STATUS, PENDING);
                delegator.store(checkExistence);
                return Map.of("createdAffiliate", getAffiliateDTO(userPartyId, false, dctx.getDelegator()));
            }
            throw new IllegalArgumentException("User already has applied for Affliate");
        }

        // create affiliate by for created/existing party
        affiliateCreateContext.put(PARTY_ID, userPartyId);
        affiliateCreateContext.put("locale", Locale.ENGLISH);
        PartyServices.createAffiliate(dctx, affiliateCreateContext);

        try {
            GenericValue genericValue = EntityQuery.use(delegator).from(AFFILIATE).where(PARTY_ID, userPartyId).queryOne();
            genericValue.set(STATUS, PENDING);
            delegator.store(genericValue);

            String affCode = (String) context.get("affCode");
            List<GenericValue> affiliateCodes = EntityQuery.use(delegator).from(AFFILIATE_CODE).queryList();
            String rootPartyId = (String) affiliateCodes.stream().filter(affDto -> affDto.get(AFFILIATE_CODE_ID).equals(affCode)).findFirst().orElse(null).get(PARTY_ID);

            genericValue.set("RootPartyId", rootPartyId);
        } catch (GenericEntityException | NullPointerException e) {
            Debug.logWarning(e.getMessage(), MODULE);
        }

        return Map.of("createdAffiliate", getAffiliateDTO(userPartyId, false, dctx.getDelegator()));
    }

    /**
     * Get partyId for respective userLoginId
     *
     * @param dctx    DispatchContext
     * @param context requires userLoginId
     * @return partyId or null if not found
     */
    public Map<String, Object> getPartyIdForUserId(DispatchContext dctx, Map<String, ?> context) {
        Map<String, Object> affiliateCreateContext = new HashMap<>();

        String userLoginId = (String) context.get("userLoginId");
        GenericValue currentUserLogin = ServiceUtil.getUserLogin(dctx, affiliateCreateContext, userLoginId);
        Map<String, Object> partyMap = new HashMap<>();
        partyMap.put(PARTY_ID, currentUserLogin.getString(PARTY_ID));
        return partyMap;
    }

    /**
     * Get list of {@link #AFFILIATE_CODE} for respective {@link #PARTY_ID}
     *
     * @param dctx    DispatchContext
     * @param context requires PARTY_ID
     * @return created {@link AffiliateDTO}
     * @throws GenericEntityException if such affiliate was not found
     */
    public Map<String, List<GenericValue>> getAffiliateCodes(DispatchContext dctx, Map<String, ?> context) throws GenericEntityException {
        String partyId = (String) context.get(PARTY_ID);
        Delegator delegator = dctx.getDelegator();
        checkApprovedAffiliate(partyId, dctx.getDelegator());
        return Map.of(AFFILIATE_DTOS, EntityQuery.use(delegator).from(AFFILIATE_CODE).where(PARTY_ID, partyId).queryList());
    }

    /**
     * Get {@link AffiliateDTO.Status} for respective {@link #PARTY_ID}
     *
     * @param dctx    DispatchContext
     * @param context requires PARTY_ID
     * @return created {@link AffiliateDTO}
     * @throws GenericEntityException if such affiliate was not found
     */
    public Map<String, String> getAffiliateStatus(DispatchContext dctx, Map<String, ?> context) throws GenericEntityException {
        String partyId = (String) context.get(PARTY_ID);
        GenericValue affiliate = EntityQuery
                .use(dctx.getDelegator())
                .from(AFFILIATE)
                .where(PARTY_ID, partyId)
                .queryOne();
        if (affiliate != null) {
            return Map.of(STATUS, affiliate.getString(STATUS));
        } else {
            return Map.of(STATUS, "NOT-PARTNER");
        }
    }

    /**
     * Checks if affiliate for respective partyId is approved
     *
     * @param partyId   affiliate partyId
     * @param delegator Delegator
     * @throws GenericEntityException if such affiliate was not found,
     *                                IllegalArgumentException if affiliate is not approved
     */
    private void checkApprovedAffiliate(String partyId, Delegator delegator) throws GenericEntityException {
        GenericValue userParty = EntityQuery
                .use(delegator)
                .from(AFFILIATE)
                .where(PARTY_ID, partyId)
                .queryOne();
        if (userParty == null) {
            throw new IllegalArgumentException("You are not an affiliate yet!");
        }

        boolean isApproved = userParty.get(DATE_TIME_APPROVED) != null;
        if (!isApproved) {
            throw new IllegalArgumentException("You are not approved yet!");

        }
    }

    /**
     * Get Person for respective {@link #PARTY_ID}
     *
     * @param delegator Delegator
     * @param partyId   {@link #PARTY_ID}
     * @return created {@link AffiliateDTO}
     */
    private GenericValue getPerson(String partyId, Delegator delegator) {
        GenericValue person = null;
        try {
            person = EntityQuery
                    .use(delegator)
                    .from("Person")
                    .where(PARTY_ID, partyId)
                    .queryOne();
        } catch (GenericEntityException e) {
            Debug.logError(e, MODULE);
        }

        return person;
    }

    /**
     * Get {@link AffiliateDTO} for respective {@link #PARTY_ID}
     *
     * @param partyId           partyId
     * @param withSubAffiliates whether to fetch subaffiliates
     * @param delegator         Delegator
     * @return respective AffiliateDTO
     */
    public AffiliateDTO getAffiliateDTO(String partyId, boolean withSubAffiliates, Delegator delegator) {
        AffiliateDTO affiliateDTO = new AffiliateDTO();
        GenericValue person = getPerson(partyId, delegator);

        GenericValue affiliate;
        List<AffiliateDTO> subAffiliates = new ArrayList<>();
        try {
            affiliate = EntityQuery
                    .use(delegator)
                    .from(AFFILIATE)
                    .where(PARTY_ID, partyId)
                    .queryOne();

            if (withSubAffiliates) {
                List<GenericValue> genericSubAffiliates = EntityQuery
                        .use(delegator)
                        .from(AFFILIATE)
                        .where("RootPartyId", partyId)
                        .queryList();

                subAffiliates = genericSubAffiliates.stream().map(x -> getAffiliateDTO((String) x.get(PARTY_ID), false, delegator)).collect(Collectors.toList());
            }
        } catch (GenericEntityException e) {
            throw new IllegalArgumentException("No such Affiliate found!");
        }

        affiliateDTO.setPartyId(partyId);
        affiliateDTO.setEmail(getEmail(partyId, delegator));
        affiliateDTO.setFirstName((String) person.get("firstName"));
        affiliateDTO.setLastName((String) person.get("lastName"));
        affiliateDTO.setStatus(AffiliateDTO.Status.valueOf((String) affiliate.get(STATUS)));
        affiliateDTO.setDate((Timestamp) affiliate.get(DATE_TIME_APPROVED));
        affiliateDTO.setSubAffiliates(subAffiliates);

        return affiliateDTO;
    }

    /**
     * Set email for respective partyId.
     *
     * @param partyId   party to find email of
     * @param delegator Delegator
     * @return email, empty string if not found
     */
    private String getEmail(String partyId, Delegator delegator) {
        GenericValue partyContactMechPurpose;
        String email = "";
        try {
            partyContactMechPurpose = EntityQuery
                    .use(delegator)
                    .from("PartyContactMechPurpose")
                    .where(PARTY_ID, partyId, "contactMechPurposeTypeId", "PRIMARY_EMAIL")
                    .queryOne();

            GenericValue contactMech = EntityQuery
                    .use(delegator)
                    .from("ContactMech")
                    .where("contactMechId", partyContactMechPurpose.get("contactMechId"), "contactMechTypeId", "EMAIL_ADDRESS")
                    .queryOne();

            email = (String) contactMech.get("infoString");
        } catch (Exception e) {
            Debug.logError(e, MODULE);
        }

        return email;
    }

    /**
     * Set global affiliate settings.
     *
     * @param dctx    DispatchContext
     * @param context requires {@link #SETTINGS_TYPE}, {@link #COMMISSION_FREQUENCY}, {@link #MULTI_LEVEL_AFFILIATION}, {@link #CODE_COOKIE_DURATION}
     * @return updated {@link SimpleDiscountDTO}
     * @throws GenericEntityException in case such SETTINGS_TYPE does not exist
     */
    public Map<String, GenericValue> setAffiliateSettings(DispatchContext dctx, Map<String, ?> context) throws GenericEntityException {
        Delegator delegator = dctx.getDelegator();
        String settingsType = (String) context.get(SETTINGS_TYPE);
        GenericValue settings = EntityQuery.use(delegator).from("AffiliateSettings").where(SETTINGS_TYPE, settingsType).queryOne();

        if (settings == null) {
            settings = delegator.makeValue("AffiliateSettings", UtilMisc.toMap(SETTINGS_TYPE, settingsType,
                    COMMISSION_FREQUENCY, context.get(COMMISSION_FREQUENCY), MULTI_LEVEL_AFFILIATION, context.get(MULTI_LEVEL_AFFILIATION),
                    CODE_COOKIE_DURATION, context.get(CODE_COOKIE_DURATION)));
            delegator.create(settings);
        } else {
            settings.set(COMMISSION_FREQUENCY, context.get(COMMISSION_FREQUENCY));
            settings.set(MULTI_LEVEL_AFFILIATION, context.get(MULTI_LEVEL_AFFILIATION));
            settings.set(CODE_COOKIE_DURATION, context.get(CODE_COOKIE_DURATION));
            delegator.store(settings);
        }
        return Map.of("settings", settings);
    }


}
