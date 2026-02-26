package uk.gov.justice.laa.amend.claim.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.amend.claim.models.FeatureFlag;

@Service
@Slf4j
public class FeatureFlagService {

    private final Path root;

    public FeatureFlagService(@Value("${config-map-roots.feature-flags}") Path root) {
        this.root = root;
    }

    public boolean isEnabled(FeatureFlag featureFlag) {
        var path = featureFlag.getPath(root);
        if (Files.exists(path)) {
            return readIsEnabled(path);
        }
        return false;
    }

    private boolean readIsEnabled(Path featureFlag) {
        try {
            return Boolean.parseBoolean(Files.readString(featureFlag).trim());
        } catch (IOException e) {
            log.error("Failed to read config map", e);
            return false;
        }
    }
}
