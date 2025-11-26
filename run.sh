#!/bin/bash

# PROFILE: `local` (default) or `dev`
PROFILE=${1:-local}

docker-compose down -v

export $(grep -v '^#' .env | xargs)

if [ "$PROFILE" = "local" ]; then
  export CLAIMS_API="http://localhost:8081"
else
  export CLAIMS_API="https://main-laa-data-claims-api-uat.cloud-platform.service.justice.gov.uk/"
fi

docker-compose up -d

./gradlew bootRun --args="--spring.profiles.active=${PROFILE}"