package uk.gov.justice.laa.amend.claim.models;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public record Scope (String name, ConcurrentHashMap<String, String> store) {

    public Scope(String name) {
        this(name, new ConcurrentHashMap<>());
    }

    public String get(String key) {
        String v = store.get(namespacedKey(name, key));
        if (v == null) {
            throw new IllegalStateException("No value in bucket for key: " + namespacedKey(name, key));
        }
        return v;
    }

    public String getOrNull(String key) {
        return store.get(namespacedKey(name, key));
    }

    public Map<String, String> all() {
        Map<String, String> out = new LinkedHashMap<>();
        String prefix = name + ".";
        for (Map.Entry<String, String> e : store.entrySet()) {
            if (e.getKey().startsWith(prefix)) {
                out.put(e.getKey().substring(prefix.length()), e.getValue());
            }
        }
        return out;
    }

    public void put(String key, String value) {
        store.put(namespacedKey(name, key), value == null ? "" : value);
    }

    private String namespacedKey(String scopeName, String key) {
        return scopeName + "." + key;
    }
}
