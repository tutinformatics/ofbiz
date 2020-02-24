package ee.ttu.objectdistribution.services;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class MqttReceiver {

    IMqttClient client;

    public MqttReceiver(IMqttClient client) {
        this.client = client;
    }

    public void receiveMessage() throws InterruptedException, MqttException {
        CountDownLatch receivedSignal = new CountDownLatch(10);
        System.out.println("RECEIVE MESSAGE");
        client.subscribe(MqttService.TOPIC, (topic, message) -> {
            byte[] payload = message.getPayload();
            System.out.println(message);
            receivedSignal.countDown();
        });
        receivedSignal.await(1, TimeUnit.MINUTES);
    }
}
