package org.apache.ofbiz.jersey.resource.ofbizpublisher;

import ee.ttu.ofbizpublisher.services.OfbizPublisherServices;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.party.party.PartyServices;
import org.apache.ofbiz.service.DispatchContext;

import java.util.HashMap;
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
        Map<String, Object> publisherContext = new HashMap<>();

        // create affiliate by for created/existing party
        publisherContext.put("OfbizPublisherId", data.get("OfbizPublisherId"));
        publisherContext.put("topic", data.get("topic"));
        publisherContext.put("description", data.get("description"));
        publisherContext.put("filter", data.get("filter"));
        OfbizPublisherServices ofbizPublisherServices = new OfbizPublisherServices();
        ofbizPublisherServices.createOfbizPublisher(dispatchContext, publisherContext);

        return getOfbizPublisherDTO((String) data.get("OfbizPublisherId"));
    }
}
