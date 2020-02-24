package ee.taltech.marketing.affiliate.connector.camel.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.party.party.PartyServices;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.GenericDispatcherFactory;
import org.apache.ofbiz.service.LocalDispatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PartyService {

    Delegator delegator;
    GenericDispatcherFactory genericDispatcherFactory;
    LocalDispatcher dispather;

    public PartyService(Delegator delegator) {
        this.delegator = delegator;
        this.genericDispatcherFactory = new GenericDispatcherFactory();
        this.dispather = genericDispatcherFactory.createLocalDispatcher("myDispather", delegator);
    }

    public static final String module = PartyService.class.getName();

    // alternative way
    public String getAdminParties() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        DispatchContext myContext = new DispatchContext("myContext", null, dispather);
        Map<String, Object> context = new HashMap<>();
        context.put("idToFind", "admin");
        context.put("partyIdentificationTypeId", null);
        context.put("searchPartyFirst", null);
        context.put("searchAllIdContext", null);

        return gson.toJson(PartyServices.findPartyById(myContext, context));
    }

    public String getParties() {
        List<GenericValue> parties = new ArrayList<>();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            parties = EntityQuery.use(delegator)
                    .from("Party")
                    .queryList();
        } catch (GenericEntityException e) {
            e.printStackTrace();
            GenericValue error = new GenericValue();
            error.put("Error", e);
            parties.add(error);
        }
        return gson.toJson(parties);
    }

}
