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

# Set ARGs for --build-arg options passed in the build command
ARG DATABASE_FIELD
ARG DATABASE_NAME
ARG HOST
ARG TOKEN

# Set run-time ENVs from ARGs
ENV DATABASE_FIELD=${DATABASE_FIELD}
ENV DATABASE_NAME=${DATABASE_NAME}
ENV HOST=${HOST}
ENV TOKEN=${TOKEN}

# Set the entrypoint to run your Java application
ENTRYPOINT ["java", "-cp", "classes:dependency/*", "com.example.javaexample.JavaExample"]
