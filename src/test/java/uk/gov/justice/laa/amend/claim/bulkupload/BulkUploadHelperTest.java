package uk.gov.justice.laa.amend.claim.bulkupload;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions.createAllowedTotalInclVatField;
import static uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions.createAllowedTotalVatField;
import static uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions.createAssessedTotalInclVatField;
import static uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions.createAssessedTotalVatField;
import static uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions.createCounselCostField;
import static uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions.createDisbursementCostField;
import static uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions.createDisbursementVatCostField;
import static uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions.createNetProfitCostField;

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
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.OutcomeType;
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

    @Test
    void mapToAssessedClaimShouldCopyAllAssessedValues() {
        ClaimResponseV2 apiResponse = new ClaimResponseV2();
        BulkUploadCivilClaim row = buildRow();

        CivilClaimDetails blank = buildEmptyDetails();
        when(claimMapper.mapToCivilClaimDetails(apiResponse)).thenReturn(blank);

        // Act
        ClaimDetails result = helper.mapToAssessedClaim(apiResponse, row);

        // Assert
        CivilClaimDetails mapped = (CivilClaimDetails) result;

        // --- currency fields ---
        assertEquals(new BigDecimal("10.00"), mapped.getNetProfitCost().getAssessed());
        assertEquals(new BigDecimal("3.00"), mapped.getDisbursementVatAmount().getAssessed());
        assertEquals(new BigDecimal("20.50"), mapped.getNetDisbursementAmount().getAssessed());
        assertEquals(new BigDecimal("5.10"), mapped.getCounselsCost().getAssessed());

        // --- VAT totals ---
        assertEquals(new BigDecimal("7.77"), mapped.getAllowedTotalVat().getAssessed());
        assertEquals(new BigDecimal("7.77"), mapped.getAssessedTotalVat().getAssessed());

        // --- Incl VAT totals ---
        assertEquals(new BigDecimal("123.45"), mapped.getAllowedTotalInclVat().getAssessed());
        assertEquals(new BigDecimal("123.45"), mapped.getAssessedTotalInclVat().getAssessed());

        // --- assessment outcome ---
        assertEquals(OutcomeType.fromCsvLabel("Reduced"), mapped.getAssessmentOutcome());

        // --- verify mapper called correctly ---
        verify(claimMapper, times(1)).mapToCivilClaimDetails(apiResponse);
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

    private CivilClaimDetails buildEmptyDetails() {
        CivilClaimDetails d = new CivilClaimDetails();
        d.setNetProfitCost(createNetProfitCostField());
        d.setDisbursementVatAmount(createDisbursementVatCostField());
        d.setNetDisbursementAmount(createDisbursementCostField());
        d.setCounselsCost(createCounselCostField());
        d.setAllowedTotalVat(createAllowedTotalVatField());
        d.setAllowedTotalInclVat(createAllowedTotalInclVatField());
        d.setAssessedTotalInclVat(createAssessedTotalInclVatField());
        d.setAssessedTotalVat(createAssessedTotalVatField());
        return d;
    }

    private BulkUploadCivilClaim buildRow() {
        BulkUploadCivilClaim r = new BulkUploadCivilClaim();
        r.setProfitCost(new BigDecimal("10.00"));
        r.setDisbursements(new BigDecimal("20.50"));
        r.setDisbursementsVat(new BigDecimal("3.00"));
        r.setCounselCosts(new BigDecimal("5.10"));
        r.setTotalAllowedVat(new BigDecimal("7.77"));
        r.setTotalAllowedInclVat(new BigDecimal("123.45"));
        r.setAssessmentOutcome("Reduced");
        return r;
    }
}
