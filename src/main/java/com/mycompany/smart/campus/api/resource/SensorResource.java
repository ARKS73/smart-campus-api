/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smart.campus.api.resource;

import com.mycompany.smart.campus.api.exception.LinkedResourceNotFoundException;
import com.mycompany.smart.campus.api.model.Sensor;
import com.mycompany.smart.campus.api.model.Room;
import com.mycompany.smart.campus.api.store.DataStore;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

// This class handles all /api/v1/sensors endpoints
@Path("/sensors")
public class SensorResource {

    // Get the single shared DataStore instance
    private DataStore dataStore = DataStore.getInstance();

    // -------------------------------------------------------
    // GET /api/v1/sensors
    // GET /api/v1/sensors?type=CO2
    // Returns all sensors, optionally filtered by type
    // -------------------------------------------------------
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllSensors(@QueryParam("type") String type) {

        // Get all sensors as a list
        List<Sensor> sensorList = new ArrayList<>(dataStore.getSensors().values());

        // If type query parameter is provided, filter the list
        if (type != null && !type.isEmpty()) {
            List<Sensor> filtered = new ArrayList<>();
            for (Sensor sensor : sensorList) {
                if (sensor.getType().equalsIgnoreCase(type)) {
                    filtered.add(sensor);
                }
            }
            return Response.ok(filtered).build();
        }

        // No filter - return all sensors
        return Response.ok(sensorList).build();
    }

    // -------------------------------------------------------
    // POST /api/v1/sensors
    // Registers a new sensor
    // Validates that the roomId exists before saving
    // -------------------------------------------------------
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createSensor(Sensor sensor) {

        // Validate that the roomId in the request body actually exists
        // If not, throw LinkedResourceNotFoundException -> 422 Unprocessable Entity
        if (!dataStore.roomExists(sensor.getRoomId())) {
            throw new LinkedResourceNotFoundException(
                "Room with ID '" + sensor.getRoomId() + "' does not exist. " +
                "Please provide a valid roomId."
            );
        }

        // Check if sensor with same ID already exists
        if (dataStore.sensorExists(sensor.getId())) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"error\": \"Sensor with ID " + sensor.getId() + " already exists.\"}")
                    .build();
        }

        // Save the new sensor
        dataStore.addSensor(sensor);

        // Also add this sensor's ID to the room's sensorIds list
        Room room = dataStore.getRoom(sensor.getRoomId());
        room.getSensorIds().add(sensor.getId());

        // Return 201 Created with the new sensor object
        return Response.status(Response.Status.CREATED)
                .entity(sensor)
                .build();
    }

    // -------------------------------------------------------
    // GET /api/v1/sensors/{sensorId}
    // Returns a single sensor by ID
    // -------------------------------------------------------
    @GET
    @Path("/{sensorId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSensorById(@PathParam("sensorId") String sensorId) {

        Sensor sensor = dataStore.getSensor(sensorId);

        // If sensor not found return 404
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Sensor with ID " + sensorId + " not found.\"}")
                    .build();
        }

        return Response.ok(sensor).build();
    }

    // -------------------------------------------------------
    // Sub-Resource Locator for readings
    // Delegates /api/v1/sensors/{sensorId}/readings
    // to SensorReadingResource class
    // -------------------------------------------------------
    @Path("/{sensorId}/readings")
    public SensorReadingResource getReadingResource(@PathParam("sensorId") String sensorId) {

        // Pass the sensorId to the SensorReadingResource
        return new SensorReadingResource(sensorId);
    }
}
