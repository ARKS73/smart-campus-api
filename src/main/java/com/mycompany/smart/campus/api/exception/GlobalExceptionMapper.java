/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smart.campus.api.exception;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

// @Provider tells JAX-RS to automatically register this mapper
// Throwable catches ALL exceptions including NullPointerException etc.
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    // Logger to log the real error on the server side
    private static final Logger LOGGER = Logger.getLogger(GlobalExceptionMapper.class.getName());

    @Override
    public Response toResponse(Throwable exception) {

        // Log the full error details on the SERVER side for debugging
        // This is safe - the stack trace stays on the server, not sent to client
        LOGGER.severe("Unexpected error occurred: " + exception.getMessage());

        // Build a GENERIC JSON error response for the CLIENT
        // Never expose stack trace or internal details to the client
        Map<String, Object> error = new HashMap<>();
        error.put("status", 500);
        error.put("error", "Internal Server Error");
        error.put("message", "An unexpected error occurred. Please contact the administrator.");

        // Return 500 Internal Server Error with generic JSON body
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}