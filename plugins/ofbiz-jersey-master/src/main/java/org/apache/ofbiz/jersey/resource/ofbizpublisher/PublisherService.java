package org.apache.ofbiz.jersey.resource.ofbizpublisher;

import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;

import java.util.List;
import java.util.stream.Collectors;

public class PublisherService {

    Delegator delegator;

    public PublisherService(Delegator delegator) {
        this.delegator = delegator;
    }


    public List<OblizPublisherDTO> getPublishers() throws GenericEntityException {
        List<GenericValue> genericValues = EntityQuery.use(delegator).from("OfbizPublisher").queryList();
        return genericValues.stream().map(x -> getOfbizPublisherDTO((String) x.get("OfbizPublisherId"))).collect(Collectors.toList());
    }

    public OblizPublisherDTO getOfbizPublisherDTO(String ofbizPublisherId) {
        OblizPublisherDTO oblizPublisherDTO = new OblizPublisherDTO();

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

        oblizPublisherDTO.setPublisherId((String) ofbizPublisher.get("OfbizPublisherId"));
        oblizPublisherDTO.setTopic((String) ofbizPublisher.get("topic"));
        oblizPublisherDTO.setDescription((String) ofbizPublisher.get("description"));
        oblizPublisherDTO.setFilter((String) ofbizPublisher.get("filter"));
        return oblizPublisherDTO;
    }
}
