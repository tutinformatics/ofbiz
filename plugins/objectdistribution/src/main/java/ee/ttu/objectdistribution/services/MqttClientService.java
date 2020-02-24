package ee.ttu.objectdistribution.services;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

public class MqttClientService {

    IMqttClient publisher;

    public MqttClientService(IMqttClient publisher) {
        this.publisher = publisher;
    }

    public void makeConnection() throws MqttException {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(100);
        publisher.connect(options);
    }
}
