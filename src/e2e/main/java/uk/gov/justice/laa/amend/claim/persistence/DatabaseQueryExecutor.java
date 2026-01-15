package uk.gov.justice.laa.amend.claim.persistence;

import uk.gov.justice.laa.amend.claim.config.EnvConfig;
import uk.gov.justice.laa.amend.claim.models.InsertStatement;
import uk.gov.justice.laa.amend.claim.models.Scope;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static uk.gov.justice.laa.amend.claim.utils.TestDataUtils.generateUfn;
import static uk.gov.justice.laa.amend.claim.utils.TestDataUtils.normalizeColumnName;
import static uk.gov.justice.laa.amend.claim.utils.TestDataUtils.parseAllInserts;
import static uk.gov.justice.laa.amend.claim.utils.TestDataUtils.readClasspathResource;
import static uk.gov.justice.laa.amend.claim.utils.TestDataUtils.replaceTokens;

public class DatabaseQueryExecutor implements AutoCloseable {

    private final Connection connection;

    public DatabaseQueryExecutor() throws SQLException {
        String url = String.format("jdbc:postgresql://%s:%s/%s", EnvConfig.dbHost(), EnvConfig.dbPort(), EnvConfig.dbName());
        this.connection = DriverManager.getConnection(url, EnvConfig.dbUser(), EnvConfig.dbPassword());
    }

    public ResultSet executeQuery(String sql, Map<Integer, Object> parameters) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            for (Map.Entry<Integer, Object> entry : parameters.entrySet()) {
                preparedStatement.setObject(entry.getKey(), entry.getValue());
            }

            return preparedStatement.executeQuery();
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute SQL fixture against DB", e);
        }
    }

    public void executeQuery(String sql) {
        try {
            Statement stmt = connection.createStatement();
            connection.setAutoCommit(false);
            stmt.execute(sql);
            connection.commit();
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute SQL fixture against DB", e);
        }
    }

    public void seed(Scope scope) {
        String generatedUfn = generateUfn();
        String generatedCalculatedFeeDetailId = UUID.randomUUID().toString();
        String generatedClaimSummaryFeeId = UUID.randomUUID().toString();
        String generatedClaimId = UUID.randomUUID().toString();
        String generatedSubmissionId = UUID.randomUUID().toString();
        String generatedBulkSubmissionId = UUID.randomUUID().toString();
        String createdBy = EnvConfig.getOrDefault("DB_CREATED_BY_USER_ID", "Data-Claims-Event-Service");

        Map<String, String> tokens = new LinkedHashMap<>();
        tokens.put("UFN", generatedUfn);
        tokens.put("CALCULATED_FEE_DETAIL_ID", generatedCalculatedFeeDetailId);
        tokens.put("CLAIM_SUMMARY_FEE_ID", generatedClaimSummaryFeeId);
        tokens.put("CLAIM_ID", generatedClaimId);
        tokens.put("SUBMISSION_ID", generatedSubmissionId);
        tokens.put("BULK_SUBMISSION_ID", generatedBulkSubmissionId);
        tokens.put("CREATED_BY_USER_ID", createdBy);

        List<String> resources = List.of(
            "insert-bulk-submission",
            "insert-submission",
            "insert-claim",
            "insert-claim-summary-fee",
            "insert-calculated-fee-detail"
        );

        for (String resource : resources) {
            String resourcePath = String.format("fixtures/db/claims/%s.sql", resource);
            String sql = replaceTokens(readClasspathResource(resourcePath), tokens);
            executeQuery(sql);
            List<InsertStatement> inserts = parseAllInserts(sql);

            scope.put("meta.fixture", resourcePath);

            for (InsertStatement insert : inserts) {
                // store each inserted column under: <scope>.db.<table>.<column>
                List<String> columns = insert.columns();
                for (int i = 0; i < columns.size(); i++) {
                    String col = normalizeColumnName(columns.get(i));
                    String val = i < insert.values().size() ? insert.values().get(i) : "";
                    String key = String.format("db.%s.%s", insert.table(), col);
                    scope.put(key, val);
                }
            }
        }

        scope.put("claim.ufn", generatedUfn);
        scope.put("calculated_fee_detail.id", generatedCalculatedFeeDetailId);
        scope.put("claim_summary_fee.id", generatedClaimSummaryFeeId);
        scope.put("claim.id", generatedClaimId);
        scope.put("submission.id", generatedSubmissionId);
        scope.put("bulk_submission.id", generatedBulkSubmissionId);
    }

    @Override
    public void close() throws Exception {
        connection.close();
    }
}
