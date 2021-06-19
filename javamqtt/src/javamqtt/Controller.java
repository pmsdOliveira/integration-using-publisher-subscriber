/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javamqtt;


import javax.swing.JLabel;
import javax.swing.table.DefaultTableModel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;


public class Controller implements IMqttMessageListener {
    
    private DefaultTableModel table;
    private JSlider slider;
    private ChangeListener changeListener;
    private JLabel label;
    private Object[][] data;
    private int row;
    
    public Controller(DefaultTableModel table, JSlider slider, JLabel label, Client client, String ...topics) {
        this.table = table;
        this.slider = slider;
        this.changeListener = new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                String val = String.valueOf(slider.getValue()) + ".0";
                client.publish("rate", val.getBytes());
            }
        };
        this.label = label;
        this.data = new Object[10][4];
        this.row = 0;
        
        slider.addChangeListener(changeListener);
        
        client.subscribe(this, topics);
        client.publish("rate", "1.0".getBytes());
    }
    
    @Override
    public void messageArrived(String topic, MqttMessage msg) throws Exception {
        int column = -1;
        
        if (topic.equals("topic_x"))
            column = 1;
        else if (topic.equals("topic_y"))
            column = 2;
        else if (topic.equals("topic_z"))
            column = 3;
        
        if (column == -1) {
            label.setText(msg.toString());
            slider.removeChangeListener(changeListener);
            slider.setValue((int) Double.parseDouble(msg.toString()));
            slider.addChangeListener(changeListener);
            return;
        }
        
        if (row < data.length) {
            data[row][0] = java.time.LocalTime.now().toString().substring(0, 8);
            data[row][column] = Double.parseDouble(msg.toString());
        } else {                
            for (int i = 0; i < data.length - 1; i++) {
                if (topic.equals("topic_z"))
                    data[i][0] = data[i + 1][0];
                data[i][column] = data[i + 1][column];
            }
            if (topic.equals("topic_z"))
                data[9][0] = java.time.LocalTime.now().toString().substring(0, 8);
            data[9][column] = Double.parseDouble(msg.toString());
        }
        
        if (topic.equals("topic_z"))
            row++;
        
        table.setDataVector(data,new Object[] {
            "Timestamp", "Acceleration X", "Acceleration Y", "Acceleration Z"
        });
    }
}
