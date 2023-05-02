# Java_FlightSqlClient

The Java example is a standalone Java application for querying
Apache Arrow Flight database servers like InfluxDB v3 using RPC and Flight SQL.

## Description

The example shows how to create a Java application that uses
Apache Arrow Flight (`org.apache.arrow.flight`)
and Flight SQL (`org.apache.arrow.flight.sql`) packages to 
interact with a Flight database server.

You can use the example to connect to InfluxDB v3, execute database commands and
SQL queries, and retrieve data.

## Build and run the Java application

1. [Prerequisites](#prerequisites)
2. [Build](#build)
3. [Run](#run)

### Prerequisites

Running the application requires the following:

- **Docker**: The example project uses Docker to ensure a consistent build environment. Follow the instructions to download and install Docker for your system:

    - **macOS**: [Install Docker for macOS](https://docs.docker.com/desktop/install/mac-install/)
    - **Linux**: [Install Docker for Linux](https://docs.docker.com/desktop/install/linux-install/)
              
- **Database**: The name of a Flight database to query--for example, an [InfluxDB Cloud Serverless bucket](https://docs.influxdata.com/influxdb/cloud-serverless/admin/buckets/).
- **Host**: The hostname of the Flight server--for example, your [InfluxDB Cloud Serverless region](https://docs.influxdata.com/influxdb/cloud-serverless/reference/regions/) without the protocol("https://").
- **Token**: An authentication `Bearer` token with _read_ permission to the database--for example, an [InfluxDB Cloud Serverless API token](https://docs.influxdata.com/influxdb/cloud-serverless/get-started/setup/).

### Build

The project contains an `influxdb-build.sh` script that you can use with InfluxDB v3 or as an example to create your build script.

#### Example: Build for InfluxDB Cloud

1. In your terminal, set the following environment variables.

    ```sh
    # Set environment variables

    export INFLUX_DATABASE=my-v3-bucket && \
    export INFLUX_HOST=us-east-1-1.aws.cloud2.influxdata.com && \
    export INFLUX_TOKEN=WIIiwererffkdfoiwe==
    ```

2. Run one of the following:
    - For Cloud Serverless: `sh ./influxdb-build.sh serverless`
    - For Cloud Dedicated: `sh ./influxdb-build.sh dedicated`

The script builds an image with the name `javaflight`.

### Run

To start the application, run `docker run <IMAGE_NAME>` in your terminal.

```sh
docker run javaflight
```
