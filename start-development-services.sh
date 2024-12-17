#!/bin/bash
set -o pipefail

# elastic config
ES_USERNAME="elastic"
ES_PASSWORD="123456"
ES_URL="http://localhost:9200"

# kibana config
KIBANA_SYSTEM_PASSWORD=KIBANA_USER@12345678

install_yq(){
  echo "install yq for editing yml file"
  winget install yq
}
move_to_deployment_directory(){
  echo "navigate to deployment folder"
  cd ./deployment
}
create_essential_network(){

  echo "create docker network for ui only see search-service"
  docker network create state-less-network || echo "Network state-less-network already exists,"
}

# Function to check if Elasticsearch is up and healthy

start_elastic_search(){
  echo "start elastic-service"
  docker-compose up -d elastic-service
}
wait_for_elasticsearch() {
  echo "Waiting for Elasticsearch to be up..."


  until curl -s -u "$ES_USERNAME:$ES_PASSWORD" "$ES_URL/_cluster/health" | grep -q '"status":"green"\|"status":"yellow"'; do
    echo "Elasticsearch not ready yet, waiting..."
    sleep 5
  done

  echo "Elasticsearch is up and healthy!"
}

# Function to change the kibana_system user password
change_password() {
  echo "Changing kibana_system password..."

  response=$(curl -X POST "$ES_URL/_security/user/kibana_system/_password" \
    -H 'Content-Type: application/json' \
    -u "$ES_USERNAME:$ES_PASSWORD" \
    -d "{\"password\": \"$KIBANA_SYSTEM_PASSWORD\"}" \
    -s -w "%{http_code}" -o ./tmp/curl_output.txt)

  # Check the HTTP response code
  http_code=$(echo "$response" | tail -n 1)

  if [[ "$http_code" -eq 200 ]]; then
    echo "Password changed successfully for kibana_system user."
    yq e ".elasticsearch.password = \"${KIBANA_SYSTEM_PASSWORD}\"" -i kibana-config.yml
    cat /tmp/curl_output.txt
  else
    echo "Failed to change the password. HTTP code: $http_code"
    cat /tmp/curl_output.txt
    exit 1
  fi
}
start_other_service_in_deployment(){
  echo "start other container in deployment directory"
  docker-compose up -d
}
wait_for_search_service(){

SERVICE_NAME="search-service-container"
  while true; do
      if docker ps --filter "name=$SERVICE_NAME" --filter "status=running"  | grep -q $SERVICE_NAME; then
          echo "$SERVICE_NAME is successfully run it"
          break
      else
          echo "$SERVICE_NAME is not running. and ui-search-app depend it .we need wait for $SERVICE_NAME to successfully run"
          sleep 5
      fi
  done
}
start_ui_search_app(){
   cd ./../ui-search-app
   docker-compose -f docker-compose.development.yml up -d
   echo "start ui-service successfully"
}
print_get_started(){

  echo "everything is ok 😀😀"
  echo "at first you need crawl for specific domain . you can do that at url http://localhost:8080/swagger-ui/index.html"
  echo "takes time to see crawled webpage and inserted to elastic"
  echo "you can go to kibana dashboard by username -> elastic & password -> 123456 . our index name is [webpages]"
  echo "you able to use localhost:3000 to access ui-search-service and use all functionality "
}
install_yq
create_essential_network
move_to_deployment_directory
start_elastic_search
wait_for_elasticsearch
change_password
start_other_service_in_deployment
wait_for_search_service
start_ui_search_app
print_get_started