# ===== Stage 1: Build the application =====
FROM gradle:8.7-jdk21 AS builder
WORKDIR /app

# Copy everything and build the JAR
COPY . .
RUN gradle build -x test

# ===== Stage 2: Create the runtime image =====
FROM eclipse-temurin:21-jdk
WORKDIR /app

# Copy the built JAR from the builder stage
COPY --from=builder /app/build/libs/*.jar /app/todo.jar

# Expose the service port
EXPOSE 8090

# Run the application
ENTRYPOINT ["java", "-jar", "/app/todo.jar"]
