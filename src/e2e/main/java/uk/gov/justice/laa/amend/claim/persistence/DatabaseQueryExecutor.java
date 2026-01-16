package uk.gov.justice.laa.amend.claim.persistence;

import uk.gov.justice.laa.amend.claim.config.EnvConfig;
import uk.gov.justice.laa.amend.claim.models.SqlStatement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static uk.gov.justice.laa.amend.claim.utils.TestDataUtils.generateUfn;

public class DatabaseQueryExecutor implements AutoCloseable {

    private final Connection connection;

    private final String crimeCalculatedFeeDetailId;
    private final String crimeClaimSummaryFeeId;
    private final String crimeClaimId;
    private final String crimeSubmissionId;
    private final String crimeBulkSubmissionId;

    private final String civilCalculatedFeeDetailId;
    private final String civilClaimSummaryFeeId;
    private final String civilClaimId;
    private final String civilSubmissionId;
    private final String civilBulkSubmissionId;
    
    private final String ufn;
    private final String createdBy;

    public DatabaseQueryExecutor() throws SQLException {
        String url = String.format("jdbc:postgresql://%s:%s/%s", EnvConfig.dbHost(), EnvConfig.dbPort(), EnvConfig.dbName());
        this.connection = DriverManager.getConnection(url, EnvConfig.dbUser(), EnvConfig.dbPassword());
        
        this.crimeCalculatedFeeDetailId = UUID.randomUUID().toString();
        this.crimeClaimSummaryFeeId = UUID.randomUUID().toString();
        this.crimeClaimId = UUID.randomUUID().toString();
        this.crimeSubmissionId = UUID.randomUUID().toString();
        this.crimeBulkSubmissionId = UUID.randomUUID().toString();

        this.civilCalculatedFeeDetailId = UUID.randomUUID().toString();
        this.civilClaimSummaryFeeId = UUID.randomUUID().toString();
        this.civilClaimId = UUID.randomUUID().toString();
        this.civilSubmissionId = UUID.randomUUID().toString();
        this.civilBulkSubmissionId = UUID.randomUUID().toString();
        
        this.ufn = generateUfn();
        this.createdBy = EnvConfig.getOrDefault("DB_CREATED_BY_USER_ID", "LAA-Amend-A-Claim-E2E-Tests");
    }

    public String getCrimeCalculatedFeeDetailId() {
        return crimeCalculatedFeeDetailId;
    }

    public String getCrimeClaimSummaryFeeId() {
        return crimeClaimSummaryFeeId;
    }

    public String getCrimeClaimId() {
        return crimeClaimId;
    }

    public String getCrimeSubmissionId() {
        return crimeSubmissionId;
    }

    public String getCrimeBulkSubmissionId() {
        return crimeBulkSubmissionId;
    }

    public String getCivilCalculatedFeeDetailId() {
        return civilCalculatedFeeDetailId;
    }

    public String getCivilClaimSummaryFeeId() {
        return civilClaimSummaryFeeId;
    }

    public String getCivilClaimId() {
        return civilClaimId;
    }

    public String getCivilSubmissionId() {
        return civilSubmissionId;
    }

    public String getCivilBulkSubmissionId() {
        return civilBulkSubmissionId;
    }

    public String getUfn() {
        return ufn;
    }

    // Caller is responsible for closing the ResultSet by using try-with-resources
    @SuppressWarnings("resource")
    public ResultSet executeQuery(SqlStatement sql) {
        try {
            PreparedStatement preparedStatement = prepareStatement(sql);
            return preparedStatement.executeQuery();
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute SQL query against DB", e);
        }
    }

    public void executeUpdate(SqlStatement sql) {
        try (PreparedStatement preparedStatement = prepareStatement(sql)) {
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute SQL update against DB", e);
        }
    }

    private PreparedStatement prepareStatement(SqlStatement sql) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(sql.sql());
        for (Map.Entry<Integer, Object> entry : sql.getParameters().entrySet()) {
            ps.setObject(entry.getKey(), entry.getValue());
        }
        return ps;
    }

    public void seed() {
        List<SqlStatement> files = List.of(
            SqlStatement.fromFile("crime", "insert-bulk-submission", List.of(crimeBulkSubmissionId, createdBy, createdBy)),
            SqlStatement.fromFile("civil", "insert-bulk-submission", List.of(civilBulkSubmissionId, createdBy, createdBy)),
            SqlStatement.fromFile("crime", "insert-submission", List.of(crimeSubmissionId, crimeBulkSubmissionId, createdBy, createdBy)),
            SqlStatement.fromFile("civil", "insert-submission", List.of(civilSubmissionId, civilBulkSubmissionId, createdBy, createdBy)),
            SqlStatement.fromFile("crime", "insert-claim", List.of(crimeClaimId, crimeSubmissionId, ufn, createdBy, createdBy)),
            SqlStatement.fromFile("civil", "insert-claim", List.of(civilClaimId, civilSubmissionId, ufn, createdBy, createdBy)),
            SqlStatement.fromFile("crime", "insert-claim-summary-fee", List.of(crimeClaimSummaryFeeId, crimeClaimId, createdBy)),
            SqlStatement.fromFile("civil", "insert-claim-summary-fee", List.of(civilClaimSummaryFeeId, civilClaimId, createdBy)),
            SqlStatement.fromFile("crime", "insert-calculated-fee-detail", List.of(crimeCalculatedFeeDetailId, crimeClaimSummaryFeeId, crimeClaimId, createdBy)),
            SqlStatement.fromFile("civil", "insert-calculated-fee-detail", List.of(civilCalculatedFeeDetailId, civilClaimSummaryFeeId, civilClaimId, createdBy))
        );

        files.forEach(this::executeUpdate);
    }

    public void delete() {
        delete("calculated_fee_detail", crimeCalculatedFeeDetailId, civilCalculatedFeeDetailId);
        delete("claim_summary_fee", crimeClaimSummaryFeeId, civilClaimSummaryFeeId);
        delete("claim", crimeClaimId, civilClaimId);
        delete("submission", crimeSubmissionId, civilSubmissionId);
        delete("bulk_submission", crimeBulkSubmissionId, civilBulkSubmissionId);
    }

    private void delete(String table, Object... ids) {
        String placeholders = Arrays.stream(ids).map(id -> "?::uuid").collect(Collectors.joining(", "));
        String sql = String.format("DELETE FROM claims.%s WHERE id IN (%s)", table, placeholders);
        executeUpdate(SqlStatement.fromRaw(sql, Arrays.asList(ids)));
    }

    public ResultSet select(String table, String id) {
        String sql = String.format("SELECT * FROM claims.%s WHERE id = ?::uuid", table);
        return executeQuery(SqlStatement.fromRaw(sql, List.of(id)));
    }

    @Override
    public void close() throws Exception {
        connection.close();
    }
}
