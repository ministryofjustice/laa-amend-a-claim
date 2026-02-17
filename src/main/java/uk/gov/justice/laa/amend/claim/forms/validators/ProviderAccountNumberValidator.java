package uk.gov.justice.laa.amend.claim.forms.validators;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.PROVIDER_ACCOUNT_NUMBER_REGEX;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import uk.gov.justice.laa.amend.claim.forms.SearchForm;
import uk.gov.justice.laa.amend.claim.forms.annotations.ValidProviderAccountNumber;

public class ProviderAccountNumberValidator extends Validator
        implements ConstraintValidator<ValidProviderAccountNumber, SearchForm> {

    private static final String FIELD = "providerAccountNumber";

    @Override
    public boolean isValid(SearchForm form, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();

        if (isBlank(form.getProviderAccountNumber())) {
            addViolation(context, FIELD, "{index.providerAccountNumber.error.required}");
            return false;
        }

        if (form.getProviderAccountNumber().length() != 6) {
            addViolation(context, FIELD, "{index.providerAccountNumber.error.format}");
            return false;
        }

        if (!form.getProviderAccountNumber().matches(PROVIDER_ACCOUNT_NUMBER_REGEX)) {
            addViolation(context, FIELD, "{index.providerAccountNumber.error.invalid}");
            return false;
        }

        return true;
    }
}
