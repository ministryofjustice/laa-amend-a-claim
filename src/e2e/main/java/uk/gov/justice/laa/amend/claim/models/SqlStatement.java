package uk.gov.justice.laa.amend.claim.models;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static uk.gov.justice.laa.amend.claim.utils.TestDataUtils.readClasspathResource;

public record SqlStatement(String sql, List<Object> parameters) {

    public Map<Integer, Object> getParameters() {
        Map<Integer, Object> map = new LinkedHashMap<>();
        for (int i = 0; i < parameters.size(); i++) {
            map.put(i + 1, parameters.get(i));
        }
        return map;
    }

    public static SqlStatement fromFile(String directory, String name, List<Object> parameters) {
        String path = String.format("fixtures/db/claims/%s/%s.sql", directory, name);
        String sql = readClasspathResource(path);
        return new SqlStatement(sql, parameters);
    }

    public static SqlStatement fromRaw(String sql, List<Object> parameters) {
        return new SqlStatement(sql, parameters);
    }
}
