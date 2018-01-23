import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

import static java.lang.Integer.parseInt;

public class MqttPublish {
    private String broker;
    private String clientId;
    private String topic;
    private String content;
    private int qos;
    private String username;
    private char[] password;

    //We have 2 constructors, because username and password fields are optional
    public MqttPublish(String b, String cid, String t, String c, int q) {
        broker = b;
        clientId = cid;
        topic = t;
        content = c;
        qos = q;
        username = "";
        password = null;
    }

    public MqttPublish(String b, String cid, String t, String c, int q, String u, char[] p) {
        broker = b;
        clientId = cid;
        topic = t;
        content = c;
        qos = q;
        username = u;
        password = p;
    }

    //publishes the message and returns true on success and false on failure
    public boolean PublishMessage () {
        MqttClient sampleClient;
        MemoryPersistence persistence = new MemoryPersistence();
        try {
            sampleClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            if ((!username.equals("")) && password!=null) {
                connOpts.setUserName(username);
                connOpts.setPassword(password);
            }
            //connecting to broker
            sampleClient.connect(connOpts);

            //Publishing message
            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(qos);
            sampleClient.publish(topic, message);

            return true;
        } catch (MqttException me) {
            System.err.println("reason " + me.getReasonCode());
            System.err.println("msg " + me.getMessage());
            System.err.println("loc " + me.getLocalizedMessage());
            System.err.println("cause " + me.getCause());
            System.err.println("excep " + me);
            //me.printStackTrace();
            return false;
        }
    }
}
