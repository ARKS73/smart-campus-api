/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smart.campus.api.filter;


import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.logging.Logger;

// @Provider tells JAX-RS to automatically register this filter
// Implements both request and response filter in one class
@Provider
public class LoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {

    // java.util.logging.Logger as required by the coursework spec
    private static final Logger LOGGER = Logger.getLogger(LoggingFilter.class.getName());

    // -------------------------------------------------------
    // Runs BEFORE every request reaches the resource method
    // Logs the HTTP method and URI
    // -------------------------------------------------------
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        // Get the HTTP method e.g. GET, POST, DELETE
        String method = requestContext.getMethod();

        // Get the full request URI e.g. /api/v1/rooms
        String uri = requestContext.getUriInfo().getRequestUri().toString();

        // Log the incoming request
        LOGGER.info("INCOMING REQUEST  --> Method: " + method + " | URI: " + uri);
    }

    // -------------------------------------------------------
    // Runs AFTER every response is sent back to the client
    // Logs the HTTP status code
    // -------------------------------------------------------
    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) throws IOException {

        // Get the HTTP status code e.g. 200, 201, 404
        int status = responseContext.getStatus();

        // Get method and URI again for context
        String method = requestContext.getMethod();
        String uri = requestContext.getUriInfo().getRequestUri().toString();

        // Log the outgoing response
        LOGGER.info("OUTGOING RESPONSE --> Method: " + method + " | URI: " + uri + " | Status: " + status);
    }
}
