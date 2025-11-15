# Stage 1: Build the application
# Use an official Maven/Java image to build the project
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src /app/src
# Create the final jar file
RUN mvn clean install -DskipTests

# Stage 2: Create the final lean image
# Use a lightweight JRE (Java Runtime Environment) image for the final container
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the built JAR file from the 'build' stage
COPY --from=build /app/target/*.jar app.jar

# Define the port the container will listen on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]