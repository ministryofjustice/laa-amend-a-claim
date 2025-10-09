package uk.gov.justice.laa.amend.claim.forms.errors;

import org.thymeleaf.spring6.util.DetailedError;

public class SearchFormError extends FormError<SearchFormError> {

    public SearchFormError(DetailedError error) {
        super(error);
    }

    public SearchFormError(String fieldName, String message) {
        super(fieldName, message);
    }

    @Override
    public int compareTo(SearchFormError other) {
        if (other == null) return 1;

        int thisOrder = getFieldOrder(this.getFieldName());
        int otherOrder = getFieldOrder(other.getFieldName());

        return Integer.compare(thisOrder, otherOrder);
    }

    private int getFieldOrder(String fieldName) {
        if (fieldName == null) return Integer.MAX_VALUE;

        return switch (fieldName) {
            case "providerAccountNumber" -> 1;
            case "submissionDateMonth" -> 2;
            case "submissionDateYear" -> 3;
            case "referenceNumber" -> 4;
            default -> Integer.MAX_VALUE;
        };
    }
}
