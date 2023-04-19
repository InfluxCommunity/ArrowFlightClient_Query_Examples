package com.example.helloworld;
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

public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
        String host = "<host without https:// i.e. us-east-1-1.aws.cloud2.influxdata.com>";
        String query = "SELECT 1";
        Location location = Location.forGrpcTls(host, 443);

        CredentialCallOption auth = new CredentialCallOption(new BearerCredentialWriter("<your token>"));
        BufferAllocator allocator = new RootAllocator(Long.MAX_VALUE);

        // We're creating an interceptor here to inject the database header on every
        // request, unconditionally.
        FlightClientMiddleware.Factory f = info -> new FlightClientMiddleware() {
            @Override
            public void onBeforeSendingHeaders(CallHeaders outgoingHeaders) {
                outgoingHeaders.insert("database", "<your bucket>");
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