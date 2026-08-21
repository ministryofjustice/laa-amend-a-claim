package uk.gov.justice.laa.amend.claim.bulkupload.civil;

import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.amend.claim.bulkupload.CsvField;
import uk.gov.justice.laa.amend.claim.bulkupload.CsvSchema;
import uk.gov.justice.laa.amend.claim.bulkupload.CsvSchemaProvider;

@Component
@RequiredArgsConstructor
public class CivilClaimSchemaProvider implements CsvSchemaProvider<BulkUploadCivilClaim> {
  public static final String OFFICE_CODE = "Office Code";
  public static final String UFN = "UFN";
  public static final String ASSESSMENT_OUTCOME = "Assessment Outcome";
  public static final String PROFIT_COST = "Profit Cost";
  public static final String DISBURSEMENTS = "Disbursements";
  public static final String DISBURSEMENTS_VAT = "Disbursements VAT";
  public static final String COUNSEL_COSTS = "Counsel Costs";
  public static final String TOTAL_ALLOWED_VAT = "Total Allowed VAT";
  public static final String TOTAL_ALLOWED_INCLUDING_VAT = "Total Allowed Including VAT";

  @Override
  public CsvSchema getSchema() {
    return new CsvSchema(
        List.of(
            new CsvField("officeCode", OFFICE_CODE, true, String.class),
            new CsvField("ufn", UFN, true, String.class),
            new CsvField("assessmentOutcome", ASSESSMENT_OUTCOME, true, String.class),
            new CsvField("profitCost", PROFIT_COST, true, BigDecimal.class),
            new CsvField("disbursements", DISBURSEMENTS, true, BigDecimal.class),
            new CsvField("disbursementsVat", DISBURSEMENTS_VAT, true, BigDecimal.class),
            new CsvField("counselCosts", COUNSEL_COSTS, true, BigDecimal.class),
            new CsvField("totalAllowedVat", TOTAL_ALLOWED_VAT, true, BigDecimal.class),
            new CsvField(
                "totalAllowedInclVat", TOTAL_ALLOWED_INCLUDING_VAT, true, BigDecimal.class)));
  }
}
