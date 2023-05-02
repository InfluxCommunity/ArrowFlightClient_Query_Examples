package com.example.javaexample;
// Import Apache Arrow Flight SQL classes
import io.grpc.CallOptions;
import org.apache.arrow.flight.*;
import org.apache.arrow.flight.auth2.BearerCredentialWriter;
import org.apache.arrow.flight.grpc.CredentialCallOption;
import org.apache.arrow.flight.sql.FlightSqlClient;
import org.apache.arrow.memory.BufferAllocator;
import org.apache.arrow.memory.RootAllocator;
import org.apache.arrow.vector.VectorSchemaRoot;
import io.grpc.Metadata;
import java.net.URI;

public class JavaExample {

    /* Get environment variables */
    public static final String DATABASE_FIELD = System.getenv("DATABASE_FIELD");
    public static final String DATABASE_NAME = System.getenv("DATABASE_NAME");
    public static final String HOST = System.getenv("HOST");
    public static final String TOKEN = System.getenv("TOKEN");
            
    public static void main(String[] args) {
        System.out.println("Query InfluxDB with the Java Flight SQL Client");
        
        String query = "SELECT * FROM home";
        Location location = Location.forGrpcTls(HOST, 443);

        CredentialCallOption auth = new CredentialCallOption(new BearerCredentialWriter(TOKEN));
        BufferAllocator allocator = new RootAllocator(Long.MAX_VALUE);

        // We're creating an interceptor here to inject the database header on every
        // request, unconditionally.
        FlightClientMiddleware.Factory f = info -> new FlightClientMiddleware() {
            @Override
            public void onBeforeSendingHeaders(CallHeaders outgoingHeaders) {
                outgoingHeaders.insert(DATABASE_FIELD, DATABASE_NAME);
            }

            @Override
            public void onHeadersReceived(CallHeaders incomingHeaders) {

            }

            @Override
            public void onCallCompleted(CallStatus status) {

            }
        };

        FlightClient client = FlightClient.builder(allocator, location)
                .intercept(f)
                .build();
        System.out.println("client" + client);
        FlightSqlClient sqlClient = new FlightSqlClient(client);
        System.out.println("sqlClient: " + sqlClient);
        FlightInfo flightInfo = sqlClient.execute(query, auth);

        final FlightStream stream = sqlClient.getStream(flightInfo.getEndpoints().get(0).getTicket(), auth);
        while (stream.next()) {
            try {
                final VectorSchemaRoot root = stream.getRoot();
                System.out.println(root.contentToTSVString());
            } catch (Exception e) {
                // handle the exception here, e.g. print error message
                System.out.println("Error executing FlightSqlClient: " + e.getMessage());
            }
        }
        try {
            stream.close();
        } catch (Exception e) {
            // handle the exception here, e.g. print error message
            System.out.println("Error closing stream: " + e.getMessage());
        }
    }
}