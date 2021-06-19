import React, { useState, useEffect } from "react";

import Graph from "./components/Graph";
import Rate from "./components/Rate";

import "./css/App.css";

let mqtt = require("mqtt");
let client = mqtt.connect("mqtt://localhost:8081", {
    clientId: `react_client_${new Date().getSeconds()}`,
});
client.subscribe(["topic_x", "topic_y", "topic_z", "rate"]);

const getCurrentTime = () => {
    let currentTime = new Date();
    let h = currentTime.getHours().toLocaleString("en-US", { minimumIntegerDigits: 2, useGrouping: false });
    let m = currentTime.getMinutes().toLocaleString("en-US", { minimumIntegerDigits: 2, useGrouping: false });
    let s = currentTime.getSeconds().toLocaleString("en-US", { minimumIntegerDigits: 2, useGrouping: false });

    return h + ":" + m + ":" + s;
};

export default function App() {
    const [x, setX] = useState([]);
    const [y, setY] = useState([]);
    const [z, setZ] = useState([]);
    const [rate, setRate] = useState(1.0);

    const handleChange = (topic, message) => {
        topic = topic.toString();
        let dataObject = { timestamp: getCurrentTime(), value: parseFloat(message.toString()) };

        if (topic === "topic_x") {
            if (x.length < 10) setX((state) => [...state, dataObject]);
        } else if (topic === "topic_y") {
            setY((state) => [...state, dataObject]);
        } else if (topic === "topic_z") {
            setZ((state) => [...state, dataObject]);
        } else if (topic === "rate") {
            setRate(dataObject.value);
        }
    };

    useEffect(() => {
        client.on("message", (topic, message) => handleChange(topic, message));
    }, []);

    return (
        <div className="app">
            <div className="modal">
                <div className="graphs">
                    <Graph type="x" data={x} color="#F00" domain={[-11, -8]} />
                    <Graph type="y" data={y} color="#F80" domain={[-2, 2]} />
                    <Graph type="z" data={z} color="#FD0" domain={[-2, 2]} />
                </div>
                <Rate rate={rate} client={client} />
            </div>
        </div>
    );
}
