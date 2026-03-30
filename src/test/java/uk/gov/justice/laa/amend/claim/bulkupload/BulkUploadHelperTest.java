package uk.gov.justice.laa.amend.claim.bulkupload;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.amend.claim.bulkupload.civil.BulkUploadCivilClaim;
import uk.gov.justice.laa.amend.claim.mappers.ClaimMapper;
import uk.gov.justice.laa.amend.claim.service.ClaimService;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResponseV2;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResultSetV2;

class BulkUploadHelperTest {

    private ClaimService claimService;
    private ClaimMapper claimMapper;
    private BulkUploadHelper helper;

    @BeforeEach
    void setup() {
        claimService = mock(ClaimService.class);
        claimMapper = mock(ClaimMapper.class);
        helper = new BulkUploadHelper(claimService, claimMapper);
    }

    @Test
    void getOfficeCodeToUfnIdxValidRowsShouldMapRowsCorrectly() {
        List<BulkUploadCivilClaim> rows = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        BulkUploadCivilClaim row1 = row("123456", "20220101/020244");
        BulkUploadCivilClaim row2 = row("123456", "20220101/020234");
        rows.add(row1);
        rows.add(row2);

        Map<Pair<String, String>, BulkUploadCivilClaim> map = helper.getOfficeCodeUfnRows(rows, errors);

        assertTrue(errors.isEmpty());
        assertEquals(2, map.size());
        assertEquals(2, map.get(Pair.of("123456", "20220101/020244")).getRowNumber());
        assertEquals(3, map.get(Pair.of("123456", "20220101/020234")).getRowNumber());
    }

    @Test
    void getAllClaimsShouldMatchClaimsAndReturnClaims() {
        String officeCode = "123456";
        // Mock claim returned from paging API
        ClaimResponseV2 claim = new ClaimResponseV2();
        claim.setUniqueFileNumber("20220101/020244");

        when(claimService.searchClaims(eq(officeCode), any(), any(), any(), any(), any(), eq(1), eq(200), isNull()))
                .thenReturn(claimList(claim));

        // After page 1, return empty content (stop)
        when(claimService.searchClaims(eq(officeCode), any(), any(), any(), any(), any(), eq(2), eq(200), isNull()))
                .thenReturn(emptyList());

        List<String> errors = new ArrayList<>();
        BulkUploadCivilClaim row = row(officeCode, "20220101/020244");
        List<BulkUploadCivilClaim> rows = List.of(row);
        var response = helper.getAllClaims(rows, errors);

        assertNotNull(response);
        assertTrue(errors.isEmpty());
    }

    private BulkUploadCivilClaim row(String officeCode, String ufn) {
        BulkUploadCivilClaim r = new BulkUploadCivilClaim();
        r.setOfficeCode(officeCode);
        r.setUfn(ufn);
        r.setCounselCosts(BigDecimal.valueOf(100));
        r.setDisbursements(BigDecimal.valueOf(200));
        r.setAssessmentOutcome("Reduced to Fixed fee");
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
