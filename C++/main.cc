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

// arrow::Status Main() {
//   ARROW_ASSIGN_OR_RAISE(auto location,
//                         flight::Location::ForGrpcTcp(FLAGS_host, FLAGS_port));
//   std::cout << "Connecting to " << location.ToString() << std::endl;

//   // Set up the Flight SQL client
//   std::unique_ptr<flight::FlightClient> flight_client;
//   ARROW_ASSIGN_OR_RAISE(flight_client, flight::FlightClient::Connect(location));
//   std::unique_ptr<flightsql::FlightSqlClient> client(
//       new flightsql::FlightSqlClient(std::move(flight_client)));

//   flight::FlightCallOptions call_options;

//   // Execute the query, getting a FlightInfo describing how to fetch the results
//   std::cout << "Executing query: '" << FLAGS_query << "'" << std::endl;
//   ARROW_ASSIGN_OR_RAISE(std::unique_ptr<flight::FlightInfo> flight_info,
//                         client->Execute(call_options, FLAGS_query));

//   // Fetch each partition sequentially (though this can be done in parallel)
//   for (const flight::FlightEndpoint& endpoint : flight_info->endpoints()) {
//     // Here we assume each partition is on the same server we originally queried, but this
//     // isn't true in general: the server may split the query results between multiple
//     // other servers, which we would have to connect to.

//     // The "ticket" in the endpoint is opaque to the client. The server uses it to
//     // identify which part of the query results to return.
//     ARROW_ASSIGN_OR_RAISE(auto stream, client->DoGet(call_options, endpoint.ticket));
//     // Read all results into an Arrow Table, though we can iteratively process record
//     // batches as they arrive as well
//     ARROW_ASSIGN_OR_RAISE(auto table, stream->ToTable());
//     std::cout << "Read one chunk:" << std::endl;
//     std::cout << table->ToString() << std::endl;
//   }

//   return arrow::Status::OK();
// }

// int main(int argc, char** argv) {
//   gflags::ParseCommandLineFlags(&argc, &argv, true);

//   if (FLAGS_host.empty()) {
//     // For CI
//     std::cerr << "Must specify the Flight SQL server host with -host" << std::endl;
//     return EXIT_SUCCESS;
//   }

//   auto status = Main();
//   if (!status.ok()) {
//     std::cerr << status.ToString() << std::endl;
//     return EXIT_FAILURE;
//   }
//   return EXIT_SUCCESS;
// }




// class DatabaseHeaderMiddleware : public arrow::flight::ClientMiddleware {
//  public:
//   explicit DatabaseHeaderMiddleware(const std::string& database_name)
//       : database_name_(database_name) {}

//   void SendingHeaders(arrow::flight::AddCallHeaders* outgoing_headers) override {
//     outgoing_headers->AddHeader("database-name", database_name_);
//   }

//   void ReceivedHeaders(const arrow::flight::CallHeaders& incoming_headers) override {}

//   void CallCompleted(const arrow::Status& status) override {}

//  private:
//   std::string database_name_;
// };

// class DatabaseHeaderMiddlewareFactory : public arrow::flight::ClientMiddlewareFactory {
//  public:
//   explicit DatabaseHeaderMiddlewareFactory(const std::string& database_name)
//       : database_name_(database_name) {}

//   void StartCall(const arrow::flight::CallInfo& info,
//                  std::unique_ptr<arrow::flight::ClientMiddleware>* middleware) override {
//     *middleware = std::unique_ptr<arrow::flight::ClientMiddleware>(
//         new DatabaseHeaderMiddleware(database_name_));
//   }

//  private:
//   std::string database_name_;
// };

// // int main(int argc, char** argv) {
// //   gflags::ParseCommandLineFlags(&argc, &argv, true);

// //   arrow::Result<arrow::flight::Location> location_result =
// //       arrow::flight::Location::ForGrpcTls(FLAGS_host, 443);
// //   if (!location_result.ok()) {
// //     std::cerr << "Failed to create location: " << location_result.status().ToString() << std::endl;
// //     return -1;
// //   }
// //   arrow::flight::Location location = *location_result;

// //   std::shared_ptr<arrow::flight::ClientMiddlewareFactory> factory =
// //       std::make_shared<DatabaseHeaderMiddlewareFactory>(FLAGS_database_name);
// //   arrow::flight::FlightClientOptions client_options;
// //   client_options.middleware.push_back(factory);

// //   arrow::Result<std::unique_ptr<arrow::flight::FlightClient>> client_result =
// //       arrow::flight::FlightClient::Connect(location, client_options);
// //   if (!client_result.ok()) {
// //     std::cerr << "Failed to connect to server: " << client_result.status().ToString() << std::endl;
// //     return -1;
// //   }
// //   std::unique_ptr<arrow::flight::FlightClient> client = std::move(*client_result);

// //   arrow::Result<std::unique_ptr<arrow::flight::FlightStreamReader>> stream_result =
// //       client->DoGet(arrow::flight::Ticket{});
// //   if (!stream_result.ok()) {
// //     std::cerr << "Failed to read data from server: " << stream_result.status().ToString() << std::endl;
// //     return -1;
// //   }
// //   std::unique_ptr<arrow::flight::FlightStreamReader> stream = std::move(*stream_result);

// //   arrow::flight::FlightStreamChunk chunk;
// //   while (true) {
// //     arrow::Status status = stream->Next(&chunk);
// //     if (!status.ok()) {
// //       std::cerr << "Failed to read batch from server: "
