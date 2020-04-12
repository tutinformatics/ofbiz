package ee.ttu.ofbizpublisher.services;

import ee.taltech.accounting.connector.camel.service.InvoiceService;
import ee.ttu.ofbizpublisher.services.mqtt.ConnectionBinding;
import ee.ttu.ofbizpublisher.services.mqtt.Publisher;
import ee.ttu.ofbizpublisher.services.mqtt.Subscriber;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.DelegatorFactory;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceContainer;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;

import java.util.UUID;

public class DataTransformer {

    public enum TransformType {
        SUBSCRIBE, PUBLISH
    }

    public void transform(String topic, TransformType transformType) throws Exception {
        if (transformType == TransformType.PUBLISH) {
            switch (topic) {
                case "ARVED":
                    LocalDispatcher localDispatcher = createDispatcher();
                    InvoiceService invoiceService = new InvoiceService(localDispatcher.getDelegator());
                    String invoices = invoiceService.getInvoices();
                    String publisherId = UUID.randomUUID().toString();
                    IMqttClient publisher = new MqttClient("tcp://mqtt.eclipse.org:1883", publisherId);
                    ConnectionBinding mqttClientService = new ConnectionBinding(publisher);
                    mqttClientService.makeConnection();
                    Publisher mqttService = new Publisher(publisher, "ARVED", invoices);
                    mqttService.call();
            }
        }
        if (transformType == TransformType.PUBLISH) {
            switch (topic) {
                case "ARVED":
                    String receiverID = UUID.randomUUID().toString();
                    IMqttClient receiver = new MqttClient("tcp://mqtt.eclipse.org:1883", receiverID);
                    ConnectionBinding mqttClientService2 = new ConnectionBinding(receiver);
                    mqttClientService2.makeConnection();
                    Subscriber subscriber = new Subscriber(receiver, "ARVED");
                    subscriber.receiveMessage();
            }
        }
    }

    private LocalDispatcher createDispatcher() {
        Delegator delegator = DelegatorFactory.getDelegator("default");
        return ServiceContainer.getLocalDispatcher("camel-dispatcher", delegator);
    }
}
