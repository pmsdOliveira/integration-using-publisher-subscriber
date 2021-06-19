# Integration using Publisher-Subscriber

## Contains:
CoppeliaSimEdu scene file to simulate a robot performing a generic action, in this case, constantly drawing the letters "IS" on a metal plate. The acceleration value of each axis is sent to a Python script using Lua. Mosquitto is used as an MQTT Broker to maintain the topics on which to publish/subscribe. Python publishes the acceleration on each axis in a different topic, and the current rate for reading values in another. Three different applications were created: the first uses Node-RED to subscribe to create a graphic UI with graphs and gauges that subscribe and display all the topics, and publish a rate according to a slider; the second is a Java Swing aplication that subscribes and displays the last 10 acceleration values for each axis, along with a slider to publish on the rate topic; the last is a React application similar to Node-RED.

## Authors:
This work and was done for the "Systems Integrations" 2020/2021 course at FCT-UNL by Frederico Marques (47359), Guilherme Russo (50760) and Pedro Oliveira (50544) from the Integrated Master's in Computer and Electrical Engineering.
