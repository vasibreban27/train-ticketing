# Train Ticketing API

## Overview

Train Ticketing API is a Java 21 Spring Boot REST application for managing train routes, ticket bookings, and delay notifications.

The application allows customers to search for train routes, create bookings, and receive booking confirmation emails. Administrators can manage trains, routes, bookings, and train delays.

The application exposes a REST API and does not require a graphical user interface. All functionalities can be tested using Postman, cURL, or the included automated integration tests.

---

## Features

### Customer Features

- Search for direct train routes between two stations.
- Search for train routes with one changeover.
- Create ticket bookings.
- Prevent overbooking based on train capacity.
- Receive booking confirmation emails.

### Administrator Features

- Add, update, and delete trains.
- Add, update, and delete routes.
- Define route stops using station name, stop order, arrival time, and departure time.
- View all bookings for a specific train.
- Mark trains as delayed.
- Notify all affected customers by email.

### Technical Features

- REST API built with Spring Boot.
- Layered architecture.
- Spring Data JPA repositories.
- H2 in-memory database for local development and testing.
- DTO-based request and response handling.
- Custom exceptions and centralized error handling.
- Email sending through SMTP.
- Automated integration tests with JUnit and MockMvc.

---

## Technologies Used

- Java 21
- Spring Boot
- Spring Web MVC
- Spring Data JPA
- Hibernate
- H2 Database
- Maven
- Lombok
- Spring Mail
- Jakarta Validation
- JUnit
- MockMvc

---

## Architecture

The project follows a layered architecture:

```text
Controller Layer
    Exposes REST endpoints.

Service Layer
    Contains business logic.

Repository Layer
    Handles database access using Spring Data JPA.

Model Layer
    Contains JPA entities.

DTO Layer
    Defines request and response objects.

Mapper Layer
    Converts entities to response DTOs.

Exception Layer
    Handles custom exceptions and API error responses.
```

---

## Project Structure

```text
src/main/java/com/vasilebreban/trainticketing
├── config
│   └── DataSeeder.java
├── controller
│   ├── AdminController.java
│   ├── BookingController.java
│   ├── RouteController.java
│   └── TrainController.java
├── dto
│   ├── request
│   └── response
├── exception
│   ├── DuplicateResourceException.java
│   ├── EmailSendingException.java
│   ├── GlobalExceptionHandler.java
│   ├── InvalidRouteException.java
│   ├── OverbookingException.java
│   └── ResourceNotFoundException.java
├── mapper
├── model
├── repository
└── service
```

---

## Domain Model

The application uses the following main entities:

```text
Train
Station
Route
RouteStop
Customer
Booking
```

### Entity Relationships

```text
Train 1 ─── 1 Route
Route 1 ─── * RouteStop
Station 1 ─── * RouteStop
Train 1 ─── * Booking
Customer 1 ─── * Booking
```

### Explanation

- `Train` stores train number, capacity, and delay information.
- `Station` stores station names.
- `Route` represents the route assigned to a train.
- `RouteStop` represents a station stop in a route, including order and time information.
- `Customer` stores customer name and email.
- `Booking` stores ticket bookings made by customers for trains.

---

## Setup and Run

### Prerequisites

Make sure the following are installed:

- Java 21
- Maven

### Clone the Repository

```bash
git clone <repository-url>
cd train-ticketing
```

### Run the Application

```bash
mvn spring-boot:run
```

The application runs on:

```text
http://localhost:9095
```

If your project uses a different port, check `src/main/resources/application.properties`.

---

## Application Configuration

Example `application.properties`:

```properties
server.port=9095

spring.datasource.url=jdbc:h2:mem:trainticketing
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

spring.h2.console.enabled=true

spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true

spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${MAIL_USERNAME}
spring.mail.password=${MAIL_PASSWORD}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
```

---

## H2 Database Console

The H2 console can be accessed at:

```text
http://localhost:9095/h2-console
```

Use the following JDBC URL:

```text
jdbc:h2:mem:trainticketing
```

Default username:

```text
sa
```

Default password is empty.

---

## Email Configuration

The application can send real emails using SMTP.

Before running the application, configure these environment variables:

```text
MAIL_USERNAME=your.email@gmail.com
MAIL_PASSWORD=your-google-app-password
```

For Gmail, `MAIL_PASSWORD` must be a Google App Password, not the normal Google account password.

### IntelliJ Configuration

In IntelliJ IDEA:

```text
Run -> Edit Configurations -> Environment variables
```

Add:

```text
MAIL_USERNAME=your.email@gmail.com;MAIL_PASSWORD=your_google_app_password
```

Do not commit real email credentials to GitHub.

### Email Functionality

The application sends emails for:

- booking confirmations;
- train delay notifications.

If valid SMTP credentials are configured, real emails are sent. If SMTP credentials are not configured correctly, email sending may fail depending on the local configuration.

---

## Demo Data

The application loads demo data automatically at startup using `DataSeeder`.

### Available Stations

```text
Cluj-Napoca
Sibiu
Brasov
Bucuresti
Iasi
```

### Available Trains

```text
IR-101
IR-205
```

### Example Routes

```text
IR-101: Cluj-Napoca -> Sibiu -> Brasov
IR-205: Brasov -> Bucuresti -> Iasi
```

This allows testing:

```text
Direct route:
Cluj-Napoca -> Brasov

Route with one change:
Cluj-Napoca -> Iasi
```

---

## API Endpoints

The project does not use the `/api` prefix.

Base URL:

```text
http://localhost:9095
```

---

## Public Train Endpoints

| Method | Endpoint | Description |
|---|---|---|
| GET | `/trains` | Get all trains |
| GET | `/trains/{id}` | Get train by ID |

---

## Public Route Endpoints

| Method | Endpoint | Description |
|---|---|---|
| GET | `/routes/search?from={from}&to={to}` | Search for direct and changeover routes |

---

## Booking Endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | `/bookings` | Create a booking |
| GET | `/bookings/{id}` | Get booking by ID |

---

## Admin Endpoints

### Train Management

| Method | Endpoint | Description |
|---|---|---|
| POST | `/admin/trains` | Create train |
| PUT | `/admin/trains/{id}` | Update train |
| DELETE | `/admin/trains/{id}` | Delete train |

### Route Management

| Method | Endpoint | Description |
|---|---|---|
| GET | `/admin/routes` | Get all routes |
| GET | `/admin/routes/{id}` | Get route by ID |
| POST | `/admin/routes` | Create route |
| PUT | `/admin/routes/{id}` | Update route |
| DELETE | `/admin/routes/{id}` | Delete route |

### Booking and Delay Management

| Method | Endpoint | Description |
|---|---|---|
| GET | `/admin/trains/{trainId}/bookings` | Get bookings for train |
| POST | `/admin/trains/{trainId}/delay` | Mark train as delayed and notify customers |

---

# Example Requests and Responses

## 1. Get All Trains

### Request

```http
GET /trains
```

### Example Response

```json
[
  {
    "id": 1,
    "trainNumber": "IR-101",
    "capacity": 100,
    "delayMinutes": 0
  },
  {
    "id": 2,
    "trainNumber": "IR-205",
    "capacity": 80,
    "delayMinutes": 0
  }
]
```

---

## 2. Get Train by ID

### Request

```http
GET /trains/1
```

### Example Response

```json
{
  "id": 1,
  "trainNumber": "IR-101",
  "capacity": 100,
  "delayMinutes": 0
}
```

---

## 3. Search Direct Route

### Request

```http
GET /routes/search?from=Cluj-Napoca&to=Brasov
```

### Example Response

```json
[
  {
    "type": "DIRECT",
    "segments": [
      {
        "trainId": 1,
        "trainNumber": "IR-101",
        "departureStation": "Cluj-Napoca",
        "arrivalStation": "Brasov",
        "departureTime": "08:30:00",
        "arrivalTime": "14:10:00"
      }
    ]
  }
]
```

---

## 4. Search Route With Changeover

### Request

```http
GET /routes/search?from=Cluj-Napoca&to=Iasi
```

### Example Response

```json
[
  {
    "type": "CHANGEOVER",
    "segments": [
      {
        "trainId": 1,
        "trainNumber": "IR-101",
        "departureStation": "Cluj-Napoca",
        "arrivalStation": "Brasov",
        "departureTime": "08:30:00",
        "arrivalTime": "14:10:00"
      },
      {
        "trainId": 2,
        "trainNumber": "IR-205",
        "departureStation": "Brasov",
        "arrivalStation": "Iasi",
        "departureTime": "15:00:00",
        "arrivalTime": "22:30:00"
      }
    ]
  }
]
```

---

## 5. Search Route With No Connection

### Request

```http
GET /routes/search?from=Iasi&to=Cluj-Napoca
```

### Example Response

```json
{
  "timestamp": "2026-05-09T11:20:00.123",
  "status": 404,
  "error": "Not Found",
  "message": "No route found between Iasi and Cluj-Napoca",
  "path": "/routes/search"
}
```

---

## 6. Create Booking

### Request

```http
POST /bookings
Content-Type: application/json
```

### Request Body

```json
{
  "trainId": 1,
  "customerName": "Vasile Breban",
  "customerEmail": "vasilebreban2017@gmail.com",
  "numberOfTickets": 2
}
```

### Example Response

```json
{
  "id": 1,
  "train": {
    "id": 1,
    "trainNumber": "IR-101",
    "capacity": 100,
    "delayMinutes": 0
  },
  "customer": {
    "id": 1,
    "fullName": "Vasile Breban",
    "email": "vasilebreban2017@gmail.com"
  },
  "numberOfTickets": 2,
  "bookingTime": "2026-05-09T11:20:15.123"
}
```

---

## 7. Overbooking Example

The application prevents bookings that exceed train capacity.

### Request

```http
POST /bookings
Content-Type: application/json
```

### Request Body

```json
{
  "trainId": 1,
  "customerName": "Overbooking Test",
  "customerEmail": "overbooking@example.com",
  "numberOfTickets": 200
}
```

### Example Response

```json
{
  "timestamp": "2026-05-09T11:25:00.123",
  "status": 409,
  "error": "Conflict",
  "message": "Not enough seats available. Available seats: 95",
  "path": "/bookings"
}
```

---

## 8. Create Train

### Request

```http
POST /admin/trains
Content-Type: application/json
```

### Request Body

```json
{
  "trainNumber": "IR-303",
  "capacity": 120
}
```

### Example Response

```json
{
  "id": 3,
  "trainNumber": "IR-303",
  "capacity": 120,
  "delayMinutes": 0
}
```

---

## 9. Update Train

### Request

```http
PUT /admin/trains/3
Content-Type: application/json
```

### Request Body

```json
{
  "trainNumber": "IR-303-UPDATED",
  "capacity": 140
}
```

### Example Response

```json
{
  "id": 3,
  "trainNumber": "IR-303-UPDATED",
  "capacity": 140,
  "delayMinutes": 0
}
```

---

## 10. Delete Train

### Request

```http
DELETE /admin/trains/3
```

### Example Response

```json
{
  "message": "Train deleted successfully."
}
```

---

## 11. Create Route

Before creating a route, create a train first and use the returned train ID.

### Request

```http
POST /admin/routes
Content-Type: application/json
```

### Request Body

```json
{
  "trainId": 3,
  "stops": [
    {
      "stationName": "Cluj-Napoca",
      "stopOrder": 1,
      "arrivalTime": null,
      "departureTime": "06:45"
    },
    {
      "stationName": "Sibiu",
      "stopOrder": 2,
      "arrivalTime": "09:10",
      "departureTime": "09:25"
    },
    {
      "stationName": "Bucuresti",
      "stopOrder": 3,
      "arrivalTime": "14:30",
      "departureTime": null
    }
  ]
}
```

### Example Response

```json
{
  "id": 3,
  "train": {
    "id": 3,
    "trainNumber": "IR-303",
    "capacity": 120,
    "delayMinutes": 0
  },
  "stops": [
    {
      "id": 6,
      "stationName": "Cluj-Napoca",
      "stopOrder": 1,
      "arrivalTime": null,
      "departureTime": "06:45:00"
    },
    {
      "id": 7,
      "stationName": "Sibiu",
      "stopOrder": 2,
      "arrivalTime": "09:10:00",
      "departureTime": "09:25:00"
    },
    {
      "id": 8,
      "stationName": "Bucuresti",
      "stopOrder": 3,
      "arrivalTime": "14:30:00",
      "departureTime": null
    }
  ]
}
```

---

## 12. Invalid Route Example

The first stop must have a departure time.

### Request

```http
POST /admin/routes
Content-Type: application/json
```

### Request Body

```json
{
  "trainId": 3,
  "stops": [
    {
      "stationName": "Cluj-Napoca",
      "stopOrder": 1,
      "arrivalTime": null,
      "departureTime": null
    },
    {
      "stationName": "Brasov",
      "stopOrder": 2,
      "arrivalTime": "12:00",
      "departureTime": null
    }
  ]
}
```

### Example Response

```json
{
  "timestamp": "2026-05-09T11:35:00.123",
  "status": 400,
  "error": "Bad Request",
  "message": "First stop must have a departure time.",
  "path": "/admin/routes"
}
```

---

## 13. Get Bookings for Train

### Request

```http
GET /admin/trains/1/bookings
```

### Example Response

```json
[
  {
    "id": 1,
    "train": {
      "id": 1,
      "trainNumber": "IR-101",
      "capacity": 100,
      "delayMinutes": 0
    },
    "customer": {
      "id": 1,
      "fullName": "Vasile Breban",
      "email": "vasilebreban2017@gmail.com"
    },
    "numberOfTickets": 2,
    "bookingTime": "2026-05-09T11:20:15.123"
  }
]
```

---

## 14. Mark Train as Delayed

### Request

```http
POST /admin/trains/1/delay
Content-Type: application/json
```

### Request Body

```json
{
  "delayMinutes": 35
}
```

### Example Response

```json
{
  "id": 1,
  "trainNumber": "IR-101",
  "capacity": 100,
  "delayMinutes": 35
}
```

When a train is marked as delayed, the application finds all bookings for that train and sends delay notification emails to the affected customers.

---

# Error Handling

The application uses custom exceptions and a global exception handler.

## Error Response Format

```json
{
  "timestamp": "2026-05-09T11:25:00.123",
  "status": 404,
  "error": "Not Found",
  "message": "Train not found with id: 999",
  "path": "/trains/999"
}
```

## Validation Error Response Format

```json
{
  "timestamp": "2026-05-09T11:25:00.123",
  "status": 400,
  "error": "Bad Request",
  "validationErrors": {
    "customerEmail": "must be a well-formed email address",
    "numberOfTickets": "must be greater than or equal to 1"
  },
  "path": "/bookings"
}
```

## Common Status Codes

| Status Code | Meaning | Example |
|---|---|---|
| 200 | OK | Successful GET, PUT, or delay update |
| 201 | Created | Booking, train, or route created |
| 400 | Bad Request | Invalid request body or invalid route structure |
| 404 | Not Found | Train, route, booking, or station not found |
| 409 | Conflict | Overbooking or duplicate resource |
| 502 | Bad Gateway | Email sending failure, if configured to fail the request |

---

# Automated Tests

The project includes integration tests using JUnit and MockMvc.

The tests cover:

- train endpoints;
- route search endpoints;
- booking creation;
- booking validation;
- overbooking prevention;
- admin train management;
- admin route management;
- route validation;
- train delay handling;
- custom error responses.

Test classes are located in:

```text
src/test/java/com/vasilebreban/trainticketing
```

Run all tests with:

```bash
mvn test
```

or:

```bash
mvn clean test
```

Expected result:

```text
BUILD SUCCESS
```

---

# Manual Testing

The API can also be tested manually using Postman.

Recommended base URL:

```text
http://localhost:9095
```

Suggested testing order:

1. `GET /trains`
2. `GET /admin/routes`
3. `GET /routes/search?from=Cluj-Napoca&to=Brasov`
4. `GET /routes/search?from=Cluj-Napoca&to=Iasi`
5. `POST /bookings`
6. `GET /admin/trains/1/bookings`
7. `POST /admin/trains/1/delay`
8. `POST /admin/trains`
9. `POST /admin/routes`

---

# Optional Problem 2

The original assignment includes an optional second problem:

```text
Suggest an interesting solution to a problem you define, and showcase a programmatic implementation for that problem.
```

A possible extension for this application is:

```text
Smart Route Recommendation
```

## Problem Definition

When multiple routes are available between two stations, users may not know which route is the best. A direct route may not always be the fastest, and a route with one change may sometimes be more convenient depending on travel and waiting times.

## Proposed Solution

The application can evaluate all available routes between two stations and recommend the best one based on:

- total travel time;
- number of train changes;
- waiting time between connections.

A possible scoring formula is:

```text
score = totalTravelMinutes + numberOfChangeovers * 30
```

This gives a penalty to routes that require train changes, while still prioritizing shorter travel time.

## Possible Endpoint

```http
GET /routes/recommendation?from=Cluj-Napoca&to=Iasi
```

## Example Response

```json
{
  "recommendationReason": "Best route selected based on shortest travel time and fewer train changes.",
  "score": 590,
  "route": {
    "type": "CHANGEOVER",
    "segments": [
      {
        "trainId": 1,
        "trainNumber": "IR-101",
        "departureStation": "Cluj-Napoca",
        "arrivalStation": "Brasov",
        "departureTime": "08:30:00",
        "arrivalTime": "14:10:00"
      },
      {
        "trainId": 2,
        "trainNumber": "IR-205",
        "departureStation": "Brasov",
        "arrivalStation": "Iasi",
        "departureTime": "15:00:00",
        "arrivalTime": "22:30:00"
      }
    ]
  }
}
```

If implemented, this feature can be added on top of the existing `RouteSearchService`.

---

# Optional Problem 2 - Smart Task Prioritizer

## Requirement

The optional assignment requirement was:

```text
Suggest an interesting solution to a problem you define, and showcase a programmatic implementation for that problem.
```

## Problem Definition

Many students and developers work on multiple tasks at the same time and often need to decide which task should be handled first.

Choosing only by deadline is not always ideal. A task can be important but not urgent, urgent but low-value, or quick to complete. Therefore, a better prioritization approach should consider multiple factors.

## Proposed Solution

The proposed solution is a **Smart Task Prioritizer**, implemented as a small Java program independent from the train ticketing API.

Each task has the following properties:

- `title`
- `urgency`, from 1 to 5
- `importance`, from 1 to 5
- `estimatedHours`
- `deadlineDays`

The program calculates a priority score for each task using this formula:

```text
score = importance * 3 + urgency * 2 + deadlinePressure - estimatedHours
```

The `deadlinePressure` value is calculated based on how close the deadline is:

```text
deadline <= 1 day   -> +10
deadline <= 3 days  -> +6
deadline <= 7 days  -> +3
otherwise           -> +0
```

After calculating the score, the tasks are sorted in descending order, so the highest-priority task appears first.

## Programmatic Implementation

The implementation is located in:

```text
src/main/java/com/vasilebreban/trainticketing/optional
```

Main classes:

```text
Task.java
TaskPriorityCalculator.java
TaskPrioritizerDemo.java
```

## Example Output

```text
Prioritized tasks:
Finish Java trainee assignment | score: 31 | urgency: 5 | importance: 5 | estimatedHours: 4 | deadlineDays: 1
Prepare interview answers | score: 27 | urgency: 4 | importance: 5 | estimatedHours: 2 | deadlineDays: 3
Refactor old project | score: 7 | urgency: 2 | importance: 3 | estimatedHours: 6 | deadlineDays: 10
Clean local files | score: 4 | urgency: 1 | importance: 1 | estimatedHours: 1 | deadlineDays: 30
```

This optional feature demonstrates basic algorithmic thinking, Java object modeling, score calculation, and sorting with collections.

---

# Notes

- The application does not include a frontend.
- All functionality is exposed through REST endpoints.
- Demo data is loaded automatically at startup.
- Real SMTP credentials are not included in the repository.
- The H2 database is in-memory, so data is reset when the application restarts.
- Automated tests can be run without starting the application manually.
