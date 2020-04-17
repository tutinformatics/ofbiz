package ee.ttu.ofbizpublisher.mqtt;

import org.apache.commons.lang.SerializationUtils;
import org.apache.ofbiz.entity.GenericValue;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.concurrent.Callable;

public class Publisher implements Callable<Void> {

    private final String topic;
    private final List<GenericValue> message;
    private final IMqttClient client;

    public Publisher(IMqttClient client, String topic, List<GenericValue> message) {
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

    private MqttMessage getDataInBytes() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(message);
        byte[] payload = bos.toByteArray();
        return new MqttMessage(payload);
    }
}
