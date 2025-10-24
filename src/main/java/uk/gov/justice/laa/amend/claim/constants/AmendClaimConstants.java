package uk.gov.justice.laa.amend.claim.constants;

public class AmendClaimConstants {
    public static final String PROVIDER_ACCOUNT_NUMBER_REQUIRED_ERROR = "{index.providerAccountNumber.error.required}";
    public static final String PROVIDER_ACCOUNT_NUMBER_INVALID_ERROR = "{index.providerAccountNumber.error.invalid}";
    public static final String UNIQUE_FILE_NUMBER_INVALID_ERROR = "{index.uniqueFileNumber.error.invalid}";
    public static final String CASE_REFERENCE_NUMBER_INVALID_ERROR = "{index.caseReferenceNumber.error.invalid}";

    public static final String PROVIDER_ACCOUNT_NUMBER_REGEX = "^[a-zA-Z0-9]*$";
    public static final String UNIQUE_FILE_NUMBER_REGEX = "^[0-9/]*$";
    public static final String CASE_REFERENCE_NUMBER_REGEX = "^[a-zA-Z0-9/.\\-\\s]*$";

    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int DEFAULT_PAGE_NUMBER = 0;

    public static final String  DEFAULT_DATE_FORMAT = "dd MMM yyyy";
    public static final String  DEFAULT_PERIOD_FORMAT = "MMM yyyy";
}
