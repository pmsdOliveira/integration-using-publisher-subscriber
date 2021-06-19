import React from "react";
import { ResponsiveContainer, LineChart, Line, CartesianGrid, XAxis, YAxis, Tooltip } from "recharts";

const Graph = (props) => {
    return (
        <div className="graph">
            <span className="graph-title">Acceleration {props.type.toUpperCase()}</span>
            <ResponsiveContainer className="graph-container">
                <LineChart data={props.data} syncId="sync">
                    <Line type="monotone" dataKey="value" stroke={props.color} />
                    <CartesianGrid stroke="#ccc" />
                    <XAxis dataKey="timestamp" />
                    <YAxis domain={props.domain} />
                    <Tooltip />
                </LineChart>
            </ResponsiveContainer>
        </div>
    );
};

export default Graph;
