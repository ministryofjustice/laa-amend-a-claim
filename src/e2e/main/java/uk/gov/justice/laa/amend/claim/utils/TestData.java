package uk.gov.justice.laa.amend.claim.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class TestData {

    private TestData() {}

    // Global store across tests (scoped by "<scopeName>.<key>")
    private static final ConcurrentHashMap<String, String> STORE = new ConcurrentHashMap<>();

    /**
     * Seeds SQL fixture(s) from classpath.
     *
     * Supported tokens:
     *  - {{UFN}}               generated ddMMyy/NNN
     *  - {{CLAIM_ID}}          generated UUID
     *  - {{SUBMISSION_ID}}     generated UUID
     *  - {{BULK_SUBMISSION_ID}}generated UUID
     *  - {{CREATED_BY_USER_ID}}string (defaults to Data-Claims-Event-Service)
     *
     * Stores:
     *  - <scope>.meta.fixture
     *  - <scope>.db.<table>.<column> for each INSERT in file
     *  - Convenience:
     *      <scope>.claim.ufn
     *      <scope>.claim.id
     *      <scope>.submission.id
     *      <scope>.bulk_submission.id
     */
    public static Scope seed(String scopeName, String fixtureResourcePath) {
        Objects.requireNonNull(scopeName, "scopeName");
        Objects.requireNonNull(fixtureResourcePath, "fixtureResourcePath");

        String sql = readClasspathResource(fixtureResourcePath);

        // Generate tokens
        String generatedUfn = generateUfn();
        String generatedClaimId = UUID.randomUUID().toString();
        String generatedSubmissionId = UUID.randomUUID().toString();
        String generatedBulkSubmissionId = UUID.randomUUID().toString();

        // Keep this stable (matches your existing data)
        String createdBy = EnvConfig.getOrDefault("DB_CREATED_BY_USER_ID", "Data-Claims-Event-Service");

        Map<String, String> tokens = new LinkedHashMap<>();
        tokens.put("UFN", generatedUfn);
        tokens.put("CLAIM_ID", generatedClaimId);
        tokens.put("SUBMISSION_ID", generatedSubmissionId);
        tokens.put("BULK_SUBMISSION_ID", generatedBulkSubmissionId);
        tokens.put("CREATED_BY_USER_ID", createdBy);

        sql = replaceTokens(sql, tokens);

        executeSql(sql);

        // Parse every INSERT in the fixture and store in bucket
        List<InsertStatement> inserts = parseAllInserts(sql);

        put(scopeName, "meta.fixture", fixtureResourcePath);

        for (InsertStatement ins : inserts) {
            // store each inserted column under: <scope>.db.<table>.<column>
            for (int i = 0; i < ins.columns.size(); i++) {
                String col = normalizeColumnName(ins.columns.get(i));
                String val = i < ins.values.size() ? ins.values.get(i) : "";
                put(scopeName, "db." + ins.table + "." + col, val);
            }
        }

        // Convenience keys (used a lot in tests)
        put(scopeName, "claim.ufn", generatedUfn);
        put(scopeName, "claim.id", generatedClaimId);
        put(scopeName, "submission.id", generatedSubmissionId);
        put(scopeName, "bulk_submission.id", generatedBulkSubmissionId);

        return new Scope(scopeName);
    }

    public static final class Scope {
        private final String name;

        private Scope(String name) { this.name = name; }

        public String name() { return name; }

        public String get(String key) {
            String v = STORE.get(namespacedKey(name, key));
            if (v == null) {
                throw new IllegalStateException("No value in bucket for key: " + namespacedKey(name, key));
            }
            return v;
        }

        public String getOrNull(String key) {
            return STORE.get(namespacedKey(name, key));
        }

        public Map<String, String> all() {
            Map<String, String> out = new LinkedHashMap<>();
            String prefix = name + ".";
            for (Map.Entry<String, String> e : STORE.entrySet()) {
                if (e.getKey().startsWith(prefix)) {
                    out.put(e.getKey().substring(prefix.length()), e.getValue());
                }
            }
            return out;
        }
    }

    private static void put(String scopeName, String key, String value) {
        STORE.put(namespacedKey(scopeName, key), value == null ? "" : value);
    }

    private static String namespacedKey(String scopeName, String key) {
        return scopeName + "." + key;
    }

    private static String replaceTokens(String sql, Map<String, String> tokens) {
        String out = sql;
        for (Map.Entry<String, String> e : tokens.entrySet()) {
            out = out.replace("{{" + e.getKey() + "}}", e.getValue());
        }
        return out;
    }

    private static void executeSql(String sql) {
        String password = EnvConfig.dbPassword();
        if (password == null || password.isBlank()) {
            throw new IllegalStateException("DB_PASSWORD is not set (env or .env).");
        }

        String jdbcUrl = "jdbc:postgresql://" + EnvConfig.dbHost() + ":" + EnvConfig.dbPort() + "/" + EnvConfig.dbName();

        try (Connection conn = DriverManager.getConnection(jdbcUrl, EnvConfig.dbUser(), password);
             Statement stmt = conn.createStatement()) {

            conn.setAutoCommit(false);

            // Allow multiple statements separated by semicolons
            for (String statement : splitStatements(sql)) {
                if (!statement.isBlank()) {
                    stmt.execute(statement);
                }
            }

            conn.commit();

        } catch (Exception e) {
            throw new RuntimeException("Failed to execute SQL fixture against DB", e);
        }
    }

    private static List<String> splitStatements(String sql) {
        // naive but good enough for fixtures: split on semicolon not inside quotes
        List<String> out = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inQuote = false;

        for (int i = 0; i < sql.length(); i++) {
            char c = sql.charAt(i);

            if (c == '\'') {
                // handle escaped '' inside strings
                if (inQuote && i + 1 < sql.length() && sql.charAt(i + 1) == '\'') {
                    cur.append("''");
                    i++;
                    continue;
                }
                inQuote = !inQuote;
                cur.append(c);
                continue;
            }

            if (c == ';' && !inQuote) {
                out.add(cur.toString().trim());
                cur.setLength(0);
                continue;
            }

            cur.append(c);
        }

        if (cur.length() > 0) out.add(cur.toString().trim());
        return out;
    }

    private static String readClasspathResource(String resourcePath) {
        InputStream is = TestData.class.getClassLoader().getResourceAsStream(resourcePath);
        if (is == null) {
            throw new IllegalArgumentException("Fixture not found on classpath: " + resourcePath);
        }
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString().trim();
        } catch (Exception e) {
            throw new RuntimeException("Failed to read fixture: " + resourcePath, e);
        }
    }

    private static String generateUfn() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyy"));
        int n = (int) (System.nanoTime() % 1000);
        if (n < 0) n = -n;
        String suffix = String.format("%03d", n);
        return datePart + "/" + suffix;
    }

    private static String normalizeColumnName(String raw) {
        if (raw == null) return "";
        String c = raw.trim();
        // Strip double quotes around identifiers: "version" -> version
        if (c.startsWith("\"") && c.endsWith("\"") && c.length() > 1) {
            c = c.substring(1, c.length() - 1);
        }
        return c;
    }

    private static final class InsertStatement {
        final String table; // schema.table
        final List<String> columns;
        final List<String> values;

        InsertStatement(String table, List<String> columns, List<String> values) {
            this.table = table;
            this.columns = columns;
            this.values = values;
        }
    }

    private static List<InsertStatement> parseAllInserts(String sql) {
        // parse each "INSERT INTO <table> (cols) VALUES (vals)" block
        List<InsertStatement> out = new ArrayList<>();
        String normalized = sql.replaceAll("\\s+", " ").trim();

        String upper = normalized.toUpperCase(Locale.ROOT);
        int idx = 0;

        while (true) {
            int intoIdx = upper.indexOf("INSERT INTO ", idx);
            if (intoIdx < 0) break;

            int colsOpen = normalized.indexOf('(', intoIdx);
            if (colsOpen < 0) throw new IllegalArgumentException("INSERT missing column list");

            String table = normalized.substring(intoIdx + "INSERT INTO ".length(), colsOpen).trim();

            int colsClose = findMatchingParen(normalized, colsOpen);
            String colsRaw = normalized.substring(colsOpen + 1, colsClose).trim();
            List<String> columns = splitCsv(colsRaw);

            int valuesIdx = upper.indexOf(" VALUES ", colsClose);
            if (valuesIdx < 0) throw new IllegalArgumentException("INSERT missing VALUES");

            int valsOpen = normalized.indexOf('(', valuesIdx);
            int valsClose = findMatchingParen(normalized, valsOpen);
            String valsRaw = normalized.substring(valsOpen + 1, valsClose).trim();
            List<String> values = splitSqlValues(valsRaw);

            List<String> cleaned = new ArrayList<>(values.size());
            for (String v : values) cleaned.add(cleanValue(v));

            out.add(new InsertStatement(table, columns, cleaned));

            idx = valsClose;
        }

        return out;
    }

    private static int findMatchingParen(String s, int openParenIdx) {
        int depth = 0;
        boolean inQuote = false;
        for (int i = openParenIdx; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\'') {
                // handle escaped '' inside strings
                if (inQuote && i + 1 < s.length() && s.charAt(i + 1) == '\'') {
                    i++;
                    continue;
                }
                inQuote = !inQuote;
            }
            if (inQuote) continue;

            if (c == '(') depth++;
            if (c == ')') {
                depth--;
                if (depth == 0) return i;
            }
        }
        throw new IllegalArgumentException("Unbalanced parentheses in SQL");
    }

    private static List<String> splitCsv(String raw) {
        String[] parts = raw.split(",");
        List<String> out = new ArrayList<>();
        for (String p : parts) out.add(p.trim());
        return out;
    }

    private static List<String> splitSqlValues(String raw) {
        List<String> out = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inQuote = false;

        for (int i = 0; i < raw.length(); i++) {
            char c = raw.charAt(i);

            if (c == '\'') {
                if (inQuote && i + 1 < raw.length() && raw.charAt(i + 1) == '\'') {
                    cur.append("''");
                    i++;
                    continue;
                }
                inQuote = !inQuote;
                cur.append(c);
                continue;
            }

            if (c == ',' && !inQuote) {
                out.add(cur.toString().trim());
                cur.setLength(0);
                continue;
            }

            cur.append(c);
        }

        if (cur.length() > 0) out.add(cur.toString().trim());
        return out;
    }

    private static String cleanValue(String raw) {
        if (raw == null) return "";
        String v = raw.trim();

        // Strip casts like ::uuid or ::jsonb
        v = v.replaceAll("::[a-zA-Z0-9_]+", "").trim();

        if (v.equalsIgnoreCase("null")) return "null";
        if (v.equalsIgnoreCase("true") || v.equalsIgnoreCase("false")) return v.toLowerCase(Locale.ROOT);

        if (v.startsWith("'") && v.endsWith("'") && v.length() >= 2) {
            v = v.substring(1, v.length() - 1);
            v = v.replace("''", "'");
        }

        return v;
    }
}