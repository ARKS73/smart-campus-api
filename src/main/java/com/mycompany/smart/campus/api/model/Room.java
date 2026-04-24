/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smart.campus.api.model;

import java.util.ArrayList;
import java.util.List;
 
public class Room {
 
    private String id;         // Unique identifier e.g. "LIB-301"
    private String name;       // Human-readable name e.g. "Library Quiet Study"
    private int capacity;      // Maximum occupancy
    private List<String> sensorIds = new ArrayList<>(); // IDs of sensors in this room
 
    // --- Constructors ---
 
    public Room() {
    }
 
    public Room(String id, String name, int capacity) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
    }
 
    // --- Getters ---
 
    public String getId() {
        return id;
    }
 
    public String getName() {
        return name;
    }
 
    public int getCapacity() {
        return capacity;
    }
 
    public List<String> getSensorIds() {
        return sensorIds;
    }
 
    // --- Setters ---
 
    public void setId(String id) {
        this.id = id;
    }
 
    public void setName(String name) {
        this.name = name;
    }
 
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
 
    public void setSensorIds(List<String> sensorIds) {
        this.sensorIds = sensorIds;
    }
}
