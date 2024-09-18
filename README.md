
# Ride Sharing Application

The Service is a microservice-based ride-hailing application that allows passengers to request rides from the nearest available drivers using geospatial queries. The system is built using Spring Boot and integrates with MongoDB, RabbitMQ, Redis, and SLF4J for logging. MongoDB's geospatial capabilities are leveraged to find the nearest driver, RabbitMQ handles asynchronous message processing, and Redis is used for caching results to improve performance.

## Technologies Used

+ Spring Boot for the backend logic
+ MongoDB for storing driver and ride request data.
+ RabbitMQ as a message broker for handling ride requests.
+ Redis for caching.
+ Swagger for API documentation.
## Prerequisites

Make sure you have the following tools installed:

+ Java 17+
+ Maven 3.6+
+ Docker (for RabbitMQ)
+ MongoDB
+ RabbitMQ (via Docker)
+ Redis Cache
## Running the application

To clone this repository

```bash
https://github.com/van2jazz/Ecab.git/

cd ecab
```

Build the Application:


```bash
mvn spring-boot:run
```


This will start the Spring Boot application and expose the REST endpoints.
## Accessing MongoDB
MongoDB is used to store data for drivers and ride requests.

+ Make sure your MongoDB instance is running locally or via Docker.

+ For a local MongoDB instance, make sure MongoDB is running on the default port (27017). You can check the driver data using MongoDB Compass.
## Interacting with RabbitMQ
To use RabbitMQ, run the following Docker command to start a RabbitMQ instance in terminal:

```bash
docker run --rm -it -p 15672:15672 -p 5672:5672 rabbitmq:3-management
```

+ The RabbitMQ management interface will be available at `http://localhost:15672`
+ Username: `guest`
+ Password: `guest`
+ The default port 5672 is for messaging.

Sending a Ride Request to RabbitMQ

Once RabbitMQ is running, you can use the 
`/api/rides/request` endpoint to send ride requests, which will be handled by RabbitMQ.


### REST API Usage
The application exposes a few REST API endpoints for submitting ride requests and fetching ride results.

Submit a Ride Request
`POST /api/rides/request`

Example "Request Body" that sends a ride request, which is processed through RabbitMQ

    {
      "passengerId": "passenger123",
      "latitude": 40.730610,
      "longitude": -73.935242
    }


To fetch Ride Result for the passenger with `passenger123` ID, do a GET request to `/api/rides/{passengerId}`

Example:
GET 
`/api/rides/passenger123`




## Swagger API Documentation
Once the application is running, you can access the API documentation using Swagger UI at the following URL:

```bash
http://localhost:8080/swagger-ui.html
