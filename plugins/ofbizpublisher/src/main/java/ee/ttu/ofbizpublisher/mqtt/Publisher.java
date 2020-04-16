package ee.ttu.ofbizpublisher.mqtt;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.concurrent.Callable;

public class Publisher implements Callable<Void> {

    private final String topic;
    private final String message;
    private final IMqttClient client;

    public Publisher(IMqttClient client, String topic, String message) {
        this.client = client;
        this.topic = topic;
        this.message = message;
    }

    @Override
    public Void call() throws Exception {
        if (!client.isConnected()) {
            return null;
        }
        MqttMessage msg = getDataInBytes();
        msg.setQos(0);
        msg.setRetained(true);
        client.publish(this.topic, msg);
        return null;
    }

    private MqttMessage getDataInBytes() {
        byte[] payload = message.getBytes();
        return new MqttMessage(payload);
    }
}
