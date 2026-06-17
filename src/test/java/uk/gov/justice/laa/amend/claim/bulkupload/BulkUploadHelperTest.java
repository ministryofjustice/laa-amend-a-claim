package uk.gov.justice.laa.amend.claim.bulkupload;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimStatus.VALID;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.amend.claim.bulkupload.civil.BulkUploadCivilClaim;
import uk.gov.justice.laa.amend.claim.models.AreaOfLaw;
import uk.gov.justice.laa.amend.claim.service.ClaimService;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResponseV2;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResultSetV2;

class BulkUploadHelperTest {

  private ClaimService claimService;
  private BulkUploadHelper helper;

  @BeforeEach
  void setup() {
    claimService = mock(ClaimService.class);
    helper = new BulkUploadHelper(claimService);
  }

  @Test
  void getOfficeCodeToUfnIdxValidRowsShouldMapRowsCorrectly() {
    List<BulkUploadCivilClaim> rows = new ArrayList<>();
    List<BulkUploadError> errors = new ArrayList<>();

    BulkUploadCivilClaim row1 = row("123456", "131019/020", 1);
    BulkUploadCivilClaim row2 = row("123456", "131019/024", 2);
    rows.add(row1);
    rows.add(row2);

    Map<Pair<String, String>, BulkUploadCivilClaim> map = helper.getOfficeCodeUfnRows(rows, errors);

    assertTrue(errors.isEmpty());
    assertEquals(2, map.size());
    assertEquals(1, map.get(Pair.of("123456", "131019/020")).getRowNumber());
    assertEquals(2, map.get(Pair.of("123456", "131019/024")).getRowNumber());
  }

  @Test
  void getAllClaimsShouldMatchClaimsAndReturnClaims() {
    var officeCode = "123456";
    var ufn = "20220101/020244";
    // Mock claim returned from paging API
    ClaimResponseV2 claim = new ClaimResponseV2();
    claim.setId("11111111-1111-1111-1111-111111111111");
    claim.setOfficeCode(officeCode);
    claim.setUniqueFileNumber(ufn);
    claim.setStatus(VALID);

    when(claimService.searchClaims(
            officeCode,
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.of(AreaOfLaw.LEGAL_HELP),
            Optional.of(true),
            List.of(VALID),
            1,
            200,
            null))
        .thenReturn(claimList(claim));

    // After page 1, return empty content (stop)
    when(claimService.searchClaims(
            officeCode,
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.of(AreaOfLaw.LEGAL_HELP),
            Optional.of(true),
            List.of(VALID),
            2,
            200,
            null))
        .thenReturn(emptyList());

    List<BulkUploadError> errors = new ArrayList<>();
    BulkUploadCivilClaim row = row(officeCode, ufn, 1);
    List<BulkUploadCivilClaim> rows = List.of(row);
    var response = helper.getAllClaims(rows, errors);

    assertEquals(List.of(claim), response);
    assertTrue(errors.isEmpty());
  }

  @Test
  void getAllClaimsShouldDeduplicateSameClaimReturnedAcrossPages() {
    // Simulate the API returning the same claim on multiple pages due to unstable pagination.
    // The claim should be deduplicated and not treated as a duplicate submission.
    var officeCode = "123456";
    var ufn = "20220101/020244";
    ClaimResponseV2 claim = new ClaimResponseV2();
    claim.setId("22222222-2222-2222-2222-222222222222");
    claim.setOfficeCode(officeCode);
    claim.setUniqueFileNumber(ufn);
    claim.setStatus(VALID);

    ClaimResultSetV2 pageOne = claimList(claim);
    pageOne.setTotalPages(2);
    when(claimService.searchClaims(
            officeCode,
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.of(AreaOfLaw.LEGAL_HELP),
            Optional.of(true),
            List.of(VALID),
            1,
            200,
            null))
        .thenReturn(pageOne);

    ClaimResultSetV2 pageTwo = claimList(claim);
    pageTwo.setTotalPages(2);
    when(claimService.searchClaims(
            officeCode,
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.of(AreaOfLaw.LEGAL_HELP),
            Optional.of(true),
            List.of(VALID),
            2,
            200,
            null))
        .thenReturn(pageTwo);

    List<BulkUploadError> errors = new ArrayList<>();
    var response = helper.getAllClaims(List.of(row(officeCode, ufn, 1)), errors);

    assertEquals(List.of(claim), response);
    assertTrue(errors.isEmpty());
  }

  @Test
  void getAllClaimsShouldRetainDistinctClaimsWithSameOfficeAndUfn() {
    // Two distinct claims sharing an office code and UFN must both be
    // retained so downstream duplicate detection can still flag them.
    var officeCode = "123456";
    var ufn = "20220101/020244";
    ClaimResponseV2 claimOne = new ClaimResponseV2();
    claimOne.setId("11111111-1111-1111-1111-111111111111");
    claimOne.setOfficeCode(officeCode);
    claimOne.setUniqueFileNumber(ufn);
    claimOne.setStatus(VALID);

    ClaimResponseV2 claimTwo = new ClaimResponseV2();
    claimTwo.setId("22222222-2222-2222-2222-222222222222");
    claimTwo.setOfficeCode(officeCode);
    claimTwo.setUniqueFileNumber(ufn);
    claimTwo.setStatus(VALID);

    when(claimService.searchClaims(
            officeCode,
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.of(AreaOfLaw.LEGAL_HELP),
            Optional.of(true),
            List.of(VALID),
            1,
            200,
            null))
        .thenReturn(claimList(claimOne, claimTwo));

    when(claimService.searchClaims(
            officeCode,
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.of(AreaOfLaw.LEGAL_HELP),
            Optional.of(true),
            List.of(VALID),
            2,
            200,
            null))
        .thenReturn(emptyList());

    List<BulkUploadError> errors = new ArrayList<>();
    var response = helper.getAllClaims(List.of(row(officeCode, ufn, 1)), errors);

    assertEquals(List.of(claimOne, claimTwo), response);
    assertTrue(errors.isEmpty());
  }

  private BulkUploadCivilClaim row(String officeCode, String ufn, int rowNumber) {
    BulkUploadCivilClaim r = new BulkUploadCivilClaim();
    r.setOfficeCode(officeCode);
    r.setUfn(ufn);
    r.setCounselCosts(BigDecimal.valueOf(100));
    r.setDisbursements(BigDecimal.valueOf(200));
    r.setAssessmentOutcome("Reduced to Fixed fee");
    r.setRowNumber(rowNumber);
    return r;
  }

  private ClaimResultSetV2 claimList(ClaimResponseV2... claims) {
    ClaimResultSetV2 result = new ClaimResultSetV2();
    result.setContent(Arrays.asList(claims));
    return result;
  }

  private ClaimResultSetV2 emptyList() {
    ClaimResultSetV2 result = new ClaimResultSetV2();
    result.setContent(Collections.emptyList());
    return result;
  }
}
