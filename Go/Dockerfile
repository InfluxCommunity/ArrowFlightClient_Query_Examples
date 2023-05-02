# Start from the official Golang base image
FROM golang:1.17-alpine

# Set the working directory inside the container
WORKDIR /app

# Enable Go modules
ENV GO111MODULE=on

# Initialize Go modules and fetch dependencies
RUN go mod init my-module
RUN go get github.com/influxdata/influxdb-client-go/v2
RUN go get github.com/apache/arrow/go/v12/arrow/flight/flightsql

# Copy the Go program file into the container
COPY main.go .

# Set ARGs for --build-arg options passed in the build command
ARG DATABASE_NAME
ARG HOST
ARG TOKEN

# Set run-time ENVs from ARGs
ENV DATABASE_NAME=${DATABASE_NAME}
ENV HOST=${HOST}
ENV TOKEN=${TOKEN}

# Compile the Go program
RUN go build -o go-example main.go

# Set the entrypoint to run the compiled program
ENTRYPOINT ["/app/go-example"]