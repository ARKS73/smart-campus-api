/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smart.campus.api.store;



import com.mycompany.smart.campus.api.model.Room;
import com.mycompany.smart.campus.api.model.Sensor;
import com.mycompany.smart.campus.api.model.SensorReading;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataStore {

    // --- Singleton Pattern ---
    // Only ONE instance of DataStore exists for the entire application
    // This is how we share data across all resource classes safely
    private static DataStore instance;

    // Private constructor prevents anyone from doing "new DataStore()"
    private DataStore() {
        // Add some sample data so API is not empty when you demo it
        seedData();
    }

    // Everyone calls DataStore.getInstance() to get the same shared object
    public static DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    // --- In-Memory Data Structures ---

    // Stores all rooms.    Key = roomId,    Value = Room object
    private Map<String, Room> rooms = new HashMap<>();

    // Stores all sensors.  Key = sensorId,  Value = Sensor object
    private Map<String, Sensor> sensors = new HashMap<>();

    // Stores all readings. Key = sensorId,  Value = List of SensorReading objects
    private Map<String, List<SensorReading>> readings = new HashMap<>();

    // --- Room Methods ---

    public Map<String, Room> getRooms() {
        return rooms;
    }

    public Room getRoom(String roomId) {
        return rooms.get(roomId);
    }

    public void addRoom(Room room) {
        rooms.put(room.getId(), room);
    }

    public boolean deleteRoom(String roomId) {
        if (rooms.containsKey(roomId)) {
            rooms.remove(roomId);
            return true;
        }
        return false;
    }

    public boolean roomExists(String roomId) {
        return rooms.containsKey(roomId);
    }

    // --- Sensor Methods ---

    public Map<String, Sensor> getSensors() {
        return sensors;
    }

    public Sensor getSensor(String sensorId) {
        return sensors.get(sensorId);
    }

    public void addSensor(Sensor sensor) {
        sensors.put(sensor.getId(), sensor);
    }

    public boolean sensorExists(String sensorId) {
        return sensors.containsKey(sensorId);
    }

    // --- SensorReading Methods ---

    public List<SensorReading> getReadings(String sensorId) {
        // If no readings exist for this sensor yet, return empty list
        return readings.getOrDefault(sensorId, new ArrayList<>());
    }

    public void addReading(String sensorId, SensorReading reading) {
        // If this sensor has no readings list yet, create one
        if (!readings.containsKey(sensorId)) {
            readings.put(sensorId, new ArrayList<>());
        }
        readings.get(sensorId).add(reading);
    }

    // --- Sample Data (so API is not empty on startup) ---

    private void seedData() {

        // Create two sample rooms
        Room room1 = new Room("LIB-301", "Library Quiet Study", 50);
        Room room2 = new Room("LAB-101", "Computer Science Lab", 30);

        rooms.put(room1.getId(), room1);
        rooms.put(room2.getId(), room2);

        // Create two sample sensors
        Sensor sensor1 = new Sensor("TEMP-001", "Temperature", "ACTIVE", 22.5, "LIB-301");
        Sensor sensor2 = new Sensor("CO2-001", "CO2", "ACTIVE", 400.0, "LAB-101");

        sensors.put(sensor1.getId(), sensor1);
        sensors.put(sensor2.getId(), sensor2);

        // Link sensors to their rooms
        room1.getSensorIds().add(sensor1.getId());
        room2.getSensorIds().add(sensor2.getId());

        // Add a sample reading for each sensor
        SensorReading reading1 = new SensorReading(22.5);
        SensorReading reading2 = new SensorReading(400.0);

        List<SensorReading> list1 = new ArrayList<>();
        list1.add(reading1);
        readings.put(sensor1.getId(), list1);

        List<SensorReading> list2 = new ArrayList<>();
        list2.add(reading2);
        readings.put(sensor2.getId(), list2);
    }
}