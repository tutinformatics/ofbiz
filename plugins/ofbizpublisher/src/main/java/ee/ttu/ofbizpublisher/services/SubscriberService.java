package ee.ttu.ofbizpublisher.services;

import ee.ttu.ofbizpublisher.model.SubscriberDTO;
import ee.ttu.ofbizpublisher.mqtt.ConnectionBinding;
import ee.ttu.ofbizpublisher.mqtt.Subscriber;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.ServiceUtil;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import services.OfbizSubscriberServices;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
        subscriberDTO.setEntityName((String) subscriber.get("OfbizEntityName"));
        subscriberDTO.setTopic((String) subscriber.get("topic"));
        subscriberDTO.setDescription((String) subscriber.get("description"));
        subscriberDTO.setFilter((String) subscriber.get("filter"));
        return subscriberDTO;
    }

    public void createSubscriber(Map<String, Object> data) throws MqttException, InterruptedException {
        Map<String, Object> subscriberContext = new HashMap<>();
        subscriberContext.put("OfbizSubscriberId", data.get("OfbizSubscriberId"));
        subscriberContext.put("OfbizEntityName", data.get("OfbizEntityName"));
        subscriberContext.put("topic", data.get("topic"));
        subscriberContext.put("description", data.get("description"));
        subscriberContext.put("filter", data.get("filter"));
        setSubscriberData(data.get("topic").toString(), data.get("OfbizEntityName").toString(), data.get("filter"));
        OfbizSubscriberServices ofbizSubscriberServices = new OfbizSubscriberServices();
        ofbizSubscriberServices.createOfbizSubscriber(dispatchContext, subscriberContext);
    }

    private void setSubscriberData(String topic, String entityName, Object filter) throws MqttException, InterruptedException {
        String receiverID = UUID.randomUUID().toString();
        IMqttClient receiver = new MqttClient("tcp://mqtt.eclipse.org:1883", receiverID);
        ConnectionBinding mqttClientService2 = new ConnectionBinding(receiver);
        mqttClientService2.makeConnection();
        Subscriber subscriber = new Subscriber(receiver, topic);
        subscriber.receiveMessage(delegator, entityName, filter);
    }

    public GenericValue deleteSubscriber(Map<String, Object> data) throws GenericEntityException {
        String ofbizSubscriberId = (String) data.get("OfbizSubscriberId");
        checkSubscriber(ofbizSubscriberId);
        GenericValue genericValue = EntityQuery
                .use(delegator)
                .from("OfbizSubscriber")
                .where("OfbizSubscriberId", ofbizSubscriberId)
                .queryOne();
        genericValue.remove();
        return genericValue;
    }

    private void checkSubscriber(String ofbizSubscriberId) throws GenericEntityException {
        GenericValue ofbizSubscriber = EntityQuery
                .use(delegator)
                .from("OfbizSubscriber")
                .where("OfbizSubscriberId", ofbizSubscriberId)
                .queryOne();
        if (ofbizSubscriber == null) {
            ServiceUtil.returnError("No ofbizSubscriber found!");
        }
    }
}
