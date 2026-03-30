package uk.gov.justice.laa.amend.claim.bulkupload;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
        List<String> errors = new ArrayList<>();

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
        String officeCode = "123456";
        // Mock claim returned from paging API
        ClaimResponseV2 claim = new ClaimResponseV2();
        claim.setUniqueFileNumber("20220101/020244");

        when(claimService.searchClaims(
                        officeCode,
                        Optional.empty(),
                        Optional.empty(),
                        Optional.empty(),
                        Optional.of(AreaOfLaw.LEGAL_HELP),
                        Optional.of(true),
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
                        2,
                        200,
                        null))
                .thenReturn(emptyList());

        List<String> errors = new ArrayList<>();
        BulkUploadCivilClaim row = row(officeCode, "20220101/020244", 1);
        List<BulkUploadCivilClaim> rows = List.of(row);
        var response = helper.getAllClaims(rows, errors);

        assertNotNull(response);
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
