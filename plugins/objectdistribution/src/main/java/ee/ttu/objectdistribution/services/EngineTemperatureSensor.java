package ee.ttu.objectdistribution.services;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.concurrent.Callable;

public class  EngineTemperatureSensor implements Callable<Void> {

    public static final String TOPIC = "TOPIC";
    IMqttClient client;

    public EngineTemperatureSensor(IMqttClient client) {
        this.client = client;
    }

    @Override
    public Void call() throws Exception {
        if ( !client.isConnected()) {
            return null;
        }
        MqttMessage msg = readEngineTemp();
        System.out.println("SEND MESSAGE");
        msg.setQos(0);
        msg.setRetained(true);
        client.publish(TOPIC,msg);
        return null;
    }

    private MqttMessage readEngineTemp() {
        String temp =  "Erki Eessaar oleks uhke!";
        byte[] payload = temp.getBytes();
        return new MqttMessage(payload);
    }
}
