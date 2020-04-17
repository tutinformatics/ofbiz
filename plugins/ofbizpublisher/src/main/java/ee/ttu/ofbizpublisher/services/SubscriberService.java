package ee.ttu.ofbizpublisher.services;

import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import ee.ttu.ofbizpublisher.model.SubscriberDTO;
import org.apache.ofbiz.service.DispatchContext;
import services.OfbizSubscriberServices;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SubscriberService {

    Delegator delegator;
    DispatchContext dispatchContext;

    public SubscriberService(DispatchContext dpc) {
        dispatchContext = dpc;
        delegator = dpc.getDelegator();
    }


    public List<SubscriberDTO> getSubscribers() throws GenericEntityException {
        List<GenericValue> genericValues = EntityQuery.use(delegator).from("OfbizSubscriber").queryList();
        return genericValues.stream().map(x -> getOfbizSubscribersDTO((String) x.get("OfbizSubscriberId"))).collect(Collectors.toList());
    }

    public SubscriberDTO getOfbizSubscribersDTO(String ofbizSubscriberId) {
        SubscriberDTO subscriberDTO = new SubscriberDTO();

        GenericValue subscriber = null;
        try {
            subscriber = EntityQuery
                    .use(delegator)
                    .from("OfbizSubscriber")
                    .where("OfbizSubscriberId", ofbizSubscriberId)
                    .queryOne();
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }

        subscriberDTO.setSubscriberId((String) subscriber.get("OfbizSubscriberId"));
        subscriberDTO.setSubscriberName((String) subscriber.get("OfbizSubscriberName"));
        subscriberDTO.setTopic((String) subscriber.get("topic"));
        subscriberDTO.setDescription((String) subscriber.get("description"));
        subscriberDTO.setFilter((String) subscriber.get("filter"));
        return subscriberDTO;
    }

    public void createSubscriber(Map<String, Object> data) {
        Map<String, Object> subscriberContext = new HashMap<>();
        subscriberContext.put("OfbizSubscriberId", data.get("OfbizSubscriberId"));
        subscriberContext.put("OfbizSubscriberName", data.get("OfbizSubscriberName"));
        subscriberContext.put("topic", data.get("topic"));
        subscriberContext.put("description", data.get("description"));
        subscriberContext.put("filter", data.get("filter"));
        OfbizSubscriberServices ofbizSubscriberServices = new OfbizSubscriberServices();
        ofbizSubscriberServices.createOfbizSubscriber(dispatchContext, subscriberContext);
    }
}
