package uk.gov.justice.laa.amend.claim.bulkupload.civil;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BulkUploadCivilClaim {
    private String officeCode;
    private String ufn;
    private String assessmentOutcome;
    private BigDecimal profitCost;
    private BigDecimal disbursements;
    private BigDecimal disbursementsVat;
    private BigDecimal counselCosts;
    private BigDecimal totalAllowedVat;
    private BigDecimal totalAllowedInclVat;
}
