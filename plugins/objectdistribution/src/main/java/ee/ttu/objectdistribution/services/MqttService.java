package ee.ttu.objectdistribution.services;

import ee.taltech.accounting.connector.camel.service.InvoiceService;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.DelegatorFactory;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceContainer;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.concurrent.Callable;

public class MqttService implements Callable<Void> {

    private InvoiceService invoiceService;

    public static final String TOPIC = "TOPIC";
    IMqttClient client;

    public MqttService(IMqttClient client) {
        this.client = client;
        LocalDispatcher localDispatcher = createDispatcher();
        invoiceService = new InvoiceService(localDispatcher.getDelegator());
    }

    @Override
    public Void call() throws Exception {
        if (!client.isConnected()) {
            return null;
        }
        MqttMessage msg = getDataInBytes();
        msg.setQos(0);
        msg.setRetained(true);
        client.publish(TOPIC, msg);
        return null;
    }

    private MqttMessage getDataInBytes() {
        String temp = invoiceService.getInvoices();
        byte[] payload = temp.getBytes();
        return new MqttMessage(payload);
    }

    private LocalDispatcher createDispatcher() {
        Delegator delegator = DelegatorFactory.getDelegator("default");
        return ServiceContainer.getLocalDispatcher("camel-dispatcher", delegator);
    }
}
