# Step 1: Use Maven image to build the application
FROM maven:3.8.4-openjdk-17-slim AS build

# Set the working directory inside the container
WORKDIR /app

# Copy your pom.xml and src folder into the container
COPY pom.xml .
COPY src ./src

# Run Maven to compile and test the application
RUN mvn clean install -DskipTests

# Step 2: Create a new image to run the Spring Boot app
FROM openjdk:17-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the jar file from the build image
COPY --from=build /app/target/*.jar app.jar

# Expose the application port (default for Spring Boot is 8080)
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
