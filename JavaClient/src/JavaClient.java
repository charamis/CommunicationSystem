import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import static java.lang.Integer.parseInt;

public class JavaClient {

    private static JTextField TFbroker, TFport, TFtopic, TFusername, TFcontent;
    private static JPasswordField password_field;
    private static ButtonGroup group;

    public static void main (String[] args) {
        final JFrame f = new JFrame("Publish MQTT");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //java program will end after closing the frame
        f.setSize(350, 400);
        f.setTitle("Publish");

        JLabel lbroker = new JLabel("server");
        lbroker.setText("Server URI:");
        lbroker.setBounds(20, 50, 80, 30);
        TFbroker = new JTextField("localhost");
        TFbroker.setBounds(110, 50, 130, 30);
        JLabel lport = new JLabel("port");
        lport.setText("Port:");
        lport.setBounds(250, 50, 40, 30);
        TFport = new JTextField("1883");
        TFport.setBounds(290, 50, 40, 30);

        JLabel lusername = new JLabel("username");
        lusername.setText("Username:");
        lusername.setBounds(20, 100, 80, 30);
        TFusername = new JTextField();
        TFusername.setBounds(110, 100, 220, 30);

        JLabel lpassword = new JLabel("password");
        lpassword.setText("Password:");
        lpassword.setBounds(20, 150, 80, 30);
        password_field = new JPasswordField();
        password_field.setBounds(110, 150, 220, 30);

        JLabel ltopic = new JLabel("topic");
        ltopic.setText("Topic:");
        ltopic.setBounds(20, 200, 80, 30);
        TFtopic = new JTextField();
        TFtopic.setBounds(110, 200, 220, 30);

        JLabel lcontent = new JLabel("content");
        lcontent.setText("Content:");
        lcontent.setBounds(20, 250, 80, 30);
        TFcontent = new JTextField();
        TFcontent.setBounds(110, 250, 220, 30);

        JLabel lqoc = new JLabel("qos");
        lqoc.setText("QoS:");
        lqoc.setBounds(20, 300, 40, 30);

        //Add radio buttons
        JRadioButton Button0 = new JRadioButton("0");
        Button0.setBounds(60, 300, 40, 30);
        Button0.setSelected(true);
        JRadioButton Button1 = new JRadioButton("1");
        Button1.setBounds(100, 300, 40, 30);
        JRadioButton Button2 = new JRadioButton("2");
        Button2.setBounds(140, 300, 40, 30);
        //Group the radio buttons.
        group = new ButtonGroup();
        group.add(Button0);group.add(Button1);group.add(Button2);

        JButton button_send = new JButton("Send");
        button_send.setBounds(210, 300, 120, 30);

        //add components to frame
        f.add(lbroker);f.add(TFbroker);
        f.add(lport);f.add(TFport);
        f.add(ltopic);f.add(TFtopic);
        f.add(lusername);f.add(TFusername);
        f.add(lpassword);f.add(password_field);
        f.add(lcontent);f.add(TFcontent);
        f.add(lqoc);f.add(Button0);f.add(Button1);f.add(Button2);
        f.add(button_send);

        f.setLayout(null);
        f.setVisible(true);

        button_send.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                MqttPublish mqttPub;

                String port_string = TFport.getText();
                String qos_string = getSelectedButtonText(group);
                String broker = "tcp://" + TFbroker.getText() + ':' + TFport.getText();
                String topic = TFtopic.getText();
                String username = TFusername.getText();
                String content = TFcontent.getText();
                char[] password = password_field.getPassword();
                if (broker.equals("") || topic.equals("") || qos_string.equals("") || port_string.equals("")) {
                    JOptionPane.showMessageDialog(f, "Empty fields!");
                    return;
                }

                int qos = parseInt(qos_string);

                //Call the right constructor
                if ((!username.equals("")) && password.length!=0)
                    mqttPub = new MqttPublish(broker, "JavaClient", topic, content, qos, username, password);
                else
                    mqttPub = new MqttPublish(broker, "JavaClient", topic, content, qos);

                if (mqttPub.PublishMessage())
                    JOptionPane.showMessageDialog(f, "Message: \"" + content + "\" published successfully!");
                else
                    JOptionPane.showMessageDialog(f, "Cannot connect! Check again your connection credentials");
            }
        });
    }

    //We use this function to get the selected number from the radio buttons in the GUI
    public static String getSelectedButtonText(ButtonGroup ButtonGroup) {
        for (Enumeration<AbstractButton> buttons = ButtonGroup.getElements(); buttons.hasMoreElements();) {
            AbstractButton button = buttons.nextElement();
            if (button.isSelected()) {
                return button.getText();
            }
        }
        return null;
    }

}


