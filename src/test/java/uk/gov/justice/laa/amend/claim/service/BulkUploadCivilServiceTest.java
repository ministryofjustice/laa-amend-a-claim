package uk.gov.justice.laa.amend.claim.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.amend.claim.models.BulkUploadResult.BulkUploadStatus.VALIDATION_FAILURE;
import static uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions.createAllowedTotalInclVatField;
import static uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions.createAllowedTotalVatField;
import static uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions.createAssessedTotalInclVatField;
import static uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions.createAssessedTotalVatField;
import static uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions.createCounselCostField;
import static uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions.createDisbursementCostField;
import static uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions.createDisbursementVatCostField;
import static uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions.createNetProfitCostField;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.amend.claim.bulkupload.BulkUploadError;
import uk.gov.justice.laa.amend.claim.bulkupload.BulkUploadHelper;
import uk.gov.justice.laa.amend.claim.bulkupload.CsvHeaderValidator;
import uk.gov.justice.laa.amend.claim.bulkupload.CsvRowMapper;
import uk.gov.justice.laa.amend.claim.bulkupload.CsvSchemaProvider;
import uk.gov.justice.laa.amend.claim.bulkupload.civil.BulkUploadCivilClaim;
import uk.gov.justice.laa.amend.claim.mappers.ClaimMapper;
import uk.gov.justice.laa.amend.claim.models.BulkUploadResult;
import uk.gov.justice.laa.amend.claim.models.BulkUploadValidationOutcome;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.OutcomeType;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResponseV2;

@ExtendWith(MockitoExtension.class)
class BulkUploadCivilServiceTest {

    public static final String OFFICE_CODE = "0G448S";
    public static final String UNIQUE_FILE_NUMBER = "131019/020";

    @Mock
    CsvSchemaProvider<BulkUploadCivilClaim> schemaProvider;

    @Mock
    CsvRowMapper<BulkUploadCivilClaim> rowMapper;

    @Mock
    CsvHeaderValidator csvHeaderValidator;

    @Mock
    BulkUploadHelper bulkUploadHelper;

    @Mock
    AssessmentService assessmentService;

    @Mock
    ClaimMapper claimMapper;

    @InjectMocks
    BulkUploadCivilService service;

    @Test
    void validateRowsShouldReturnSuccessWhenRowMatchesClaim() {
        // -------- arrange --------
        BulkUploadCivilClaim row = mock(BulkUploadCivilClaim.class);
        when(row.validate()).thenReturn(List.of()); // no row validation errors
        when(row.getOfficeCode()).thenReturn(OFFICE_CODE);
        when(row.getUfn()).thenReturn(UNIQUE_FILE_NUMBER);

        // API claim returned from helper
        ClaimResponseV2 claimResponse = mock(ClaimResponseV2.class);
        when(claimResponse.getOfficeCode()).thenReturn(OFFICE_CODE);
        when(claimResponse.getUniqueFileNumber()).thenReturn(UNIQUE_FILE_NUMBER);

        when(bulkUploadHelper.getAllClaims(anyList(), anyList())).thenReturn(List.of(claimResponse));

        // Mapper produces a CivilClaimDetails instance
        when(claimMapper.mapToCivilClaimDetails(any())).thenReturn(buildEmptyDetails());

        // -------- act --------
        BulkUploadValidationOutcome outcome = service.validateRows(List.of(row));

        // -------- assert --------
        assertNotNull(outcome);
        assertEquals(BulkUploadResult.BulkUploadStatus.SUCCESS, outcome.result().status());

        assertEquals(1, outcome.claimDetailsList().size());
    }

    @Test
    void mapAssessmentShouldPopulateAssessedValuesCorrectly() {
        ClaimResponseV2 apiResponse = new ClaimResponseV2();
        apiResponse.setOfficeCode(OFFICE_CODE);
        apiResponse.setUniqueFileNumber(UNIQUE_FILE_NUMBER);
        BulkUploadCivilClaim row = buildRow();

        CivilClaimDetails blank = buildEmptyDetails();
        when(claimMapper.mapToCivilClaimDetails(apiResponse)).thenReturn(blank);
        when(bulkUploadHelper.getAllClaims(anyList(), anyList())).thenReturn(List.of(apiResponse));
        // --- Act ---
        var result = service.validateRows(List.of(row)); // triggers mapping

        CivilClaimDetails mapped = (CivilClaimDetails) result.claimDetailsList().getFirst();

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

    @Test
    void validateRowsShouldErrorWhenMoreThanOneMatchingClaimReturned() {
        // --- arrange ---
        BulkUploadCivilClaim row = buildRow();
        ClaimResponseV2 claim1 = mock(ClaimResponseV2.class);
        ClaimResponseV2 claim2 = mock(ClaimResponseV2.class);

        when(claim1.getOfficeCode()).thenReturn(OFFICE_CODE);
        when(claim1.getUniqueFileNumber()).thenReturn(UNIQUE_FILE_NUMBER);

        when(claim2.getOfficeCode()).thenReturn(OFFICE_CODE);
        when(claim2.getUniqueFileNumber()).thenReturn(UNIQUE_FILE_NUMBER);

        // Return TWO claims → triggers the duplicate error
        when(bulkUploadHelper.getAllClaims(anyList(), anyList())).thenReturn(List.of(claim1, claim2));

        // --- act ---
        BulkUploadValidationOutcome outcome = service.validateRows(List.of(row));

        // --- assert ---
        assertEquals(VALIDATION_FAILURE, outcome.result().status());
        BulkUploadError error = outcome.result().errors().getFirst();
        assertTrue(error.message().contains("Duplicate Escaped Civil Claim"));
    }

    @Test
    void validateRowsShouldReturnErrorsSortedByRowNumber() {
        // --- arrange: two rows with invalid office codes, row 5 before row 2 in iteration ---
        BulkUploadCivilClaim row2 = mock(BulkUploadCivilClaim.class);
        BulkUploadCivilClaim row5 = mock(BulkUploadCivilClaim.class);

        when(row2.getRowNumber()).thenReturn(2);
        when(row2.getOfficeCode()).thenReturn(OFFICE_CODE);
        when(row2.getUfn()).thenReturn(UNIQUE_FILE_NUMBER);
        when(row2.validate()).thenReturn(List.of(new BulkUploadError(2, "error on row 2")));

        when(row5.getRowNumber()).thenReturn(5);
        when(row5.getOfficeCode()).thenReturn(OFFICE_CODE);
        when(row5.getUfn()).thenReturn("999999/999");
        when(row5.validate()).thenReturn(List.of(new BulkUploadError(5, "error on row 5")));

        when(bulkUploadHelper.getAllClaims(anyList(), anyList())).thenReturn(List.of());

        // --- act: pass row5 first, then row2 ---
        BulkUploadValidationOutcome outcome = service.validateRows(List.of(row5, row2));

        // --- assert: errors sorted ascending by row number ---
        assertEquals(VALIDATION_FAILURE, outcome.result().status());
        List<BulkUploadError> errors = outcome.result().errors();
        for (int i = 1; i < errors.size(); i++) {
            assertTrue(
                    errors.get(i - 1).rowNumber() <= errors.get(i).rowNumber(),
                    "Errors should be sorted by row number ascending");
        }
    }

    private CivilClaimDetails buildEmptyDetails() {
        CivilClaimDetails d = new CivilClaimDetails();
        d.setOfficeCode(OFFICE_CODE);
        d.setUniqueFileNumber(UNIQUE_FILE_NUMBER);
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
        r.setOfficeCode(OFFICE_CODE);
        r.setUfn(UNIQUE_FILE_NUMBER);
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
