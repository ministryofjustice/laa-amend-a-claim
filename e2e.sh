#!/bin/bash

set -e

API_BOOTRUN_PID=""
BOOTRUN_PID=""

cleanup() {
  echo "[INFO] Cleaning up..."
  kill "$BOOTRUN_PID" "$API_BOOTRUN_PID" 2>/dev/null || true
}

trap cleanup EXIT INT TERM

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
FRONTEND_LOG="$SCRIPT_DIR/e2e.log"
API_LOG="$SCRIPT_DIR/e2e.api.log"

rm -rf "$FRONTEND_LOG"
rm -rf "$API_LOG"

echo "[INFO] Configuring environment variables..."
export $(grep -v '^#' .env | xargs)
export CLAIMS_API="http://localhost:8082"
export CLAIMS_TOKEN="f67f968e-b479-4e61-b66e-f57984931e56"
export SPRING_PROFILES_ACTIVE="e2e"
export SILAS_AUTH_ENABLED=false

echo "[INFO] Starting API application..."
pushd ../laa-data-claims-api >/dev/null
git checkout main
git pull
cd claims-data
docker-compose down -v
docker compose up -d postgres
../gradlew bootRun --args='--server.port=8082' >> "$API_LOG" 2>&1 &
API_BOOTRUN_PID=$!
popd >/dev/null

echo "[INFO] Starting frontend application..."
docker-compose down -v
docker-compose up -d redis
./gradlew bootRun >> "$FRONTEND_LOG" 2>&1 &
BOOTRUN_PID=$!

echo "[INFO] Waiting for application to be ready..."
wait_for() {
  local url=$1
  local word=$2
  local retries=30

  until curl -s "$url" | grep -q "$word"; do
    ((retries--)) || {
      echo "[ERROR] $url never became ready"
      exit 1
    }
    sleep 1
  done
}
wait_for "http://localhost:8182/actuator/health" "UP"
wait_for "http://localhost:8082/v3/api-docs" "openapi"

CMD="./gradlew test"
for arg in "$@"; do
  case $arg in
    --allure-report)
      CMD+=" allureReport"
      ;;
    --allure-serve)
      CMD+=" allureServe"
      ;;
    *)
      CMD+=" --tests uk.gov.justice.laa.amend.claim.tests.$arg"
      ;;
  esac
done
echo "[INFO] Running tests with $CMD"
(cd src/e2e/ && eval $CMD)
