docker build \
  --build-arg DATABASE_NAME=$INFLUX_DATABASE \
  --build-arg HOST=$INFLUX_HOST \
  --build-arg TOKEN=$INFLUX_TOKEN \
  -t goflight .
