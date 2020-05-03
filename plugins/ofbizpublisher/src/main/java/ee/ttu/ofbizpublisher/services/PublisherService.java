package ee.ttu.ofbizpublisher.services;

import com.google.gson.Gson;
import ee.ttu.ofbizpublisher.OfbizPublisherServices;
import ee.ttu.ofbizpublisher.model.PublisherDTO;
import ee.ttu.ofbizpublisher.mqtt.ConnectionBinding;
import ee.ttu.ofbizpublisher.mqtt.Publisher;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.model.ModelEntity;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.jersey.util.QueryParamStringConverter;
import org.apache.ofbiz.service.ServiceUtil;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;

import java.util.*;
import java.util.stream.Collectors;

public class PublisherService {

    Delegator delegator;

    public PublisherService(Delegator delegator) {
        this.delegator = delegator;
    }

    public List<PublisherDTO> getPublishers() throws GenericEntityException {
        List<GenericValue> genericValues = EntityQuery.use(delegator).from("OfbizPublisher").queryList();
        return genericValues.stream().map(x -> getOfbizPublisherDTO((String) x.get("OfbizPublisherId"))).collect(Collectors.toList());
    }

    public PublisherDTO getOfbizPublisherDTO(String ofbizPublisherId) {
        PublisherDTO ofbizPublisherDTO = new PublisherDTO();
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
        ofbizPublisherDTO.setPublisherId((String) ofbizPublisher.get("OfbizPublisherId"));
        ofbizPublisherDTO.setEntityName((String) ofbizPublisher.get("OfbizEntityName"));
        ofbizPublisherDTO.setTopic((String) ofbizPublisher.get("topic"));
        ofbizPublisherDTO.setDescription((String) ofbizPublisher.get("description"));
        ofbizPublisherDTO.setFilter((String) ofbizPublisher.get("filter"));
        return ofbizPublisherDTO;
    }

    public void createPublisher(Map<String, Object> data) throws Exception {
        Map<String, Object> publisherContext = new HashMap<>();
        publisherContext.put("OfbizPublisherId", data.get("OfbizPublisherId"));
        publisherContext.put("OfbizEntityName", data.get("OfbizEntityName"));
        publisherContext.put("topic", data.get("topic"));
        publisherContext.put("description", data.get("description"));
        publisherContext.put("filter", data.get("filter"));
        setPublisherData(data.get("OfbizEntityName").toString(), data.get("topic").toString(), data.get("filter").toString());
        OfbizPublisherServices ofbizPublisherServices = new OfbizPublisherServices();
        ofbizPublisherServices.createOfbizPublisher(delegator, publisherContext);
    }

    public void setPublisherData(String entityName, String topic, String filterParams) throws Exception {
        Gson gson = new Gson();
        Object filter = gson.fromJson(filterParams, Object.class);
        List<Map<String, List<String>>> queryList = (List<Map<String, List<String>>>) filter;
        ModelEntity model = delegator.getModelReader().getModelEntity(entityName);
        List<GenericValue> genericValues = new ArrayList<>();
        for (Map<String, List<String>> query : queryList) {
            Map<String, Object> queryParams = query.entrySet().stream()
                    .map(x -> new AbstractMap.SimpleEntry<>(x.getKey(), QueryParamStringConverter.convert(x.getValue().get(0), model.getField(x.getKey()).getType())))
                    .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
            genericValues.addAll(EntityQuery.use(delegator).from(entityName).where(queryParams).queryList());
        }
        if (queryList.isEmpty()) {
            genericValues = EntityQuery.use(delegator).from(entityName).queryList();
        }
        String publisherId = UUID.randomUUID().toString();
        IMqttClient publisher = new MqttClient("tcp://mqtt.eclipse.org:1883", publisherId);
        ConnectionBinding mqttClientService = new ConnectionBinding(publisher);
        mqttClientService.makeConnection();
        Publisher mqttService = new Publisher(publisher, topic);
        mqttService.call(genericValues);
    }

    public void setPublisherDataWithPublisher(String entityName, String topic, String filterParams) throws Exception {
        Gson gson = new Gson();
        Object filter = gson.fromJson(filterParams, Object.class);
        List<Map<String, List<String>>> queryList = (List<Map<String, List<String>>>) filter;
        ModelEntity model = delegator.getModelReader().getModelEntity(entityName);
        List<GenericValue> genericValues = new ArrayList<>();
        for (Map<String, List<String>> query : queryList) {
            Map<String, Object> queryParams = query.entrySet().stream()
                    .map(x -> new AbstractMap.SimpleEntry<>(x.getKey(), QueryParamStringConverter.convert(x.getValue().get(0), model.getField(x.getKey()).getType())))
                    .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
            genericValues.addAll(EntityQuery.use(delegator).from(entityName).where(queryParams).queryList());
        }
        if (queryList.isEmpty()) {
            genericValues = EntityQuery.use(delegator).from(entityName).queryList();
        }
        Publisher ofbizPublisher = (Publisher) EntityQuery
                .use(delegator)
                .from("OfbizPublisher")
                .where("topic", topic)
                .queryOne();
        ofbizPublisher.callWithTopic(genericValues, topic, ofbizPublisher);
    }

    public GenericValue deletePublisher(String ofbizPublisherId) throws GenericEntityException {
        checkPublisher(ofbizPublisherId);
        GenericValue genericValue = EntityQuery
                .use(delegator)
                .from("OfbizPublisher")
                .where("OfbizPublisherId", ofbizPublisherId)
                .queryOne();
        genericValue.remove();
        return genericValue;
    }

    private void checkPublisher(String ofbizPublisherId) throws GenericEntityException {
        GenericValue ofbizPublisher = EntityQuery
                .use(delegator)
                .from("OfbizPublisher")
                .where("OfbizPublisherId", ofbizPublisherId)
                .queryOne();
        if (ofbizPublisher == null) {
            ServiceUtil.returnError("No ofbizPublisher found!");
        }
    }
}
