# Use an official lightweight OpenJDK 21 image
FROM openjdk:21-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR into the container
# IMPORTANT: Make sure the JAR name matches the file inside build/libs/
COPY build/libs/ToDo-microservice-0.0.1-SNAPSHOT.jar /app/todo.jar

# Expose the port the microservice runs on
EXPOSE 8090

# Run the application
ENTRYPOINT ["java", "-jar", "/app/todo.jar"]

