# Java Spring Boot Project

## Description
This is a simple Java Spring Boot project that demonstrates how to create Full Stack Java Spring Boot application with Vaadin. with Restful API. The Project is a simple CRUD application that allows you to create, read, update and delete a user, using Vaadin as the front end and Spring Boot as the backend and Jasper Report for the report generation.

## Tech Stack
- **Java**: Version 17
- **Spring Boot**: Version 3.3.5
- **Maven**: Version 3.9.3
- **Database**: PostgreSQL
- **Frontend**: Vaadin
- **Report**: Jasper Report
- **Dockers**: Yes

## Pre-requisites
- Java 17 installed
- Maven installed
- PostgreSQL installed
- Docker installed

## How to run
- Clone the repository
- Open the project in your favorite IDE
- Rename the file `application.properties.example` to `application.properties`
- Configure the database connection in the `application.properties` file
- Run command `mvn spring-boot:run` to start the application on your local machine
- Open your browser and navigate to (example: `http://localhost:8080`)

## How to build with Docker
- Clone the repository
- Open the project in your favorite IDE
- Rename the file `application.properties.example` to `application.properties`
- Configure the database connection in the `application.properties` file
- Run the command (example`docker build --pull --rm -f "Dockerfile" -t manulife:latest ".")
- Run the command (example`docker run -p 8080:8080/tcp manulife:latest`)
- Open your browser and navigate to (example: `http://localhost:8080`)

## How to use API
- Import the postman collection Manulife.postman_collection.json in your postman
- You can use the API to create, read, update and delete a user
- You can also use the API to generate a report

## How to use Frontend
- Open your browser and navigate to (example: `http://localhost:8080`)
- You can use the frontend to create, read, update and delete a user
- You can also use the frontend to generate a report