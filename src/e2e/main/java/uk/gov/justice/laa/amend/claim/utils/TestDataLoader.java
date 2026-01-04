package uk.gov.justice.laa.amend.claim.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;

public final class TestDataLoader {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private TestDataLoader() {}

    public static <T> T loadJsonFromResources(String resourcePath, Class<T> clazz) {
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IllegalArgumentException("Could not find resource on classpath: " + resourcePath);
            }
            return MAPPER.readValue(is, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load test data: " + resourcePath, e);
        }
    }
}