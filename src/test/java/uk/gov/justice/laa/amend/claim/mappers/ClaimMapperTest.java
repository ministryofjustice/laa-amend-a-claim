package uk.gov.justice.laa.amend.claim.mappers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.justice.laa.amend.claim.models.CivilClaim;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.models.CrimeClaim;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.BoltOnPatch;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResponse;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.FeeCalculationPatch;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.TOTAL;

class ClaimMapperTest {

    private ClaimMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(ClaimMapper.class);
    }

    @Test
    void testMapMatterTypeCodeOneAndTwo() {
        ClaimResponse response = new ClaimResponse();
        response.setMatterTypeCode("ABC+DEF:GHI");

        assertEquals("ABC", mapper.mapMatterTypeCodeOne(response));
        assertEquals("DEF", mapper.mapMatterTypeCodeTwo(response));
    }

    @Test
    void testMapTotalAmount() {
        ClaimResponse response = new ClaimResponse();
        response.setTotalValue(BigDecimal.valueOf(100));
        FeeCalculationPatch feeCalc = new FeeCalculationPatch();
        feeCalc.setTotalAmount(BigDecimal.valueOf(120));
        response.setFeeCalculationResponse(feeCalc);

        ClaimField claimField = mapper.mapTotalAmount(response);
        assertEquals(TOTAL, claimField.getLabel());
        assertEquals(BigDecimal.valueOf(100), claimField.getSubmitted());
        assertEquals(BigDecimal.valueOf(120), claimField.getCalculated());
    }

    @Test
    void testMapToCivilClaimSummary() {
        ClaimResponse response = new ClaimResponse();
        response.setUniqueFileNumber("UFN123");
        response.setCaseReferenceNumber("CASE456");
        response.setClientSurname("Doe");
        response.setClientForename("John");
        response.setCaseStartDate("2025-01-01");
        response.setCaseConcludedDate("2025-02-01");

        FeeCalculationPatch feeCalc = new FeeCalculationPatch();
        feeCalc.setFeeCodeDescription("FeeSchemeX");
        feeCalc.setCategoryOfLaw("Civil");
        BoltOnPatch boltOn = new BoltOnPatch();
        boltOn.setEscapeCaseFlag(true);
        feeCalc.setBoltOnDetails(boltOn);
        response.setFeeCalculationResponse(feeCalc);

        response.setMatterTypeCode("MT1+MT2");

        CivilClaim claim = mapper.mapToCivilClaim(response);

        assertEquals("UFN123", claim.getUniqueFileNumber());
        assertEquals("CASE456", claim.getCaseReferenceNumber());
        assertEquals("Doe", claim.getClientSurname());
        assertEquals("John", claim.getClientForename());
        assertEquals("FeeSchemeX", claim.getFeeScheme());
        assertEquals("Civil", claim.getCategoryOfLaw());
        assertTrue(claim.getEscaped());
        assertEquals("MT1", claim.getMatterTypeCodeOne());
        assertEquals("MT2", claim.getMatterTypeCodeTwo());
    }

    @Test
    void testMapToCrimeClaimSummary() {
        ClaimResponse response = new ClaimResponse();
        response.setUniqueFileNumber("UFN123");
        response.setCaseReferenceNumber("CASE456");
        response.setClientSurname("Doe");
        response.setClientForename("John");
        response.setCaseStartDate("2025-01-01");
        response.setCaseConcludedDate("2025-02-01");

        FeeCalculationPatch feeCalc = new FeeCalculationPatch();
        feeCalc.setFeeCodeDescription("FeeSchemeX");
        feeCalc.setCategoryOfLaw("Civil");
        BoltOnPatch boltOn = new BoltOnPatch();
        boltOn.setEscapeCaseFlag(true);
        feeCalc.setBoltOnDetails(boltOn);
        response.setFeeCalculationResponse(feeCalc);

        response.setCrimeMatterTypeCode("CRIME123");
        CrimeClaim claim = mapper.mapToCrimeClaim(response);

        assertEquals("UFN123", claim.getUniqueFileNumber());
        assertEquals("CASE456", claim.getCaseReferenceNumber());
        assertEquals("Doe", claim.getClientSurname());
        assertEquals("John", claim.getClientForename());
        assertEquals("FeeSchemeX", claim.getFeeScheme());
        assertEquals("Civil", claim.getCategoryOfLaw());
        assertTrue(claim.getEscaped());
        assertEquals("CRIME123", claim.getMatterTypeCode());
    }
}
