package uk.gov.justice.laa.amend.claim.forms.validators;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.UNIQUE_FILE_NUMBER_CHARACTER_REGEX;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.UNIQUE_FILE_NUMBER_FORMAT_REGEX;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import uk.gov.justice.laa.amend.claim.forms.SearchForm;
import uk.gov.justice.laa.amend.claim.forms.annotations.ValidUniqueFileNumber;

public class UniqueFileNumberValidator extends Validator
        implements ConstraintValidator<ValidUniqueFileNumber, SearchForm> {

    private static final String FIELD = "uniqueFileNumber";

    @Override
    public boolean isValid(SearchForm form, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();

        if (!isBlank(form.getUniqueFileNumber())) {
            if (!form.getUniqueFileNumber().matches(UNIQUE_FILE_NUMBER_FORMAT_REGEX)) {
                addViolation(context, FIELD, "{index.uniqueFileNumber.error.format}");
                return false;
            }

            if (!form.getUniqueFileNumber().matches(UNIQUE_FILE_NUMBER_CHARACTER_REGEX)) {
                addViolation(context, FIELD, "{index.uniqueFileNumber.error.invalid}");
                return false;
            }
        }

        return true;
    }
}
