package ee.ttu.ofbizpublisher.mqtt;

import org.apache.ofbiz.entity.GenericValue;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

public class Publisher {

    private String topic;
    private IMqttClient client;

    public Publisher(IMqttClient client, String topic) {
        this.client = client;
        this.topic = topic;
    }

    public Publisher() {
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setClient(IMqttClient client) {
        this.client = client;
    }

    public Void call(List<GenericValue> message) throws Exception {
        if (!client.isConnected()) {
            return null;
        }
        MqttMessage msg = getDataInBytes(message);
        msg.setQos(0);
        msg.setRetained(true);
        client.publish(this.topic, msg);
        return null;
    }

    private MqttMessage getDataInBytes(List<GenericValue> message) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(message);
        byte[] payload = bos.toByteArray();
        return new MqttMessage(payload);
    }
}
