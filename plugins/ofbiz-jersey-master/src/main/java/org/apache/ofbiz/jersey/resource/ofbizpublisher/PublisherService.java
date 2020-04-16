package org.apache.ofbiz.jersey.resource.ofbizpublisher;

import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.DispatchContext;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PublisherService {

    Delegator delegator;
    DispatchContext dispatchContext;

    public PublisherService(DispatchContext dpc) {
        dispatchContext = dpc;
        delegator = dpc.getDelegator();
    }


    public List<ObfizPublisherDTO> getPublishers() throws GenericEntityException {
        List<GenericValue> genericValues = EntityQuery.use(delegator).from("OfbizPublisher").queryList();
        return genericValues.stream().map(x -> getOfbizPublisherDTO((String) x.get("OfbizPublisherId"))).collect(Collectors.toList());
    }

    public ObfizPublisherDTO getOfbizPublisherDTO(String ofbizPublisherId) {
        ObfizPublisherDTO obfizPublisherDTO = new ObfizPublisherDTO();

        GenericValue ofbizPublisher = null;
        try {
            ofbizPublisher = EntityQuery
                    .use(delegator)
                    .from("OfbizPublisher")
                    .where("OfbizPublisherId", ofbizPublisherId)
                    .queryOne();
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }

        obfizPublisherDTO.setPublisherId((String) ofbizPublisher.get("OfbizPublisherId"));
        obfizPublisherDTO.setTopic((String) ofbizPublisher.get("topic"));
        obfizPublisherDTO.setDescription((String) ofbizPublisher.get("description"));
        obfizPublisherDTO.setFilter((String) ofbizPublisher.get("filter"));
        return obfizPublisherDTO;
    }

    public ObfizPublisherDTO createPublisher(Map<String, Object> data) {
    }
}
