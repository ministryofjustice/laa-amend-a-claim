package uk.gov.justice.laa.amend.claim.mappers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import uk.gov.justice.laa.amend.claim.viewmodels.CivilClaimSummary;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimFieldRow;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimSummary;
import uk.gov.justice.laa.amend.claim.viewmodels.CrimeClaimSummary;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.BoltOnPatch;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResponse;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.FeeCalculationPatch;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label.TOTAL;


@SpringJUnitConfig
@ContextConfiguration(classes = {ClaimSummaryMapperImpl.class, ClaimMapperHelper.class})
class ClaimSummaryMapperTest {

    @Autowired
    private ClaimSummaryMapper mapper;

    @Test
    void testMapMatterTypeCodeOneAndTwo() {
        ClaimResponse response = new ClaimResponse();
        response.setMatterTypeCode("ABC+DEF:GHI");
        var res = mapper.mapToCivilClaimSummary(response);

        assertEquals("ABC", res.getMatterTypeCodeOne());
        assertEquals("DEF", res.getMatterTypeCodeTwo());
    }

    @Test
    void testMapTotalAmount() {
        ClaimResponse response = new ClaimResponse();
        response.setTotalValue(BigDecimal.valueOf(100));
        FeeCalculationPatch feeCalc = new FeeCalculationPatch();
        feeCalc.setTotalAmount(BigDecimal.valueOf(120));
        response.setFeeCalculationResponse(feeCalc);
        var res = mapper.mapBaseFields(response);

        ClaimFieldRow row = res.getTotalAmount();
        assertEquals(TOTAL, row.getLabel());
        assertEquals(BigDecimal.valueOf(100), row.getSubmitted());
        assertEquals(BigDecimal.valueOf(120), row.getCalculated());
    }

    @Test
    void testMapBaseFields() {
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

        ClaimSummary summary = mapper.mapBaseFields(response);

        assertEquals("UFN123", summary.getUniqueFileNumber());
        assertEquals("CASE456", summary.getCaseReferenceNumber());
        assertEquals("Doe", summary.getClientSurname());
        assertEquals("John", summary.getClientForename());
        assertEquals("FeeSchemeX", summary.getFeeScheme());
        assertEquals("Civil", summary.getCategoryOfLaw());
        assertTrue(summary.getEscaped());
    }

    @Test
    void testMapToCivilClaimSummary() {
        ClaimResponse response = new ClaimResponse();
        response.setMatterTypeCode("MT1+MT2");
        CivilClaimSummary civilSummary = mapper.mapToCivilClaimSummary(response);

        assertEquals("MT1", civilSummary.getMatterTypeCodeOne());
        assertEquals("MT2", civilSummary.getMatterTypeCodeTwo());
    }

    @Test
    void testMapToCrimeClaimSummary() {
        ClaimResponse response = new ClaimResponse();
        response.setCrimeMatterTypeCode("CRIME123");
        CrimeClaimSummary crimeSummary = mapper.mapToCrimeClaimSummary(response);

        assertEquals("CRIME123", crimeSummary.getMatterTypeCode());
    }
}
