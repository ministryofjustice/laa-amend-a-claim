package uk.gov.justice.laa.amend.claim.models;

import java.nio.file.Path;
import java.nio.file.Paths;

public enum FeatureFlag {
    IS_VOIDING_ENABLED("is-voiding-enabled");

    private static final Path ROOT = Paths.get("/config/feature-flags");

    private final String key;

    FeatureFlag(String key) {
        this.key = key;
    }

    public Path getPath() {
        return ROOT.resolve(key);
    }
}
