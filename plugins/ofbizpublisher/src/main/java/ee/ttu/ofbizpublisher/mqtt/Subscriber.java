package ee.ttu.ofbizpublisher.mqtt;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Subscriber {

    private final IMqttClient client;
    private final String topic;

    public Subscriber(IMqttClient client, String topic) {
        this.client = client;
        this.topic = topic;
    }

    public void receiveMessage() throws InterruptedException, MqttException {
        CountDownLatch receivedSignal = new CountDownLatch(10);
        System.out.println("RECEIVE MESSAGE");
        client.subscribe(topic, (topic, message) -> {
            byte[] payload = message.getPayload();
            System.out.println(message);
            receivedSignal.countDown();
        });
        receivedSignal.await(1, TimeUnit.MINUTES);
    }
}
