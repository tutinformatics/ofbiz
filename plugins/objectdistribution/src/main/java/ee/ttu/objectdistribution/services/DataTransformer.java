package ee.ttu.objectdistribution.services;

import com.sun.net.httpserver.HttpServer;
import ee.taltech.accounting.connector.camel.service.InvoiceService;
import ee.ttu.objectdistribution.services.mqtt.ConnectionBinding;
import ee.ttu.objectdistribution.services.mqtt.Publisher;
import ee.ttu.objectdistribution.services.mqtt.Subscriber;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.DelegatorFactory;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceContainer;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
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

    public void getSubscriptionData() throws IOException {
        int serverPort = 8002;
        HttpServer server = HttpServer.create(new InetSocketAddress(serverPort), 0);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Data", "SUB");
        server.createContext("/api/invoices/subscribe", (exchange -> {
            if ("GET".equals(exchange.getRequestMethod())) {
                String respText = jsonObject.toJSONString();
                exchange.sendResponseHeaders(200, respText.getBytes().length);
                OutputStream output = exchange.getResponseBody();
                output.write(respText.getBytes());
                output.flush();
                try {
                    transform("ARVED", TransformType.SUBSCRIBE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                exchange.sendResponseHeaders(405, -1);// 405 Method Not Allowed
            }
            exchange.close();
        }));
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    public void addPublisher() throws Exception {
        int serverPort = 8001;
        HttpServer server = HttpServer.create(new InetSocketAddress(serverPort), 0);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Data", "PUB");
        server.createContext("/api/invoices/publish", (exchange -> {
            if ("GET".equals(exchange.getRequestMethod())) {
                String respText = jsonObject.toJSONString();
                exchange.sendResponseHeaders(200, respText.getBytes().length);
                OutputStream output = exchange.getResponseBody();
                output.write(respText.getBytes());
                output.flush();
                try {
                    transform("ARVED", TransformType.PUBLISH);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                exchange.sendResponseHeaders(405, -1);// 405 Method Not Allowed
            }
            exchange.close();
        }));
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    private LocalDispatcher createDispatcher() {
        Delegator delegator = DelegatorFactory.getDelegator("default");
        return ServiceContainer.getLocalDispatcher("camel-dispatcher", delegator);
    }
}
