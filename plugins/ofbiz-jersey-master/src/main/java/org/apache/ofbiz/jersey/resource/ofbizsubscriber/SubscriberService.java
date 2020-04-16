package org.apache.ofbiz.jersey.resource.ofbizsubscriber;

import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.DispatchContext;

import java.util.List;
import java.util.stream.Collectors;

public class SubscriberService {

    Delegator delegator;
    DispatchContext dispatchContext;

    public SubscriberService(DispatchContext dpc) {
        dispatchContext = dpc;
        delegator = dpc.getDelegator();
    }


    public List<ObfizSubscriberDTO> getSubscribers() throws GenericEntityException {
        List<GenericValue> genericValues = EntityQuery.use(delegator).from("OfbizSubscriber").queryList();
        return genericValues.stream().map(x -> getOfbizSubscribersDTO((String) x.get("OfbizSubscriberId"))).collect(Collectors.toList());
    }

    public ObfizSubscriberDTO getOfbizSubscribersDTO(String ofbizSubscriberId) {
        ObfizSubscriberDTO obfizSubscriberDTO = new ObfizSubscriberDTO();

        GenericValue ofbizSubscriber = null;
        try {
            ofbizSubscriber = EntityQuery
                    .use(delegator)
                    .from("OfbizSubscriber")
                    .where("OfbizSubscriberId", ofbizSubscriberId)
                    .queryOne();
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }

        obfizSubscriberDTO.setSubscriberId((String) ofbizSubscriber.get("OfbizSubscriberId"));
        obfizSubscriberDTO.setTopic((String) ofbizSubscriber.get("topic"));
        obfizSubscriberDTO.setDescription((String) ofbizSubscriber.get("description"));
        obfizSubscriberDTO.setFilter((String) ofbizSubscriber.get("filter"));
        return obfizSubscriberDTO;
    }
}
