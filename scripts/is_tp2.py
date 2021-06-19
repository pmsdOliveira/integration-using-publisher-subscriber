import sim
import time 
import threading
from datetime import datetime
from flask import Flask
from flask_caching import Cache
import paho.mqtt.client as mqtt
import paho.mqtt.publish as publish

app = Flask(__name__)

# Instantiate the cache
cache = Cache()
cache.init_app(app=app, config={"CACHE_TYPE": "filesystem",'CACHE_DIR': './tmp'})

# global configuration variables
clientID=-1


# callback function when a message is received from a subscribed topic
def on_message(client, userdata, message):
    print("Received new rate: %s" % float(str(message.payload.decode("utf-8"))))
    cache.set("current_rate", float(str(message.payload.decode("utf-8"))))


# Helper function provided by the teaching staff
def get_data_from_simulation(id):
    """Connects to the simulation and gets a float signal value

    Parameters
    ----------
    id : str
        The signal id in CoppeliaSim. Possible values are 'accelX', 'accelY' and 'accelZ'.

    Returns
    -------
    data : float
        The float value retrieved from the simulation. None if retrieval fails.
    """
    if clientID!=-1:
        res, data = sim.simxGetFloatSignal(clientID, id, sim.simx_opmode_blocking)
        if res==sim.simx_return_ok:
            return data
    return None


# TODO LAB 1 - Implement the data collection loop in a thread
class DataCollection(threading.Thread):
    def __init__(self):
        threading.Thread.__init__(self)
        # initialize the current_rate value in the cache
        cache.set("current_rate", 1.0)
        publish.single("rate", 1.0)

    def run(self):
        while True:
            x = get_data_from_simulation('accelX')
            y = get_data_from_simulation('accelY')
            z = get_data_from_simulation('accelZ')

            if x == None or y == None or z == None:
                continue

            if cache.get("current_rate") == None:
                cache.set("current_rate", 1.0) 
                publish.single("rate", 1.0)

            msgs = [{"topic": "topic_x", "payload": x},
                    {"topic": "topic_y", "payload": y},
                    {"topic": "topic_z", "payload": z},
                    {"topic": "rate", "payload": cache.get("current_rate")}]
            publish.multiple(msgs)

            print("Rate: %s, X: %s, Y: %s, Z: %s" % (cache.get("current_rate"), x, y, z))
            time.sleep(cache.get("current_rate") if cache.get("current_rate") else 1.0)


if __name__ == '__main__':
    sim.simxFinish(-1) # just in case, close all opened connections
    clientID=sim.simxStart('127.0.0.1',19997,True,True,5000,5) # Connect to CoppeliaSim
    if clientID!=-1:
        mqtt_client = mqtt.Client("python_client_%s" % (datetime.now().timestamp()))
        mqtt_client.connect("127.0.0.1")
        mqtt_client.subscribe("rate")
        mqtt_client.on_message = on_message
        mqtt_client.loop_start()
        dataCollection = DataCollection()
        dataCollection.daemon = True
        dataCollection.start()
        app.run(debug=True, threaded=True)      
    else:
        exit()
