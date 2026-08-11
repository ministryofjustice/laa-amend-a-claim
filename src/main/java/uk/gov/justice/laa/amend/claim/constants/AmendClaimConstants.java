package uk.gov.justice.laa.amend.claim.constants;

import java.math.BigDecimal;
import java.util.Set;

public class AmendClaimConstants {

  // Office code is alphanumeric e.g. 1T702E
  public static final String OFFICE_CODE_REGEX = "^[a-zA-Z0-9]*$";
  // Unique file number contains 6 numbers, a forward slash, then 3 numbers e.g. 070722/001
  public static final String UNIQUE_FILE_NUMBER_FORMAT_REGEX = "^[^/]{6}/[^/]{3}$";
  public static final String UNIQUE_FILE_NUMBER_CHARACTER_REGEX = "^[0-9/]*$";
  // Case reference number is alphanumeric but can also include hyphens, spaces, full stops and
  // forward slashes e.g.
  // XX/4560/2018/43646802
  public static final String CASE_REFERENCE_NUMBER_REGEX = "^[a-zA-Z0-9/.\\-\\s]*$";

  public static final int DEFAULT_PAGE_SIZE = 10;
  public static final int DEFAULT_PAGE_NUMBER = 0;

  public static final String DEFAULT_DATE_FORMAT = "dd MMMM yyyy";
  public static final String DEFAULT_PERIOD_FORMAT = "MMM yyyy";
  public static final String DEFAULT_TIME_FORMAT = "h:mma";

  public static final String ASSESSMENT_REASON_ESCAPE_CASE = "Escape Fee Case Assessment";
  public static final String ASSESSMENT_REASON_ESCAPE_CASE_CONTINGENCY =
      "Escape Fee Case Assessment (Contingency)";
  public static final String ASSESSMENT_REASON_STAGE_DISBURSEMENT = "Stage Disbursement Assessment";
  public static final String ASSESSMENT_REASON_STAGE_DISBURSEMENT_CONTINGENCY =
      "Stage Disbursement Assessment (Contingency)";
  public static final String ASSESSMENT_REASON_VOID = "Void";

  public static final String ASSESSMENT_OUTCOME_REQUIRED_ERROR =
      "{assessmentOutcome.assessmentOutcomeRequiredError}";
  public static final String CONTINGENCY_ASSESSMENT_REQUIRED_ERROR =
      "{assessmentOutcome.contingencyAssessmentRequiredError}";

  public static class Label {
    public static final String FIXED_FEE = "FIXED_FEE";
    public static final String NET_PROFIT_COST = "PROFIT_COST";
    public static final String DISBURSEMENT_VAT = "DISBURSEMENTS_VAT";
    public static final String NET_DISBURSEMENTS_COST = "DISBURSEMENTS";
    public static final String COUNSELS_COST = "COUNSELS_COST";
    public static final String DETENTION_TRAVEL_COST = "DETENTION_TRAVEL";
    public static final String JR_FORM_FILLING = "JR_FORM_FILLING";
    public static final String ADJOURNED_FEE = "ADJOURNED_HEARING_FEE";
    public static final String CMRH_TELEPHONE = "CMRH_TELEPHONE";
    public static final String CMRH_ORAL = "CMRH_ORAL";
    public static final String HO_INTERVIEW = "HOME_OFFICE";
    public static final String SUBSTANTIVE_HEARING = "SUBSTANTIVE_HEARING";
    public static final String VAT = "VAT";
    public static final String TOTAL = "TOTAL";
    public static final String TRAVEL_COSTS = "TRAVEL_COSTS";
    public static final String WAITING_COSTS = "WAITING_COSTS";
    public static final String ASSESSED_TOTAL_VAT = "ASSESSED_TOTAL_VAT";
    public static final String ASSESSED_TOTAL_INCL_VAT = "ASSESSED_TOTAL_INCL_VAT";
    public static final String ALLOWED_TOTAL_VAT = "ALLOWED_TOTAL_VAT";
    public static final String ALLOWED_TOTAL_INCL_VAT = "ALLOWED_TOTAL_INCL_VAT";
    public static final String TRAVEL_AND_WAITING_COSTS = "TRAVEL_AND_WAITING_COSTS";
    public static final String IS_LONDON_RATE = "IS_LONDON_RATE";
    public static final String PRIOR_AUTHORITY_REFERENCE = "PRIOR_AUTHORITY_REFERENCE";
  }

  public static final String ASSESSMENT_ID = "assessmentId";

  public static final Set<String> STAGE_DISBURSEMENT_FEE_CODES =
      Set.of("MHLDIS", "EDUDIS", "ICASD", "ICISD", "ICSSD", "ILHSD");

  // We allow setting the assessed total values if it's a crime case and has a valid police station
  // fee code
  public static final Set<String> VALID_POLICE_STATION_FEE_CODES = Set.of("INVC");
  public static final BigDecimal MIN_CURRENCY = BigDecimal.ZERO;
  public static final BigDecimal MAX_CURRENCY = BigDecimal.valueOf(1_000_000);
}
