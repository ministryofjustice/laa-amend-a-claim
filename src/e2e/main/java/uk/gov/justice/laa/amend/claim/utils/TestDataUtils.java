package uk.gov.justice.laa.amend.claim.utils;

import uk.gov.justice.laa.amend.claim.models.InsertStatement;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class TestDataUtils {

    public static String replaceTokens(String sql, Map<String, String> tokens) {
        String out = sql;
        for (Map.Entry<String, String> e : tokens.entrySet()) {
            out = out.replace("{{" + e.getKey() + "}}", e.getValue());
        }
        return out;
    }

    public static String readClasspathResource(String resourcePath) {
        InputStream is = TestDataUtils.class.getClassLoader().getResourceAsStream(resourcePath);
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

    public static String generateUfn() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyy"));
        int n = (int) (System.nanoTime() % 1000);
        if (n < 0) n = -n;
        String suffix = String.format("%03d", n);
        return datePart + "/" + suffix;
    }

    public static String normalizeColumnName(String raw) {
        if (raw == null) return "";
        String c = raw.trim();
        // Strip double quotes around identifiers: "version" -> version
        if (c.startsWith("\"") && c.endsWith("\"") && c.length() > 1) {
            c = c.substring(1, c.length() - 1);
        }
        return c;
    }

    public static List<InsertStatement> parseAllInserts(String sql) {
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

        if (!cur.isEmpty()) out.add(cur.toString().trim());
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