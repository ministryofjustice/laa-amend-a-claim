#!/usr/bin/env bash
set -euo pipefail

DB_HOST="localhost"
DB_PORT="5432"
DB_NAME="laa_data_claims_api_dev"
DB_USER="user"


docker exec -i local-postgres psql -U user -d ${DB_NAME} < db/bulk_submission.sql
echo "Importing db/bulk_submission.sql into ${DB_NAME}..."
docker exec -i local-postgres psql -U user -d ${DB_NAME} < db/submission.sql
echo "Importing db/submission.sql into ${DB_NAME}..."
docker exec -i local-postgres psql -U user -d ${DB_NAME} < db/claim.sql
docker exec -i local-postgres psql -U user -d ${DB_NAME} < db/client.sql
docker exec -i local-postgres psql -U user -d ${DB_NAME} < db/claim_case.sql
docker exec -i local-postgres psql -U user -d ${DB_NAME} < db/claim_summary_fee.sql
docker exec -i local-postgres psql -U user -d ${DB_NAME} < db/calculated_fee_detail.sql
docker exec -i local-postgres psql -U user -d ${DB_NAME} < db/matter_start.sql
docker exec -i local-postgres psql -U user -d ${DB_NAME} < db/replication_summary.sql
docker exec -i local-postgres psql -U user -d ${DB_NAME} < db/native_replication_test.sql
docker exec -i local-postgres psql -U user -d ${DB_NAME} < db/native_replication_test_child.sql
docker exec -i local-postgres psql -U user -d ${DB_NAME} < db/filtered_replication_test.sql
docker exec -i local-postgres psql -U user -d ${DB_NAME} < db/validation_message_log.sql
docker exec -i local-postgres psql -U user -d ${DB_NAME} < db/flyway_schema_history.sql


echo "Import completed"
