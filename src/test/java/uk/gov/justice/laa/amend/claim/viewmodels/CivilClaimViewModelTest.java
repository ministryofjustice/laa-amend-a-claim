package uk.gov.justice.laa.amend.claim.viewmodels;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.amend.claim.models.CivilClaim;

import java.time.LocalDate;
import java.time.YearMonth;

public class CivilClaimViewModelTest {

    @Nested
    class GetMatterTypeCodeOneTests {
        @Test
        void getMatterTypeCodeOneWhenInExpectedFormat() {
            CivilClaim claim = new CivilClaim();
            claim.setMatterTypeCode("IMLB+AHQS");
            CivilClaimViewModel viewModel = new CivilClaimViewModel(claim);
            Assertions.assertEquals("IMLB", viewModel.getMatterTypeCodeOne());
        }

        @Test
        void getMatterTypeCodeOneWhenInUnexpectedFormat() {
            CivilClaim claim = new CivilClaim();
            claim.setMatterTypeCode("IMLB");
            CivilClaimViewModel viewModel = new CivilClaimViewModel(claim);
            Assertions.assertEquals("IMLB", viewModel.getMatterTypeCodeOne());
        }

        @Test
        void getMatterTypeCodeOneWhenNull() {
            CivilClaim claim = new CivilClaim();
            claim.setMatterTypeCode(null);
            CivilClaimViewModel viewModel = new CivilClaimViewModel(claim);
            Assertions.assertNull(null, viewModel.getMatterTypeCodeOne());
        }

        @Test
        void getMatterTypeCodeOneWhenEmpty() {
            CivilClaim claim = new CivilClaim();
            claim.setMatterTypeCode("");
            CivilClaimViewModel viewModel = new CivilClaimViewModel(claim);
            Assertions.assertNull(null, viewModel.getMatterTypeCodeOne());
        }

        @Test
        void getMatterTypeCodeOneWhenBlank() {
            CivilClaim claim = new CivilClaim();
            claim.setMatterTypeCode(" ");
            CivilClaimViewModel viewModel = new CivilClaimViewModel(claim);
            Assertions.assertNull(null, viewModel.getMatterTypeCodeOne());
        }
    }

    @Nested
    class GetMatterTypeCodeTwoTests {
        @Test
        void getMatterTypeCodeTwoWhenInExpectedFormat() {
            CivilClaim claim = new CivilClaim();
            claim.setMatterTypeCode("IMLB+AHQS");
            CivilClaimViewModel viewModel = new CivilClaimViewModel(claim);
            Assertions.assertEquals("AHQS", viewModel.getMatterTypeCodeTwo());
        }

        @Test
        void getMatterTypeCodeTwoWhenInUnexpectedFormat() {
            CivilClaim claim = new CivilClaim();
            claim.setMatterTypeCode("IMLB");
            CivilClaimViewModel viewModel = new CivilClaimViewModel(claim);
            Assertions.assertNull(viewModel.getMatterTypeCodeTwo());
        }

        @Test
        void getMatterTypeCodeTwoWhenNull() {
            CivilClaim claim = new CivilClaim();
            claim.setMatterTypeCode(null);
            CivilClaimViewModel viewModel = new CivilClaimViewModel(claim);
            Assertions.assertNull(null, viewModel.getMatterTypeCodeTwo());
        }

        @Test
        void getMatterTypeCodeTwoWhenEmpty() {
            CivilClaim claim = new CivilClaim();
            claim.setMatterTypeCode("");
            CivilClaimViewModel viewModel = new CivilClaimViewModel(claim);
            Assertions.assertNull(null, viewModel.getMatterTypeCodeTwo());
        }

        @Test
        void getMatterTypeCodeTwoWhenBlank() {
            CivilClaim claim = new CivilClaim();
            claim.setMatterTypeCode(" ");
            CivilClaimViewModel viewModel = new CivilClaimViewModel(claim);
            Assertions.assertNull(null, viewModel.getMatterTypeCodeTwo());
        }
    }
}
