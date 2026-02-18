package uk.gov.justice.laa.amend.claim.forms.validators;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.CASE_REFERENCE_NUMBER_REGEX;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import uk.gov.justice.laa.amend.claim.forms.SearchForm;
import uk.gov.justice.laa.amend.claim.forms.annotations.ValidCaseReferenceNumber;

public class CaseReferenceNumberValidator extends Validator
        implements ConstraintValidator<ValidCaseReferenceNumber, SearchForm> {

    private static final String FIELD = "caseReferenceNumber";

    @Override
    public boolean isValid(SearchForm form, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();

        if (!isBlank(form.getCaseReferenceNumber())) {
            if (form.getCaseReferenceNumber().length() > 30) {
                addViolation(context, FIELD, "{index.caseReferenceNumber.error.format}");
                return false;
            }

            if (!form.getCaseReferenceNumber().matches(CASE_REFERENCE_NUMBER_REGEX)) {
                addViolation(context, FIELD, "{index.caseReferenceNumber.error.invalid}");
                return false;
            }
        }

        return true;
    }
}
