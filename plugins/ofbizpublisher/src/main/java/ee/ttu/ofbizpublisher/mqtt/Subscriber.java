package ee.ttu.ofbizpublisher.mqtt;

import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
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

    public void receiveMessage(Delegator delegator, String entityName, Object filter) throws InterruptedException, MqttException {
        CountDownLatch receivedSignal = new CountDownLatch(10);
        System.out.println("RECEIVE MESSAGE");
        client.subscribe(topic, (topic, message) -> {
            byte[] payload = message.getPayload();
            deserialize(payload, delegator, entityName, filter);
            System.out.println(message);
            receivedSignal.countDown();
        });
        receivedSignal.await(1, TimeUnit.MINUTES);
    }

    private void deserialize(byte[] payload, Delegator delegator, String entityName, Object filter) throws IOException, ClassNotFoundException, GenericEntityException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(payload);
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        List<GenericValue> genericValues = (List<GenericValue>) objectInputStream.readObject();
        for (GenericValue genericValue : genericValues) {
            GenericValue check = delegator.findOne(entityName, genericValue.getPrimaryKey(), false);
            if (check == null) {
                try {
                    delegator.create(genericValue);
                } catch (GenericEntityException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
