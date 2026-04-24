# Smart Campus API

A RESTful API for managing campus Rooms and Sensors, built using JAX-RS (Jersey) and deployed on Apache Tomcat.

---

## Table of Contents

- [Project Overview](#project-overview)
- [Technology Stack](#technology-stack)
- [Project Structure](#project-structure)
- [API Design Overview](#api-design-overview)
- [How to Build and Run](#how-to-build-and-run)
- [Sample curl Commands](#sample-curl-commands)
- [Report Questions and Answers](#report-questions-and-answers)

---

## Project Overview

This project is the Smart Campus Sensor and Room Management API developed for the 5COSC022W Client-Server Architectures module at the University of Westminster.

The API allows campus facilities managers and automated building systems to:
- Manage Rooms across the campus
- Register and monitor Sensors inside those rooms
- Record and retrieve historical Sensor Readings
- Handle errors gracefully with meaningful HTTP status codes and JSON responses

All data is stored in-memory using Java HashMaps and ArrayLists. No database is used.

---

## Technology Stack

- **Language:** Java 8
- **Framework:** JAX-RS (Jersey 2.32)
- **Server:** Apache Tomcat 9
- **Build Tool:** Maven
- **JSON:** Jackson (via jersey-media-json-jackson 2.32)
- **IDE:** Apache NetBeans

---

## Project Structure

```
smart-campus-api/
├── pom.xml
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── smartcampus/
        │           ├── SmartCampusApplication.java
        │           ├── model/
        │           │   ├── Room.java
        │           │   ├── Sensor.java
        │           │   └── SensorReading.java
        │           ├── store/
        │           │   └── DataStore.java
        │           ├── resource/
        │           │   ├── DiscoveryResource.java
        │           │   ├── RoomResource.java
        │           │   ├── SensorResource.java
        │           │   └── SensorReadingResource.java
        │           ├── exception/
        │           │   ├── RoomNotEmptyException.java
        │           │   ├── LinkedResourceNotFoundException.java
        │           │   ├── SensorUnavailableException.java
        │           │   ├── RoomNotEmptyExceptionMapper.java
        │           │   ├── LinkedResourceNotFoundExceptionMapper.java
        │           │   ├── SensorUnavailableExceptionMapper.java
        │           │   └── GlobalExceptionMapper.java
        │           └── filter/
        │               └── LoggingFilter.java
        └── webapp/
            └── WEB-INF/
                └── web.xml
```

---

## API Design Overview

### Base URL
```
http://localhost:8080/api/v1
```

### Resource Hierarchy
```
/api/v1                              → Discovery endpoint
/api/v1/rooms                        → Room collection
/api/v1/rooms/{roomId}               → Single room
/api/v1/sensors                      → Sensor collection
/api/v1/sensors?type={type}          → Filtered sensors by type
/api/v1/sensors/{sensorId}           → Single sensor
/api/v1/sensors/{sensorId}/readings  → Readings sub-resource
```

### Data Models

**Room**
```json
{
  "id": "LIB-301",
  "name": "Library Quiet Study",
  "capacity": 50,
  "sensorIds": ["TEMP-001"]
}
```

**Sensor**
```json
{
  "id": "TEMP-001",
  "type": "Temperature",
  "status": "ACTIVE",
  "currentValue": 22.5,
  "roomId": "LIB-301"
}
```

**SensorReading**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "timestamp": 1714000000000,
  "value": 22.5
}
```

### HTTP Status Codes Used

| Status Code | Meaning | When Used |
|---|---|---|
| 200 OK | Success | GET and DELETE success |
| 201 Created | Resource created | POST success |
| 403 Forbidden | Not allowed | Reading on MAINTENANCE sensor |
| 404 Not Found | Resource not found | Invalid ID in path |
| 409 Conflict | Conflict | Deleting room with sensors |
| 415 Unsupported Media Type | Wrong content type | Non-JSON request body |
| 422 Unprocessable Entity | Invalid reference | Sensor with invalid roomId |
| 500 Internal Server Error | Unexpected error | Any unhandled exception |

---

## How to Build and Run

### Prerequisites
- Java 8 or higher installed
- Apache Tomcat 9 configured in NetBeans
- Maven installed (comes with NetBeans)
- Apache NetBeans IDE

### Step 1 — Clone the Repository
```bash
git clone https://github.com/yourusername/smart-campus-api.git
```

### Step 2 — Open in NetBeans
- Open NetBeans
- File → Open Project
- Navigate to the cloned folder and open it

### Step 3 — Build the Project
- Right click the project in the Projects panel
- Select Clean and Build
- Maven will download all dependencies automatically

### Step 4 — Run on Tomcat
- Right click the project
- Select Run
- NetBeans will deploy the WAR to Tomcat automatically

### Step 5 — Verify the Server is Running
Open your browser or Postman and visit:
```
http://localhost:8080/api/v1
```
You should see the discovery JSON response.

---

## Sample curl Commands

### 1. Get API Discovery Info
```bash
curl -X GET http://localhost:8080/api/v1
```
**Expected:** 200 OK with API metadata and resource links

---

### 2. Get All Rooms
```bash
curl -X GET http://localhost:8080/api/v1/rooms
```
**Expected:** 200 OK with list of all rooms

---

### 3. Create a New Room
```bash
curl -X POST http://localhost:8080/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d "{\"id\": \"HALL-01\", \"name\": \"Main Hall\", \"capacity\": 200}"
```
**Expected:** 201 Created with the new room object

---

### 4. Get a Single Room by ID
```bash
curl -X GET http://localhost:8080/api/v1/rooms/LIB-301
```
**Expected:** 200 OK with room details

---

### 5. Delete a Room with No Sensors
```bash
curl -X DELETE http://localhost:8080/api/v1/rooms/HALL-01
```
**Expected:** 200 OK with success message

---

### 6. Delete a Room That Has Sensors (409 Conflict)
```bash
curl -X DELETE http://localhost:8080/api/v1/rooms/LIB-301
```
**Expected:** 409 Conflict — room has sensors assigned

---

### 7. Get All Sensors
```bash
curl -X GET http://localhost:8080/api/v1/sensors
```
**Expected:** 200 OK with list of all sensors

---

### 8. Get Sensors Filtered by Type
```bash
curl -X GET "http://localhost:8080/api/v1/sensors?type=CO2"
```
**Expected:** 200 OK with only CO2 sensors

---

### 9. Create a New Sensor with Valid Room ID
```bash
curl -X POST http://localhost:8080/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d "{\"id\": \"OCC-001\", \"type\": \"Occupancy\", \"status\": \"ACTIVE\", \"currentValue\": 0.0, \"roomId\": \"LIB-301\"}"
```
**Expected:** 201 Created with the new sensor object

---

### 10. Create a Sensor with Invalid Room ID (422 Error)
```bash
curl -X POST http://localhost:8080/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d "{\"id\": \"OCC-002\", \"type\": \"Occupancy\", \"status\": \"ACTIVE\", \"currentValue\": 0.0, \"roomId\": \"INVALID-ROOM\"}"
```
**Expected:** 422 Unprocessable Entity — roomId does not exist

---

### 11. Get All Readings for a Sensor
```bash
curl -X GET http://localhost:8080/api/v1/sensors/TEMP-001/readings
```
**Expected:** 200 OK with list of readings

---

### 12. Add a New Reading to a Sensor
```bash
curl -X POST http://localhost:8080/api/v1/sensors/TEMP-001/readings \
  -H "Content-Type: application/json" \
  -d "{\"value\": 25.3}"
```
**Expected:** 201 Created — also updates sensor currentValue to 25.3

---

### 13. Create a Sensor in MAINTENANCE Status
```bash
curl -X POST http://localhost:8080/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d "{\"id\": \"TEMP-002\", \"type\": \"Temperature\", \"status\": \"MAINTENANCE\", \"currentValue\": 0.0, \"roomId\": \"LAB-101\"}"
```
**Expected:** 201 Created

---

### 14. Post Reading to MAINTENANCE Sensor (403 Forbidden)
```bash
curl -X POST http://localhost:8080/api/v1/sensors/TEMP-002/readings \
  -H "Content-Type: application/json" \
  -d "{\"value\": 30.0}"
```
**Expected:** 403 Forbidden — sensor is under maintenance

---

### 15. Get a Room That Does Not Exist (404)
```bash
curl -X GET http://localhost:8080/api/v1/rooms/FAKE-999
```
**Expected:** 404 Not Found

---

### 16. Trigger a 500 Internal Server Error (Global Safety Net)
```bash
curl -X GET http://localhost:8080/api/v1/crash

```
**Expected:** 500 Internal Server Error 

---

## Report Questions and Answers

### Part 1 — Question 1: JAX-RS Resource Class Lifecycle

By default, JAX-RS works on a per-request lifecycle. This means every time a new HTTP request arrives, a brand new instance of the resource class is created to handle it. Once the request is complete and the response is sent, that instance is thrown away. Any data stored as an instance variable inside it is lost permanently.

This is a critical architectural decision because it means shared data cannot be stored inside resource classes. To solve this, I implemented a Singleton DataStore class. All resource classes call DataStore.getInstance() to access the same single shared object. This way the in-memory HashMaps persist for the full lifetime of the server and remain consistent across all incoming requests, preventing any data loss or race conditions.

---

### Part 1 — Question 2: Why HATEOAS is Important

HATEOAS stands for Hypermedia As The Engine Of Application State. It is considered a hallmark of advanced RESTful design because it makes an API self-describing. Instead of forcing client developers to read lengthy external documentation to know all the available endpoints, the API itself provides navigation links directly inside the response body.

In this project, the discovery endpoint (GET /api/v1) acts like a starting point for the client. Instead of knowing every endpoint in advance, the client only needs this one root URL. From there, it can follow the links provided in the response to access /api/v1/rooms and /api/v1/sensors.

This approach makes things much more flexible. If any URLs change on the server side later, the client doesn’t need to be updated—as long as it continues to follow the links it receives. It reduces the dependency between the client and server and makes the API easier and quicker to use.

---

### Part 2 — Question 1: Full Objects vs IDs When Listing Rooms

Returning only room IDs keeps the response size very small but forces the client to make a separate GET request for every single room it wants to display. If there are 100 rooms, that is 100 extra HTTP requests — this is known as the N+1 problem and causes serious performance issues.

Returning full room objects makes the initial response slightly larger but allows the client to display all room information immediately in a single request. For a campus management dashboard that needs room names, capacities, and sensor lists all at once, returning full objects is far more efficient. The small increase in payload size is much less costly than the overhead of hundreds of additional network round trips.

---

### Part 2 — Question 2: Is DELETE Idempotent?

In this implementation, DELETE is not fully idempotent. Idempotency in REST theory means that sending the same request multiple times produces the same result as sending it once. While the server state ends up the same after repeated DELETE requests, the HTTP response codes differ.

The first DELETE request removes the room and returns 200 OK. If the same request is sent again, the room is no longer there, so it returns 404 Not Found.

Even though the server ends up in the same state (the room is deleted), the response is different each time. Because of that, this doesn’t strictly follow idempotency. In a fully idempotent design, the response would be consistent every time—even if the resource was already deleted.

---

### Part 3 — Question 1: Consequences of Wrong Content-Type with @Consumes

The @Consumes(MediaType.APPLICATION_JSON) annotation simply tells JAX-RS that this POST method only accepts JSON data. So when a client sends a request, it must include a Content-Type: application/json header.

If the client sends the data in any other format, like text/plain or application/xml, JAX-RS won’t even pass the request to your method. Instead, it automatically stops it and returns a 415 Unsupported Media Type response. This helps keep your code clean, since you don’t need to manually check the request format inside the method.

No code inside the method executes at all. This is beneficial because it protects the API from malformed or unexpected input automatically without requiring any manual validation logic inside the method. It keeps resource methods clean and focused on business logic only, and gives the client a clear and meaningful error code explaining exactly why the request was rejected.

---

### Part 3 — Question 2: @QueryParam vs Path Parameter for Filtering

Using @QueryParam for filtering with a URL like GET /api/v1/sensors?type=CO2 is superior to embedding the filter in the path like GET /api/v1/sensors/type/CO2 for several reasons.

First, query parameters are optional by nature. The same single endpoint handles both GET /api/v1/sensors (all sensors) and GET /api/v1/sensors?type=CO2 (filtered sensors) without needing two separate methods.

Second, path parameters are mainly used to point to a specific resource using its unique ID, like /sensors/TEMP-001. Using them for filtering doesn’t really match their purpose and can make the API structure confusing and less aligned with RESTful design.

Third, query parameters are much better when you need to apply multiple filters. For example, GET /api/v1/sensors?type=CO2&status=ACTIVE is simple and easy to read. If you tried to do the same thing with path parameters, the URL would become long, nested, and harder to manage.

---

### Part 4 — Question 1: Benefits of Sub-Resource Locator Pattern

The Sub-Resource Locator pattern allows a parent resource class to delegate handling of nested URLs to a dedicated child class. In this project, SensorResource delegates all /readings paths to SensorReadingResource by returning an instance of it from a method that has a @Path annotation but no HTTP verb annotation.

This pattern provides important architectural benefits. First it enforces the Single Responsibility Principle — SensorResource manages sensors and SensorReadingResource manages readings. Neither class becomes too large. Second it improves readability and maintainability because each class is small and focused. Third it allows context to be passed cleanly — the sensorId is injected through the constructor of SensorReadingResource so every method inside already knows which sensor it is working with. Finally it makes testing easier because each class can be tested independently without affecting the other.

---

### Part 5 — Question 1: Why 422 Over 404 for Missing roomId

A 404 Not Found response conventionally means the requested URL endpoint does not exist on the server. In this scenario, the URL /api/v1/sensors is perfectly valid and was found by the server. The JSON payload was also well-formed and the Content-Type was correct.

The problem is that a reference inside the payload — the roomId field — points to a resource that does not exist. HTTP 422 Unprocessable Entity is far more semantically accurate because it signals that the server understood the request and its format, but was unable to process the instructions due to a logical error in the data. This distinction is critical for developers. A 404 tells them to check their URL, while a 422 tells them to check their request body. Using 422 leads to faster debugging and a much clearer API contract.

---

### Part 5 — Question 2: Risks of Exposing Stack Traces

Exposing internal Java stack traces to external API consumers presents serious cybersecurity risks. Stack traces reveal class names, package structures, and file paths of the internal application, allowing attackers to understand the system architecture. They also expose library names and version numbers, which attackers can cross-reference with known CVEs to find exploitable vulnerabilities in those specific versions.

Additionally stack traces can reveal server-side business logic, the sequence of method calls, and sometimes even sensitive data values that were being processed when the error occurred. The GlobalExceptionMapper in this project addresses all of these risks. It catches every unexpected Throwable, logs the full technical details safely on the server side using java.util.logging.Logger, and returns only a generic 500 Internal Server Error message to the client. The client learns something went wrong but gains no exploitable information.

---

### Part 5 — Question 3: Why Use Filters for Logging

Using JAX-RS filters for cross-cutting concerns like logging is far superior to manually inserting Logger.info() statements inside every resource method for several important reasons.

First it follows the DRY principle — Don't Repeat Yourself. The logging code is written once in LoggingFilter.java and automatically applies to every single endpoint in the entire API without any extra effort.

Second it keeps resource methods clean. Resource methods should only contain business logic — validating input, interacting with the data store, and building responses. Mixing logging code into every method makes them harder to read and maintain.

Third filters are consistent and reliable. A developer cannot forget to add logging to a new endpoint because the filter runs automatically for every HTTP request regardless of which endpoint is called. If the logging format ever needs to change in the future, only one class needs to be updated instead of every resource method across the entire project.
