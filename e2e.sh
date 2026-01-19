#!/bin/bash

echo "[INFO] Configuring environment variables..."
export $(grep -v '^#' .env | xargs)
export CLAIMS_API="http://localhost:8082"
export SPRING_PROFILES_ACTIVE="dev"

echo "[INFO] Starting API port-forward..."
kubectl port-forward amend-api-port-forward-pod 8082:8080 -n laa-amend-a-claim-dev >/dev/null 2>&1 &
API_PORT_FORWARD_PID=$!
sleep 3

echo "[INFO] Starting DB port-forward..."
kubectl port-forward amend-db-port-forward-pod 5440:5432 -n laa-amend-a-claim-dev >/dev/null 2>&1 &
DB_PORT_FORWARD_PID=$!
sleep 3

echo "[INFO] Starting application..."
docker-compose up -d
./gradlew bootRun >/dev/null 2>&1 &
BOOTRUN_PID=$!
sleep 10

TEST_CLASS="${1:-}"
if [[ -n "$TEST_CLASS" ]]; then
  TEST_CLASS_PATH="uk.gov.justice.laa.amend.claim.tests.$TEST_CLASS"
  echo "[INFO] Running test: $TEST_CLASS"
  (cd src/e2e/ && ./gradlew test --tests "$TEST_CLASS_PATH")
else
  echo "[INFO] Running tests"
  (cd src/e2e/ && ./gradlew test)
fi

echo "[INFO] Cleaning up..."
kill "$BOOTRUN_PID" "$API_PORT_FORWARD_PID" "$DB_PORT_FORWARD_PID" 2>/dev/null || true
docker-compose down -v
