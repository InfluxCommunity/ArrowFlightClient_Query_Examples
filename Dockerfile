# Use the official Maven image as the base image
FROM maven:3.8.3-openjdk-11 AS build

# Set the working directory
WORKDIR /app

# Copy the pom.xml file into the container
COPY pom.xml .

# Download and cache dependencies
RUN mvn dependency:go-offline

# Copy the rest of the source code into the container
COPY src/ ./src/

# Compile the source code and copy dependencies
RUN mvn compile dependency:copy-dependencies

# Use the official OpenJDK image as the runtime base image
FROM openjdk:11-jre-slim

# Set the working directory
WORKDIR /app

# Copy the compiled classes and dependencies from the build stage
COPY --from=build /app/target/classes ./classes
COPY --from=build /app/target/dependency ./dependency

# Set the entrypoint to run your Java application
ENTRYPOINT ["java", "-cp", "classes:dependency/*", "com.example.helloworld.HelloWorld"]
