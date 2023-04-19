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
        String host = "us-east-1-1.aws.cloud2.influxdata.com";
        String query = "SELECT * from \"airSensors\"";
        //String query = "SELECT 1";
        Location location = Location.forGrpcTls(host, 443);

        CallHeaders headers = new FlightCallHeaders();

        headers.insert("bucket-name", "anais-iox");
        // headers.insert("database", "anais-iox");
        HeaderCallOption headerOption = new HeaderCallOption(headers);
        CredentialCallOption auth = new CredentialCallOption(new BearerCredentialWriter(""));
        BufferAllocator allocator = new RootAllocator(Long.MAX_VALUE);
       
        FlightClient client = FlightClient.builder(allocator, location)
                .verifyServer(false)
                .build();
        System.out.println("client" + client);
        FlightSqlClient sqlClient = new FlightSqlClient(client);
        System.out.println( "sqlClient: " + sqlClient);
        FlightInfo flightInfo = sqlClient.execute(query, headerOption, auth);
        FlightStream stream = sqlClient.getStream(flightInfo.getEndpoints().get(0).getTicket(), headerOption, auth);
        System.out.println( "stream: " + stream);
        VectorSchemaRoot root = stream.getRoot();
        System.out.println( "root: " + root);
        System.out.println(root.contentToTSVString());
        // while (stream.next()) {
        //   try { final VectorSchemaRoot root = stream.getRoot(); 
        //     System.out.println(root.contentToTSVString());
        //   }  catch (Exception e) {
        // // handle the exception here, e.g. print error message
        // System.out.println("Error executing FlightSqlClient: " + e.getMessage());
        //   }
        // } 
        // stream.close();
    }
}

// <dependency>
// <groupId>io.grpc</groupId>
// <artifactId>grpc-netty-shaded</artifactId>
// <version>1.41.0</version>
// </dependency>


