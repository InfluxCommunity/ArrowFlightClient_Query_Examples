#include <cstdlib>
#include <iostream>

#include <arrow/flight/client.h>
#include <arrow/flight/sql/client.h>
#include <arrow/table.h>
#include <gflags/gflags.h>

namespace flight = arrow::flight;
namespace flightsql = arrow::flight::sql;

DEFINE_string(host, "", "The host of the Flight SQL server.");
DEFINE_int32(port, 31337, "The port of the Flight SQL server.");
DEFINE_string(query, "SELECT * FROM intTable WHERE value >= 0", "The query to execute.");

arrow::Status Main() {
  ARROW_ASSIGN_OR_RAISE(auto location,
                        flight::Location::ForGrpcTcp(FLAGS_host, FLAGS_port));
  std::cout << "Connecting to " << location.ToString() << std::endl;

  // Set up the Flight SQL client
  std::unique_ptr<flight::FlightClient> flight_client;
  ARROW_ASSIGN_OR_RAISE(flight_client, flight::FlightClient::Connect(location));
  std::unique_ptr<flightsql::FlightSqlClient> client(
      new flightsql::FlightSqlClient(std::move(flight_client)));

  flight::FlightCallOptions call_options;

  // Execute the query, getting a FlightInfo describing how to fetch the results
  std::cout << "Executing query: '" << FLAGS_query << "'" << std::endl;
  ARROW_ASSIGN_OR_RAISE(std::unique_ptr<flight::FlightInfo> flight_info,
                        client->Execute(call_options, FLAGS_query));

  // Fetch each partition sequentially (though this can be done in parallel)
  for (const flight::FlightEndpoint& endpoint : flight_info->endpoints()) {
    // Here we assume each partition is on the same server we originally queried, but this
    // isn't true in general: the server may split the query results between multiple
    // other servers, which we would have to connect to.

    // The "ticket" in the endpoint is opaque to the client. The server uses it to
    // identify which part of the query results to return.
    ARROW_ASSIGN_OR_RAISE(auto stream, client->DoGet(call_options, endpoint.ticket));
    // Read all results into an Arrow Table, though we can iteratively process record
    // batches as they arrive as well
    ARROW_ASSIGN_OR_RAISE(auto table, stream->ToTable());
    std::cout << "Read one chunk:" << std::endl;
    std::cout << table->ToString() << std::endl;
  }

  return arrow::Status::OK();
}

int main(int argc, char** argv) {
  gflags::ParseCommandLineFlags(&argc, &argv, true);

  if (FLAGS_host.empty()) {
    // For CI
    std::cerr << "Must specify the Flight SQL server host with -host" << std::endl;
    return EXIT_SUCCESS;
  }

  auto status = Main();
  if (!status.ok()) {
    std::cerr << status.ToString() << std::endl;
    return EXIT_FAILURE;
  }
  return EXIT_SUCCESS;
}