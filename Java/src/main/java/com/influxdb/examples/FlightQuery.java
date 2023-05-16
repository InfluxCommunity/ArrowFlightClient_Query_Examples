package com.influxdb.examples;

import org.apache.arrow.flight.auth2.BearerCredentialWriter;
import org.apache.arrow.flight.CallHeaders;
import org.apache.arrow.flight.CallStatus;
import org.apache.arrow.flight.grpc.CredentialCallOption;
import org.apache.arrow.flight.Location;
import org.apache.arrow.flight.FlightClient;
import org.apache.arrow.flight.FlightClientMiddleware;
import org.apache.arrow.flight.FlightInfo;
import org.apache.arrow.flight.FlightStream;
import org.apache.arrow.flight.sql.FlightSqlClient;
import org.apache.arrow.flight.Ticket;
import org.apache.arrow.memory.BufferAllocator;
import org.apache.arrow.memory.RootAllocator;
import org.apache.arrow.vector.VectorSchemaRoot;

public class FlightQuery {

    /* Get server credentials from environment variables */
    public static final String DATABASE_NAME = System.getenv("DATABASE_NAME");
    public static final String HOST = System.getenv("HOST");
    public static final String TOKEN = System.getenv("TOKEN");

    public static void main() {

        System.out.println("Query InfluxDB with the Java Flight SQL Client");

        // Create an interceptor that injects header metadata (database name) in every request.
        FlightClientMiddleware.Factory f = info -> new FlightClientMiddleware() {
            @Override
            public void onBeforeSendingHeaders(CallHeaders outgoingHeaders) {
                outgoingHeaders.insert("database", DATABASE_NAME);
            }

            @Override
            public void onHeadersReceived(CallHeaders incomingHeaders) {

            }

            @Override
            public void onCallCompleted(CallStatus status) {

            }
        };

        // Create a gRPC+TLS channel URI with HOST and port 443.
        Location location = Location.forGrpcTls(HOST, 443);

        // Set the allowed memory.
        BufferAllocator allocator = new RootAllocator(Long.MAX_VALUE);

        // Create a client with the allocator and gRPC channel.
        FlightClient client = FlightClient.builder(allocator, location)
                .intercept(f)
                .build();
        System.out.println("client" + client);

        FlightSqlClient sqlClient = new FlightSqlClient(client);
        System.out.println("sqlClient: " + sqlClient);

        // Define the SQL query to execute.
        String query = "SELECT * FROM home";
        
        /*  Construct a bearer credential using TOKEN.
            Construct a credentials option using the bearer credential.
        */
        CredentialCallOption auth = new CredentialCallOption(new BearerCredentialWriter(TOKEN));

        /*  Execute the query.
            If successful, execute returns a FlightInfo object that contains metadata
            and an endpoints list.
            Each endpoint contains the following:
                - A list of addresses where you can retrieve the data.
                - A `ticket` value that identifies the data to retrieve.
        */
        FlightInfo flightInfo = sqlClient.execute(query, auth);

        // Extract the Flight ticket from the response.
        Ticket ticket = flightInfo.getEndpoints().get(0).getTicket();
        
        // Pass the ticket to request the Arrow stream data from the endpoint.
        final FlightStream stream = sqlClient.getStream(ticket, auth);

        // Process all the Arrow stream data.
        while (stream.next()) {
            try {
                // Get the current vector data from the stream.
                final VectorSchemaRoot root = stream.getRoot();
                System.out.println(root.contentToTSVString());
            } catch (Exception e) {
                // Handle exceptions.
                System.out.println("Error executing FlightSqlClient: " + e.getMessage());
            }
        }
        try {
            // Close the stream and release resources.
            stream.close();
        } catch (Exception e) {
            // Handle exceptions.
            System.out.println("Error closing stream: " + e.getMessage());
        }

        try {
            // Close the client
            sqlClient.close();
        } catch (Exception e) {
            // Handle exceptions.
            System.out.println("Error closing client: " + e.getMessage());
        }
    }
}