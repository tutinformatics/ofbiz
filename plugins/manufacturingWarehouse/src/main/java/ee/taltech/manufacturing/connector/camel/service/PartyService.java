package ee.taltech.accounting.connector.camel.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import java.util.ArrayList;
import java.util.List;

public class PartyService {

    Delegator delegator;

    public PartyService(Delegator delegator) {
        this.delegator = delegator;
    }

    public static final String module = PartyService.class.getName();


    public String getParties() {
        List<GenericValue> orderItems = new ArrayList<>();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            orderItems = EntityQuery.use(delegator)
                    .from("Party")
                    .queryList();
        } catch (GenericEntityException e) {
            e.printStackTrace();
            GenericValue error = new GenericValue();
            error.put("Error", e);
            orderItems.add(error);
        }
        return gson.toJson(orderItems);
    }

}
