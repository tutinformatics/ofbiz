package ee.ttu.ofbizpublisher.mqtt;

import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.model.ModelEntity;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Subscriber {

    private final IMqttClient client;
    private final String topic;

    public Subscriber(IMqttClient client, String topic) {
        this.client = client;
        this.topic = topic;
    }

    public void receiveMessage(Delegator delegator, Object properties, String entityName) throws InterruptedException, MqttException {
        CountDownLatch receivedSignal = new CountDownLatch(10);
        List<String> fieldProperties = (List<String>) properties;
        System.out.println("RECEIVE MESSAGE");
        client.subscribe(topic, (topic, message) -> {
            byte[] payload = message.getPayload();
            deserialize(payload, delegator, fieldProperties, entityName);
            System.out.println(message);
            receivedSignal.countDown();
        });
        receivedSignal.await(1, TimeUnit.MINUTES);
    }

    private void deserialize(byte[] payload, Delegator delegator, List<String> properties, String entityName) throws IOException, ClassNotFoundException {
        ModelEntity entity = delegator.getModelEntity(entityName);
        List<String> fieldNames = entity.getAllFieldNames();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(payload);
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        List<GenericValue> genericValues = (List<GenericValue>) objectInputStream.readObject();
        for (GenericValue genericValue : genericValues) {
            try {
                GenericValue genericValue2 = EntityQuery
                        .use(delegator)
                        .from(entityName)
                        .where(entity.getPkFieldNames().get(0), genericValue.getAllFields().get(entity.getPkFieldNames().get(0)))
                        .queryOne();
                if (!properties.isEmpty() && genericValue2 != null) {
                    for (String field : fieldNames) {
                        if (!properties.contains(field)) {
                            genericValue2.put(field, genericValue.get(field));
                        }
                    }
                    delegator.createOrStore(genericValue2);
                } else {
                    delegator.createOrStore(genericValue);
                }

            } catch (GenericEntityException e) {
                e.printStackTrace();
            }
        }

    }

}
