#!/bin/bash
set -o pipefail

EXTERNAL_NETWORK=state-less-network
echo "create docker network for ui only see search-service"
docker network create $EXTERNAL_NETWORK || echo "Network $EXTERNAL_NETWORK already exists,"

echo "navigate to deployment folder"
cd ./deployment
docker-compose up

SERVICE_NAME="search-service-container"

while true; do
    if docker ps --filter "name=$SERVICE_NAME" --filter "status=running"  | grep -q $SERVICE_NAME; then
        echo "$SERVICE_NAME is running"
        cd ./../ui-search-app
        docker-compose -f docker-compose.development.yml up
        break
    else
        echo "$SERVICE_NAME is not running. and ui-search-app depend it .we need wait for $SERVICE_NAME to successfully run"
        sleep 5
    fi
done

echo "our services configured successfully!"