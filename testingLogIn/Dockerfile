# Use OpenJDK 21 image as base
FROM openjdk:22-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the compiled JAR file into the container
COPY target/Al_Furqan_SOFTWARE_ENGINEERING-0.0.1-SNAPSHOT.jar app.jar

# Expose the port
EXPOSE 8081

# Command to run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]