package uk.gov.justice.laa.amend.claim.models;

import java.nio.file.Path;

public enum FeatureFlag {
    IS_VOIDING_ENABLED("is-voiding-enabled");

    private final String key;

    FeatureFlag(String key) {
        this.key = key;
    }

    public Path getPath(Path root) {
        return root.resolve(key);
    }
}
