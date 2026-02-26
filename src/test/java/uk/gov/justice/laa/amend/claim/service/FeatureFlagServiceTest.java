package uk.gov.justice.laa.amend.claim.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.amend.claim.models.FeatureFlag;

@ExtendWith(MockitoExtension.class)
public class FeatureFlagServiceTest {

    private static final Path ROOT = Path.of("/config/featureFlags");

    private FeatureFlagService service;

    @BeforeEach
    void setup() {
        service = new FeatureFlagService(ROOT);
    }

    static List<FeatureFlag> featureFlags() {
        return List.of(FeatureFlag.IS_VOIDING_ENABLED);
    }

    @ParameterizedTest
    @MethodSource("featureFlags")
    void featureFlagIsEnabled(FeatureFlag featureFlag) {
        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.exists(featureFlag.getPath(ROOT))).thenReturn(true);
            mockedFiles.when(() -> Files.readString(featureFlag.getPath(ROOT))).thenReturn("true");

            assertTrue(service.isEnabled(featureFlag));
        }
    }

    @ParameterizedTest
    @MethodSource("featureFlags")
    void featureFlagIsDisabled(FeatureFlag featureFlag) {
        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.exists(featureFlag.getPath(ROOT))).thenReturn(true);
            mockedFiles.when(() -> Files.readString(featureFlag.getPath(ROOT))).thenReturn("false");

            assertFalse(service.isEnabled(featureFlag));
        }
    }

    @ParameterizedTest
    @MethodSource("featureFlags")
    void featureFlagIsMissing(FeatureFlag featureFlag) {
        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.exists(featureFlag.getPath(ROOT))).thenReturn(false);

            assertFalse(service.isEnabled(featureFlag));
        }
    }
}
