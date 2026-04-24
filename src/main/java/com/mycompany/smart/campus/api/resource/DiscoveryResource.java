/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smart.campus.api.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;
 
// This class handles GET /api/v1
@Path("/")
public class DiscoveryResource {

    // Test endpoint to deliberately trigger a 500 Internal Server Error
    @GET
    @Path("/crash")
    @Produces(MediaType.APPLICATION_JSON)
    public Response triggerCrash() {
        // Intentionally throwing an unchecked exception to test GlobalExceptionMapper
        throw new RuntimeException("Simulated unexpected server error for testing!");
    }
 
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response discover() {
 
        // Version info
        Map<String, Object> response = new HashMap<>();
        response.put("api", "Smart Campus API");
        response.put("version", "v1");
        response.put("description", "REST API for managing campus rooms and sensors");
        response.put("contact", "admin@smartcampus.ac.uk");
 
        // Links to primary resource collections (HATEOAS)
        Map<String, String> links = new HashMap<>();
        links.put("rooms",   "/api/v1/rooms");
        links.put("sensors", "/api/v1/sensors");
 
        response.put("resources", links);
 
        return Response.ok(response).build();
    }
}