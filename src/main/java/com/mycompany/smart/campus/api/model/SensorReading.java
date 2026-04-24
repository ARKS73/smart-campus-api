/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smart.campus.api.model;

import java.util.UUID;

public class SensorReading {

    private String id;        // Unique reading event ID (UUID)
    private long timestamp;   // Epoch time in milliseconds when reading was captured
    private double value;     // The actual metric value recorded by the sensor

    // --- Constructors ---

    public SensorReading() {
    }

    public SensorReading(double value) {
        this.id = UUID.randomUUID().toString(); // Auto generate unique ID
        this.timestamp = System.currentTimeMillis(); // Auto set current time
        this.value = value;
    }

    // --- Getters ---

    public String getId() {
        return id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public double getValue() {
        return value;
    }

    // --- Setters ---

    public void setId(String id) {
        this.id = id;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setValue(double value) {
        this.value = value;
    }
}