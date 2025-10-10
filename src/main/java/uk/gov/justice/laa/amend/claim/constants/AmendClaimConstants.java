package uk.gov.justice.laa.amend.claim.constants;

public class AmendClaimConstants {
    public static final String INDEX_PROVIDER_ACCOUNT_NUMBER_ERROR_REQUIRED = "{index.providerAccountNumber.error.required}";
    public static final String INDEX_PROVIDER_ACCOUNT_NUMBER_ERROR_INVALID = "{index.providerAccountNumber.error.invalid}";
    public static final String INDEX_REFERENCE_NUMBER_ERROR_INVALID = "{index.referenceNumber.error.invalid}";
    public static final String PROVIDER_ACCOUNT_REGEX = "^[a-zA-Z0-9]*$";
    public static final String REF_NUMBER_REGEX = "^[a-zA-Z0-9/.\\-\\s]*$";

    public static final int DEFAULT_PAGE_SIZE = 3;
    public static final int DEFAULT_PAGE_NUMBER = 0;
}
