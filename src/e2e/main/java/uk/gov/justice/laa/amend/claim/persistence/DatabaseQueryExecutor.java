package uk.gov.justice.laa.amend.claim.persistence;

import uk.gov.justice.laa.amend.claim.config.EnvConfig;
import uk.gov.justice.laa.amend.claim.models.Insert;
import uk.gov.justice.laa.amend.claim.models.SqlStatement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class DatabaseQueryExecutor implements AutoCloseable {

    private final Connection connection;

    public DatabaseQueryExecutor() throws SQLException {
        String url = EnvConfig.dbConnectionUrl();
        this.connection = DriverManager.getConnection(url, EnvConfig.dbUser(), EnvConfig.dbPassword());
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

    public void seed(List<Insert> inserts) {
        inserts
            .stream()
            .map(SqlStatement::fromFile)
            .forEach(this::executeUpdate);
    }

    public void clean() {
        delete("assessment");
        delete("calculated_fee_detail");
        delete("claim_summary_fee");
        delete("client");
        delete("claim");
        delete("submission");
        delete("bulk_submission");
    }

    public void delete(String table) {
        String sql = String.format("DELETE FROM claims.%s", table);
        executeUpdate(SqlStatement.fromRaw(sql));
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
