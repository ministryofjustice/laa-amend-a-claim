package uk.gov.justice.laa.amend.claim.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.ADJOURNED_FEE;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.CMRH_ORAL;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.CMRH_TELEPHONE;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.FIXED_FEE;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.HO_INTERVIEW;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.JR_FORM_FILLING;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.SUBSTANTIVE_HEARING;

public class ClaimFieldTest {

    @Test
    void constructorWithoutAmendedValueShouldUseSubmittedValue() {
        ClaimField claimField = new ClaimField("fooBar", BigDecimal.ONE, BigDecimal.TWO);

        Assertions.assertEquals("fooBar", claimField.getKey());
        Assertions.assertEquals("claimSummary.rows.fooBar", claimField.getLabel());
        Assertions.assertEquals("claimSummary.rows.fooBar.error", claimField.getErrorKey());
        Assertions.assertEquals("foo-bar", claimField.getId());
        Assertions.assertEquals(BigDecimal.ONE, claimField.getSubmitted());
        Assertions.assertEquals(BigDecimal.TWO, claimField.getCalculated());
        Assertions.assertEquals(BigDecimal.ONE, claimField.getAssessed());
        Assertions.assertNull(claimField.getChangeUrl());
    }


    @Nested
    class GetChangeUrlTests {

        String submissionId = "foo";
        String claimId = "bar";

        @Test
        void returnNullWhenCostIsNull() {
            ClaimField claimField = new ClaimField();
            Assertions.assertNull(claimField.getChangeUrl(submissionId, claimId));
        }

        @Test
        void returnUrlWhenRowCostIsNotNull() {
            ClaimField claimField = new ClaimField();
            claimField.setChangeUrl(Cost.PROFIT_COSTS.getChangeUrl());
            String expectedResult = "/submissions/foo/claims/bar/profit-costs";
            Assertions.assertEquals(expectedResult, claimField.getChangeUrl(submissionId, claimId));
        }
    }

    @Nested
    class DisplayTest {

        @Test
        void displayFixedFeeWhenSubmittedValueIsNull() {
            ClaimField claimField = new ClaimField();
            claimField.setKey(FIXED_FEE);
            claimField.setSubmitted(null);

            Assertions.assertTrue(claimField.display());
        }

        @Test
        void doNotDisplayCmrhTelephoneWhenSubmittedValueIsNull() {
            ClaimField claimField = new ClaimField();
            claimField.setKey(CMRH_TELEPHONE);
            claimField.setSubmitted(null);

            Assertions.assertFalse(claimField.display());
        }

        @Test
        void doNotDisplayCmrhOralWhenSubmittedValueIsNull() {
            ClaimField claimField = new ClaimField();
            claimField.setKey(CMRH_ORAL);
            claimField.setSubmitted(null);

            Assertions.assertFalse(claimField.display());
        }

        @Test
        void doNotDisplayJrFormFillingWhenSubmittedValueIsNull() {
            ClaimField claimField = new ClaimField();
            claimField.setKey(JR_FORM_FILLING);
            claimField.setSubmitted(null);

            Assertions.assertFalse(claimField.display());
        }

        @Test
        void doNotDisplayAdjournedFeeWhenSubmittedValueIsNull() {
            ClaimField claimField = new ClaimField();
            claimField.setKey(ADJOURNED_FEE);
            claimField.setSubmitted(null);

            Assertions.assertFalse(claimField.display());
        }

        @Test
        void doNotDisplayHoInterviewWhenSubmittedValueIsNull() {
            ClaimField claimField = new ClaimField();
            claimField.setKey(HO_INTERVIEW);
            claimField.setSubmitted(null);

            Assertions.assertFalse(claimField.display());
        }

        @Test
        void doNotDisplaySubstantiveHearingWhenSubmittedValueIsNull() {
            ClaimField claimField = new ClaimField();
            claimField.setKey(SUBSTANTIVE_HEARING);
            claimField.setSubmitted(null);

            Assertions.assertFalse(claimField.display());
        }
    }
}
