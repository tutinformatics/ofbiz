package ee.taltech.manufacturing.connector.camel.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;

import java.util.ArrayList;
import java.util.List;

public class WorkEffortService {

    Delegator delegator;

    public WorkEffortService(Delegator delegator) {
        this.delegator = delegator;
    }

    public static final String module = WorkEffortService.class.getName();

    //@Deprecated
    /*
    public String getWorkEfforts(DispatchContext dctx, Map<String, ?> context) {
        List<GenericValue> orderItems;
        Delegator delegator = dctx.getDelegator();
        try {
            orderItems = EntityQuery.use(delegator)
                    .from("WorkEffort")
                    .queryList();
            System.out.println(orderItems);
        } catch (GenericEntityException e) {
            e.printStackTrace();
            GenericValue error = new GenericValue();
            error.put("Error", e);
            orderItems.add(error);
        }

        return gson.toJson(orderItems);
    }*/

    public String getWorkEfforts() {
        List<GenericValue> orderItems = new ArrayList<>();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            orderItems = EntityQuery.use(delegator)
                    .from("WorkEffort")
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
