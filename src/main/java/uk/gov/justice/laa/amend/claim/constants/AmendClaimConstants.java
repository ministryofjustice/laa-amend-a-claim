package uk.gov.justice.laa.amend.claim.constants;

import uk.gov.justice.laa.amend.claim.models.SortDirection;

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

    public static final String DEFAULT_DATE_FORMAT = "dd MMM yyyy";
    public static final String DEFAULT_PERIOD_FORMAT = "MMM yyyy";

    public static final String DEFAULT_SORT = "uniqueFileNumber,asc";
    public static final String DEFAULT_SORT_FIELD = "uniqueFileNumber";
    public static final SortDirection DEFAULT_SORT_ORDER = SortDirection.ASCENDING;
}
