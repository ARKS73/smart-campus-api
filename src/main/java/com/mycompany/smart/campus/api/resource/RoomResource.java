/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smart.campus.api.resource;

import com.mycompany.smart.campus.api.exception.RoomNotEmptyException;
import com.mycompany.smart.campus.api.model.Room;
import com.mycompany.smart.campus.api.store.DataStore;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

// This class handles all /api/v1/rooms endpoints
@Path("/rooms")
public class RoomResource {

    // Get the single shared DataStore instance
    private DataStore dataStore = DataStore.getInstance();

    // -------------------------------------------------------
    // GET /api/v1/rooms
    // Returns a list of all rooms
    // -------------------------------------------------------
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllRooms() {

        // Convert HashMap values to a List
        List<Room> roomList = new ArrayList<>(dataStore.getRooms().values());

        return Response.ok(roomList).build();
    }

    // -------------------------------------------------------
    // POST /api/v1/rooms
    // Creates a new room
    // -------------------------------------------------------
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createRoom(Room room) {

        // Check if room with same ID already exists
        if (dataStore.roomExists(room.getId())) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"error\": \"Room with ID " + room.getId() + " already exists.\"}")
                    .build();
        }

        // Save the new room
        dataStore.addRoom(room);

        // Return 201 Created with the new room object
        return Response.status(Response.Status.CREATED)
                .entity(room)
                .build();
    }

    // -------------------------------------------------------
    // GET /api/v1/rooms/{roomId}
    // Returns a single room by ID
    // -------------------------------------------------------
    @GET
    @Path("/{roomId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRoomById(@PathParam("roomId") String roomId) {

        Room room = dataStore.getRoom(roomId);

        // If room not found return 404
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Room with ID " + roomId + " not found.\"}")
                    .build();
        }

        return Response.ok(room).build();
    }

    // -------------------------------------------------------
    // DELETE /api/v1/rooms/{roomId}
    // Deletes a room - but only if it has no sensors assigned
    // -------------------------------------------------------
    @DELETE
    @Path("/{roomId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteRoom(@PathParam("roomId") String roomId) {

        Room room = dataStore.getRoom(roomId);

        // If room not found return 404
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Room with ID " + roomId + " not found.\"}")
                    .build();
        }

        // Business Logic: cannot delete room if it still has sensors
        // This throws RoomNotEmptyException which is caught by the mapper
        if (!room.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException(
                    "Room " + roomId + " cannot be deleted because it still has " +
                            room.getSensorIds().size() + " sensor(s) assigned to it.");
        }

        // Safe to delete
        dataStore.deleteRoom(roomId);

        return Response.ok()
                .entity("{\"message\": \"Room " + roomId + " deleted successfully.\"}")
                .build();
    }
}