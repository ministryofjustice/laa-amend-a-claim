package uk.gov.justice.laa.amend.claim.bulkupload;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimStatus.VALID;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
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
    var claimId = "11111111-1111-1111-1111-111111111111";
    whenSearchPage(officeCode, 1).thenReturn(claimList(claim(claimId, officeCode, ufn)));

    List<BulkUploadError> errors = new ArrayList<>();
    var response = helper.getAllClaims(List.of(row(officeCode, ufn, 1)), errors);

    assertEquals(1, response.size());
    assertEquals(claimId, response.getFirst().getId());
    assertTrue(errors.isEmpty());
  }

  @Test
  void getAllClaimsShouldDeduplicateSameClaimReturnedAcrossPages() {
    // Unstable pagination returns the same claim (same ID) on two pages as distinct instances.
    // It must be deduplicated by claim ID, not treated as a duplicate submission.
    var officeCode = "123456";
    var ufn = "20220101/020244";
    var sharedId = "22222222-2222-2222-2222-222222222222";
    ClaimResponseV2 pageOneClaim = claim(sharedId, officeCode, ufn);
    ClaimResponseV2 pageTwoClaim = claim(sharedId, officeCode, ufn);

    ClaimResultSetV2 pageOne = claimList(pageOneClaim);
    pageOne.setTotalPages(2);
    whenSearchPage(officeCode, 1).thenReturn(pageOne);

    ClaimResultSetV2 pageTwo = claimList(pageTwoClaim);
    pageTwo.setTotalPages(2);
    whenSearchPage(officeCode, 2).thenReturn(pageTwo);

    List<BulkUploadError> errors = new ArrayList<>();
    var response = helper.getAllClaims(List.of(row(officeCode, ufn, 1)), errors);

    assertEquals(1, response.size());
    assertEquals(sharedId, response.getFirst().getId());
    assertTrue(errors.isEmpty());
  }

  @Test
  void getAllClaimsShouldRetainDistinctClaimsWithSameOfficeAndUfn() {
    // Two distinct claims sharing an office code and UFN must both be
    // retained so downstream duplicate detection can still flag them.
    var officeCode = "123456";
    var ufn = "20220101/020244";
    var claimOneId = "11111111-1111-1111-1111-111111111111";
    var claimTwoId = "22222222-2222-2222-2222-222222222222";
    ClaimResponseV2 claimOne = claim(claimOneId, officeCode, ufn);
    ClaimResponseV2 claimTwo = claim(claimTwoId, officeCode, ufn);

    whenSearchPage(officeCode, 1).thenReturn(claimList(claimOne, claimTwo));

    List<BulkUploadError> errors = new ArrayList<>();
    var response = helper.getAllClaims(List.of(row(officeCode, ufn, 1)), errors);

    assertEquals(2, response.size());
    assertEquals(
        List.of(claimOneId, claimTwoId), response.stream().map(ClaimResponseV2::getId).toList());
    assertTrue(errors.isEmpty());
  }

  @Test
  void getAllClaimsShouldRetryUntilAllClaimsReportedByTotalElementsAreResolved() {
    // The first sweep skips a claim (returns fewer than total_elements); a retry reconciles the
    // full set, so no claim is missed.
    var officeCode = "123456";
    var ufn = "20220101/020244";
    ClaimResponseV2 claimOne = claim("11111111-1111-1111-1111-111111111111", officeCode, ufn);
    ClaimResponseV2 claimTwo = claim("22222222-2222-2222-2222-222222222222", officeCode, ufn);

    ClaimResultSetV2 incompleteSweep = claimList(claimOne);
    incompleteSweep.setTotalElements(2);
    ClaimResultSetV2 completeSweep = claimList(claimOne, claimTwo);

    whenSearchPage(officeCode, 1).thenReturn(incompleteSweep, completeSweep);

    List<BulkUploadError> errors = new ArrayList<>();
    var response = helper.getAllClaims(List.of(row(officeCode, ufn, 1)), errors);

    assertEquals(2, response.size());
    assertTrue(response.containsAll(List.of(claimOne, claimTwo)));
    assertTrue(errors.isEmpty());
  }

  @Test
  void getAllClaimsShouldFailWhenClaimsCannotBeReconciledWithTotalElements() {
    // Every sweep returns fewer claims than total_elements reports, so completeness can never be
    // confirmed. The upload must fail loudly rather than silently dropping a claim.
    var officeCode = "123456";
    var ufn = "20220101/020244";
    ClaimResponseV2 claimOne = claim("11111111-1111-1111-1111-111111111111", officeCode, ufn);

    ClaimResultSetV2 alwaysIncomplete = claimList(claimOne);
    alwaysIncomplete.setTotalElements(2);
    whenSearchPage(officeCode, 1).thenReturn(alwaysIncomplete);

    List<BulkUploadCivilClaim> rows = List.of(row(officeCode, ufn, 1));
    List<BulkUploadError> errors = new ArrayList<>();

    RuntimeException exception =
        assertThrows(RuntimeException.class, () -> helper.getAllClaims(rows, errors));
    assertTrue(exception.getMessage().contains(officeCode));
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
    result.setTotalPages(1);
    result.setTotalElements(claims.length);
    return result;
  }

  private ClaimResponseV2 claim(String id, String officeCode, String ufn) {
    ClaimResponseV2 claim = new ClaimResponseV2();
    claim.setId(id);
    claim.setOfficeCode(officeCode);
    claim.setUniqueFileNumber(ufn);
    claim.setStatus(VALID);
    return claim;
  }

  private org.mockito.stubbing.OngoingStubbing<ClaimResultSetV2> whenSearchPage(
      String officeCode, int page) {
    return when(
        claimService.searchClaims(
            officeCode,
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.of(AreaOfLaw.LEGAL_HELP),
            Optional.of(true),
            List.of(VALID),
            page,
            200,
            null));
  }
}
