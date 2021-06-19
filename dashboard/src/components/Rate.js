import { useState, useEffect } from "react";

import "../css/App.css";

export default function Rate({ rate, client }) {
    const handleChange = (e) => {
        client.publish("rate", e.target.value);
    };

    return (
        <div className="rate">
            <div className="rate-header">
                <span>Current Rate</span>
            </div>
            <div className="rate-value">
                <span>{rate}</span>
            </div>
            <div className="rate-field">
                <div className="rate-value-left">0.5</div>
                <input
                    className="rate-slider"
                    type="range"
                    min="0.5"
                    max="10"
                    step="0.5"
                    value={rate}
                    onChange={(e) => handleChange(e)}
                />
                <div className="rate-value-right">10</div>
            </div>
        </div>
    );
}
