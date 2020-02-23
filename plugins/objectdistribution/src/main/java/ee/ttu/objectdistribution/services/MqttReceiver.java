package ee.ttu.objectdistribution.services;

import ee.ttu.objectdistribution.services.EngineTemperatureSensor;
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
        client.subscribe(EngineTemperatureSensor.TOPIC, (topic, message) -> {
            System.out.println(message);
            System.out.println(topic);
            byte[] payload = message.getPayload();
            System.out.println(payload);
            receivedSignal.countDown();
        });
        receivedSignal.await(1, TimeUnit.MINUTES);
    }
}
