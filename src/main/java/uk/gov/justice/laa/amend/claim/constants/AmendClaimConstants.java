package uk.gov.justice.laa.amend.claim.constants;

import uk.gov.justice.laa.amend.claim.models.SortDirection;

import java.util.Set;

public class AmendClaimConstants {
    public static final String PROVIDER_ACCOUNT_NUMBER_REQUIRED_ERROR = "{index.providerAccountNumber.error.required}";
    public static final String PROVIDER_ACCOUNT_NUMBER_INVALID_ERROR = "{index.providerAccountNumber.error.invalid}";
    public static final String UNIQUE_FILE_NUMBER_INVALID_ERROR = "{index.uniqueFileNumber.error.invalid}";
    public static final String CASE_REFERENCE_NUMBER_INVALID_ERROR = "{index.caseReferenceNumber.error.invalid}";

    // Provider account number is alphanumeric e.g. 1T702E
    public static final String PROVIDER_ACCOUNT_NUMBER_REGEX = "^[a-zA-Z0-9]*$";
    // Unique file number contains 6 numbers, a forward slash, then 3 numbers e.g. 070722/001
    public static final String UNIQUE_FILE_NUMBER_REGEX = "^[0-9/]*$";
    // Case reference number is alphanumeric but can also include hyphens, spaces, full stops and forward slashes e.g. XX/4560/2018/43646802
    public static final String CASE_REFERENCE_NUMBER_REGEX = "^[a-zA-Z0-9/.\\-\\s]*$";

    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int DEFAULT_PAGE_NUMBER = 0;

    public static final String DEFAULT_DATE_FORMAT = "dd MMMM yyyy";
    public static final String DEFAULT_PERIOD_FORMAT = "MMM yyyy";
    public static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";

    public static final String DEFAULT_SORT = "uniqueFileNumber,asc";
    public static final String DEFAULT_SORT_FIELD = "uniqueFileNumber";
    public static final SortDirection DEFAULT_SORT_ORDER = SortDirection.ASCENDING;

    public static final String ASSESSMENT_OUTCOME_REQUIRED_ERROR = "{assessmentOutcome.assessmentOutcomeRequiredError}";
    public static final String LIABILITY_FOR_VAT_REQUIRED_ERROR = "{assessmentOutcome.liabilityForVatRequiredError}";
    
    public static class Label {
        public static final String FIXED_FEE = "fixedFee";
        public static final String NET_PROFIT_COST = "profitCost";
        public static final String DISBURSEMENT_VAT = "disbursementsVat";
        public static final String NET_DISBURSEMENTS_COST = "disbursements";
        public static final String COUNSELS_COST = "counselsCost";
        public static final String DETENTION_TRAVEL_COST = "detentionTravel";
        public static final String JR_FORM_FILLING = "jrFormFilling";
        public static final String ADJOURNED_FEE = "adjournedHearingFee";
        public static final String CMRH_TELEPHONE = "cmrhTelephone";
        public static final String CMRH_ORAL = "cmrhOral";
        public static final String HO_INTERVIEW = "homeOffice";
        public static final String SUBSTANTIVE_HEARING = "substantiveHearing";
        public static final String VAT = "vat";
        public static final String TOTAL = "total";
        public static final String TRAVEL_COSTS = "travel";
        public static final String WAITING_COSTS = "waiting";
        public static final String ASSESSED_TOTAL_VAT = "assessedTotalVat";
        public static final String ASSESSED_TOTAL_INCL_VAT = "assessedTotalInclVat";
        public static final String ALLOWED_TOTAL_VAT = "allowedTotalVat";
        public static final String ALLOWED_TOTAL_INCL_VAT = "allowedTotalInclVat";
    }

    public static final String ASSESSMENT_ID = "assessmentId";

    // We allow setting the assessed total values if it's a crime case and has a valid police station fee code
    public static final Set<String> VALID_POLICE_STATION_FEE_CODES = Set.of("INVC");
}
