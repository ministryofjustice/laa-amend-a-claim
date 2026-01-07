#!/bin/bash

# PROFILE: `local` (default) or `dev`
PROFILE=${1:-local}

docker-compose down -v

export $(grep -v '^#' .env | xargs)

export CLAIMS_API="https://amend-laa-data-claims-api-uat.cloud-platform.service.justice.gov.uk/"

docker-compose up -d

./gradlew bootRun --args="--spring.profiles.active=${PROFILE}"
