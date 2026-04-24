/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smart.campus.api.resource;

import com.mycompany.smart.campus.api.exception.SensorUnavailableException;
import com.mycompany.smart.campus.api.model.Sensor;
import com.mycompany.smart.campus.api.model.SensorReading;
import com.mycompany.smart.campus.api.store.DataStore;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

// This is a Sub-Resource class - it has no @Path at class level
// It is reached via SensorResource's sub-resource locator method
// URL: /api/v1/sensors/{sensorId}/readings
public class SensorReadingResource {

    // The sensorId passed from SensorResource
    private String sensorId;

    // Get the single shared DataStore instance
    private DataStore dataStore = DataStore.getInstance();

    // Constructor receives the sensorId from the parent resource
    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    // -------------------------------------------------------
    // GET /api/v1/sensors/{sensorId}/readings
    // Returns all historical readings for a specific sensor
    // -------------------------------------------------------
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllReadings() {

        // First check if the sensor exists
        Sensor sensor = dataStore.getSensor(sensorId);

        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Sensor with ID " + sensorId + " not found.\"}")
                    .build();
        }

        // Get all readings for this sensor
        List<SensorReading> readingList = dataStore.getReadings(sensorId);

        return Response.ok(readingList).build();
    }

    // -------------------------------------------------------
    // POST /api/v1/sensors/{sensorId}/readings
    // Adds a new reading for a specific sensor
    // Also updates the sensor's currentValue (side effect)
    // -------------------------------------------------------
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addReading(SensorReading reading) {

        // First check if the sensor exists
        Sensor sensor = dataStore.getSensor(sensorId);

        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Sensor with ID " + sensorId + " not found.\"}")
                    .build();
        }

        // Business Logic: sensor in MAINTENANCE cannot accept new readings
        // This throws SensorUnavailableException -> 403 Forbidden
        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException(
                    "Sensor " + sensorId + " is currently under MAINTENANCE " +
                            "and cannot accept new readings.");
        }

        // Also block OFFLINE sensors
        if ("OFFLINE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException(
                    "Sensor " + sensorId + " is OFFLINE " +
                            "and cannot accept new readings.");
        }

        // Auto generate ID and timestamp if not provided by client
        if (reading.getId() == null || reading.getId().isEmpty()) {
            reading.setId(java.util.UUID.randomUUID().toString());
        }
        if (reading.getTimestamp() == 0) {
            reading.setTimestamp(System.currentTimeMillis());
        }

        // Save the new reading
        dataStore.addReading(sensorId, reading);

        // --- SIDE EFFECT (required by spec Part 4 Task 2) ---
        // Update the parent sensor's currentValue with this new reading's value
        sensor.setCurrentValue(reading.getValue());

        // Return 201 Created with the new reading
        return Response.status(Response.Status.CREATED)
                .entity(reading)
                .build();
    }
}