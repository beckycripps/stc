# Use an official Maven image as a parent image
FROM maven:3.8.4-openjdk-11 AS build

# Set the working directory to /app
WORKDIR /app

# Copy the pom.xml and src files into the container at /app
COPY pom.xml .
COPY src ./src

# Build the application with Maven
RUN mvn clean package

# Use an official OpenJDK runtime as a parent image
FROM openjdk:11-jre-slim

# Set the working directory to /app
WORKDIR /app

# Copy the JAR file from the build stage to the current directory in the container
COPY --from=build /app/target/STC-1.0-SNAPSHOT.jar .

# Command to run the application
CMD ["java", "-jar", "STC-1.0-SNAPSHOT.jar"]
