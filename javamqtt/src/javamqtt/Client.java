package javamqtt;


import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;


public class Client implements MqttCallbackExtended {
    
    private final String serverURI;
    private final MqttConnectOptions options;
    private MqttClient client;
    
    public Client(String serverURI) {
        this.serverURI = serverURI;
        
        options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(false);
        options.setConnectionTimeout(3);
        options.setKeepAliveInterval(10);
        options.setMaxInflight(200);
    }
    
    public void connect() {
        try {
            client = new MqttClient(this.serverURI,
                    String.format("java_client_%d", System.currentTimeMillis()));
            
            client.setCallback(this);
            client.connect(this.options);
        } catch(MqttException e) {
            System.err.println("Error connecting to MQTT broker: " + e);
        }
    }
    
    public void disconnect() {
        if (this.client == null || !this.client.isConnected())
            return;
        
        try {
            client.disconnect();
            client.close();
        } catch(MqttException e) {
            System.err.println("Error disconnecting from MQTT broker: " + e);
        }
    }
    
    public IMqttToken subscribe(IMqttMessageListener listener, String ...topics) {
        if (this.client == null || topics.length == 0)
            return null;
        
        int[] qos = new int[topics.length];       
        IMqttMessageListener[] listeners = new IMqttMessageListener[topics.length];
        
        for (int i = 0; i < topics.length; i++) {
            qos[i] = 2;
            listeners[i] = listener;
        }
        
        try {
            return this.client.subscribeWithResponse(topics, qos, listeners);
        } catch (MqttException e) {
            System.err.println("Error subscribing to topics: " + e);
        }
        
        return null;
    }
    
    public synchronized void publish(String topic, byte[] payload) {
        try {
            if (this.client.isConnected()) {
                client.publish(topic, payload, 2, false);
            }
        } catch (MqttException e) {
            System.err.println("Error publishing to " + topic + ": " + e);
        }
    }
    
    @Override
    public void connectionLost(Throwable t) {
        System.out.println("Lost connection to broker: " + t);
    }
    
    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        System.out.println("Client " + (reconnect ? "reconnected" : "connected") + " to broker " + serverURI);
    }
    
    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {}
    
    @Override
    public void messageArrived(String topic, MqttMessage msg) {}
}
