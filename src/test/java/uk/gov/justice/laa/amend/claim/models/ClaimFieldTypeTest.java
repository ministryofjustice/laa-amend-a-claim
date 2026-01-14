package uk.gov.justice.laa.amend.claim.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class ClaimFieldTypeTest {

    @Nested
    class IsNotAssessableTests {

        @Test
        void returnsFalseForNormal() {
            ClaimFieldType cft = ClaimFieldType.NORMAL;
            Assertions.assertFalse(cft.isNotAssessable());
        }

        @Test
        void returnsTrueForBoltOn() {
            ClaimFieldType cft = ClaimFieldType.BOLT_ON;
            Assertions.assertTrue(cft.isNotAssessable());
        }

        @Test
        void returnsFalseForAssessed() {
            ClaimFieldType cft = ClaimFieldType.ASSESSED;
            Assertions.assertFalse(cft.isNotAssessable());
        }

        @Test
        void returnsFalseForAllowed() {
            ClaimFieldType cft = ClaimFieldType.ALLOWED;
            Assertions.assertFalse(cft.isNotAssessable());
        }

        @Test
        void returnsTrueForTotal() {
            ClaimFieldType cft = ClaimFieldType.TOTAL;
            Assertions.assertTrue(cft.isNotAssessable());
        }

        @Test
        void returnsTrueForFixedFee() {
            ClaimFieldType cft = ClaimFieldType.FIXED_FEE;
            Assertions.assertTrue(cft.isNotAssessable());
        }
    }
}
