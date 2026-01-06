#!/bin/bash

echo "[INFO] Configuring environment variables..."
export $(grep -v '^#' .env | xargs)
export CLAIMS_API="http://localhost:8082"
export SPRING_PROFILES_ACTIVE="dev"

echo "[INFO] Starting port-forward..."
kubectl port-forward amend-api-port-forward-pod 8082:8080 -n laa-amend-a-claim-dev >/dev/null 2>&1 &
PORT_FORWARD_PID=$!
sleep 3

echo "[INFO] Starting application..."
./gradlew bootRun >/dev/null 2>&1 &
BOOTRUN_PID=$!
sleep 10

echo "[INFO] Starting tests..."
(cd src/e2e/ && ./gradlew test)

echo "[INFO] Cleaning up..."
kill "$BOOTRUN_PID" "$PORT_FORWARD_PID" 2>/dev/null || true
