package ee.taltech.marketing.affiliate.connector.camel.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.camel.Exchange;
import org.apache.camel.component.sparkrest.SparkMessage;
import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.party.party.PartyServices;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.GenericDispatcherFactory;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PartyService {

    Delegator delegator;
    GenericDispatcherFactory genericDispatcherFactory;
    LocalDispatcher dispatcher;
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static final String module = PartyServices.class.getName();
    private DispatchContext dispatchCtx;


    public PartyService(Delegator delegator) {
        this.delegator = delegator;
        this.genericDispatcherFactory = new GenericDispatcherFactory();
        this.dispatcher = genericDispatcherFactory.createLocalDispatcher("myDispatcher", delegator);
        this.dispatchCtx = new DispatchContext("myContext", null, dispatcher);
    }


    /**
     * @param exchange - request that is wrapped by camel with additional information added during routing
     * @return JSON with all the parties found with a given partyId
     */
    public String getPartyById(Exchange exchange) {
        String id = getParamValueFromExchange("id", exchange);

        DispatchContext myContext = new DispatchContext("myContext", null, dispatcher);
        Map<String, Object> context = new HashMap<>();
        context.put("idToFind", id);
        context.put("partyIdentificationTypeId", null);
        context.put("searchPartyFirst", null);
        context.put("searchAllIdContext", null);

        return gson.toJson(PartyServices.findPartyById(myContext, context));
    }

    /**
     * Get unconfirmed affiliates (such that have no approval date)
     *
     * @return
     * @throws GenericEntityException
     */
    public String getUnconfirmedAffiliates() throws GenericEntityException {
        List<GenericValue> genericValues = EntityQuery.use(delegator).from("Affiliate").where("dateTimeApproved", null).queryList();
        return gson.toJson(genericValues);
    }

    /**
     * Get all affiliates
     * @return
     * @throws GenericEntityException
     */
    public String getAffiliates() throws GenericEntityException {
        List<GenericValue> genericValues = EntityQuery.use(delegator).from("Affiliate").queryList();
        return gson.toJson(genericValues);
    }

    public String approve(Exchange exchange) throws GenericEntityException {
        String partyId = parseJson("partyId", exchange);
        GenericValue genericValue = EntityQuery.use(delegator).from("Affiliate").where("partyId", partyId).queryOne();
        genericValue.set("dateTimeApproved", new Timestamp(System.currentTimeMillis()));
        delegator.store(genericValue);
        return gson.toJson(genericValue);
    }

    public String disapprove(Exchange exchange) throws GenericEntityException {
        String partyId = parseJson("partyId", exchange);
        GenericValue genericValue = EntityQuery.use(delegator).from("Affiliate").where("partyId", partyId).queryOne();
        genericValue.set("dateTimeApproved", null);
        delegator.store(genericValue);
        return gson.toJson(genericValue);
    }


    /**
     * @param exchange - http request wrapped by Camel
     * @return - status of operation
     */
    public String createAffiliateForUserLogin(Exchange exchange) throws GenericEntityException {
        Map<String, Object> affiliateCreateContext = new HashMap<>();

        //Retrieve UserLogin via userLoginId
        String userLoginId = parseJson("userLoginId", exchange);
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
                return gson.toJson(Map.of("status", "user already has party of another type"));
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
            String rootPartyId = parseJson("rootPartyId", exchange);
            GenericValue genericValue = EntityQuery.use(delegator).from("Affiliate").where("partyId", userPartyId).queryOne();
            genericValue.set("RootPartyId", rootPartyId);
            delegator.store(genericValue);
        } catch (GenericEntityException | NullPointerException e) {
            Debug.logWarning(e.getMessage(), module);
        }

        return gson.toJson(Map.of("status", "ok"));
    }

    /**
     * handled map = Map<ParameterName, ParameterValue>
     * this map can be found by the following path:
     * exchange -> in -> request -> params
     *
     * @param paramName - name of parameter provided with query in requested url
     * @param exchange  - request wrapped by camel
     * @return value of parameter
     */
    private String getParamValueFromExchange(String paramName, Exchange exchange) {
        SparkMessage msg = (SparkMessage) exchange.getIn();
        Map<String, String> params = msg.getRequest().params();
        String sparkParamName = ":" + paramName;
        return params.get(sparkParamName);
    }

    /**
     * @param property - name of property to retrieve from json
     * @param exchange - object populated by Camel
     * @return string value of json property
     */
    private String parseJson(String property, Exchange exchange) {
        SparkMessage msg = (SparkMessage) exchange.getIn();
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(msg.getBody().toString()).getAsJsonObject();
        return obj.get(property).getAsString();
    }

}
