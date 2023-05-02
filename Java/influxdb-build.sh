# Cloud Serverless build
function serverless() {
  _build bucket-name
}

# Cloud Dedicated build
function dedicated() {
  _build iox-namespace-name
}

function _build() {
  docker build \
    --build-arg DATABASE_FIELD=$1 \
    --build-arg DATABASE_NAME=$INFLUX_DATABASE \
    --build-arg HOST=$INFLUX_HOST \
    --build-arg TOKEN=$INFLUX_TOKEN \
    -t javaflight .
}

if [ -z $1 ]; then
    serverless
else
    $1
fi
