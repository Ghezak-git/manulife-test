# Use the Maven image to build the application
FROM maven:3.9.3-eclipse-temurin-17 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the pom.xml and the src directory to the working directory
COPY pom.xml .
COPY src ./src

# Build the application using Maven, skipping tests for faster build
RUN mvn clean package -Pproduction -DskipTests

# Stage 2: Run the Spring Boot application
FROM eclipse-temurin:17-jdk-alpine

# Set the working directory for the final container
WORKDIR /app

# Copy the JAR file from the build stage to the runtime stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port the application will run on
EXPOSE 9090

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
