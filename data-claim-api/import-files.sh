NAMESPACE=xxxxx
POD_NAME=$(kubectl get pod -n $NAMESPACE -l app=e2e-data-claims-api -o jsonpath="{.items[0].metadata.name}")
# Load files in dependency order
cd db
for file in bulk_submission.sql submission.sql claim.sql client.sql claim.sql claim_case.sql claim_summary_fee.sql calculated_fee_detail.sql matter_start.sql replication_summary.sql native_replication_test.sql native_replication_test_child.sql filtered_replication_test.sql validation_message_log.sql flyway_schema_history.sql; do
  echo "Loading $file..."
  kubectl exec -n laa-amend-a-claim-dev $POD_NAME -c postgres -i -- \
    psql -U user -d laa_data_claims_api_dev -p 5435 < $file
done