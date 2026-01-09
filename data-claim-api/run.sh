#!/usr/bin/env bash
set -euo pipefail

DB_HOST="localhost"
DB_PORT="5432"
DB_NAME="laa_data_claims_api_dev"
DB_USER="user"


ls
echo "Importing ${SQL_FILE} into ${DB_NAME}..."

docker exec -i local-postgres psql -U user -d laa_data_claims_api_dev < db/bulk_submission.sql
docker exec -i local-postgres psql -U user -d laa_data_claims_api_dev < db/submission.sql
docker exec -i local-postgres psql -U user -d laa_data_claims_api_dev < db/claim.sql
docker exec -i local-postgres psql -U user -d laa_data_claims_api_dev < db/client.sql
docker exec -i local-postgres psql -U user -d laa_data_claims_api_dev < db/claim_case.sql
docker exec -i local-postgres psql -U user -d laa_data_claims_api_dev < db/claim_summary_fee.sql
docker exec -i local-postgres psql -U user -d laa_data_claims_api_dev < db/calculated_fee_detail.sql
docker exec -i local-postgres psql -U user -d laa_data_claims_api_dev < db/matter_start.sql
docker exec -i local-postgres psql -U user -d laa_data_claims_api_dev < db/replication_summary.sql
docker exec -i local-postgres psql -U user -d laa_data_claims_api_dev < db/native_replication_test.sql
docker exec -i local-postgres psql -U user -d laa_data_claims_api_dev < db/native_replication_test_child.sql
docker exec -i local-postgres psql -U user -d laa_data_claims_api_dev < db/filtered_replication_test.sql
docker exec -i local-postgres psql -U user -d laa_data_claims_api_dev < db/validation_message_log.sql
docker exec -i local-postgres psql -U user -d laa_data_claims_api_dev < db/flyway_schema_history.sql
#psql \
#  -h "localhost" \
#  -p "${DB_PORT}" \
#  -U "${DB_USER}" \
#  -d "${DB_NAME}" \
#  -f "${SQL_FILE}"

echo "Import completed"
