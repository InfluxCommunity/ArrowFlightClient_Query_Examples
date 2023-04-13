package com.example.helloworld;

// Import Apache Arrow Flight SQL classes
import org.apache.arrow.flight.*;
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
        String query = "SELECT * FROM \"airSensors\"";
        Location location = Location.forGrpcInsecure(host, 443).forGrpcTls(host, 0);

        CallHeaders headers = new FlightCallHeaders();
        headers.insert("token", "pnsXlP9U_q-AQC6MXK-HS6jeatkT5dBpkt8BGjHdNEn-IhEk1Zysaj9h2l3LCsF6QtQZ7qG0rdU9bxK_YiCuzQ==");
        headers.insert("bucket-name", "anais-iox");
        HeaderCallOption headerOption = new HeaderCallOption(headers);
        BufferAllocator allocator = new RootAllocator(Long.MAX_VALUE);
       
        FlightClient client = FlightClient.builder(allocator, location).build(); 
        System.out.println("client" + client);
        FlightSqlClient sqlClient = new FlightSqlClient(client);
        System.out.println( "sqlClient: " + sqlClient);
        FlightInfo flightInfo = sqlClient.execute(query, headerOption);
        FlightEndpoint endpoint = flightInfo.getEndpoints().get(0);
        Ticket ticket = endpoint.getTicket();
        System.out.println("Hello, World!");

        // without bufferAllocator
        // try {
        //     client = FlightClient.builder()
        //     .location(location)
        //     .build();
        //     System.out.println(client);
        //     FlightSqlClient sqlClient = new FlightSqlClient(client);
        //     System.out.println(sqlClient);
        //     FlightInfo flightInfo = sqlClient.execute(query, headerOption);
        //     FlightEndpoint endpoint = flightInfo.getEndpoints().get(0);
        //     Ticket ticket = endpoint.getTicket();
        //     System.out.println("Hello, World!");
        // } catch (Exception e) {
        //     System.out.println("Error creating FlightClient: " + e.getMessage());
        // } 

    }
}
