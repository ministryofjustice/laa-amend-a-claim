package uk.gov.justice.laa.amend.claim.forms.validators;

import io.vavr.control.Either;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import uk.gov.justice.laa.amend.claim.forms.SearchForm;
import uk.gov.justice.laa.amend.claim.forms.annotations.ValidSubmissionDate;
import uk.gov.justice.laa.amend.claim.forms.errors.FieldError;
import uk.gov.justice.laa.amend.claim.forms.errors.FieldErrorType;

import java.time.YearMonth;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class DateValidator implements ConstraintValidator<ValidSubmissionDate, SearchForm> {

    @Override
    public boolean isValid(SearchForm form, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();

        Either<FieldError, Integer> month = validateMonth(form);
        Either<FieldError, Integer> year = validateYear(form);

        if (isRequired(month) && isRequired(year)) {
            return true;
        }

        if (isInvalid(month) && isInvalid(year)) {
            addViolations(context);
            return false;
        }

        if (isInvalid(month)) {
            addViolation(context, "submissionDateMonth", month.getLeft().getMessage());
        }

        if (isInvalid(year)) {
            addViolation(context, "submissionDateYear", year.getLeft().getMessage());
        }

        if (isValid(month) && isValid(year)) {
            try {
                YearMonth.of(year.get(), month.get());
                return true;
            } catch (Exception e) {
                addViolations(context);
            }
        }

        return false;
    }

    private Either<FieldError, Integer> validateMonth(SearchForm form) {
        if (isBlank(form.getSubmissionDateMonth())) {
            return Either.left(new FieldError(FieldErrorType.REQUIRED, "{index.submissionDate.month.error.required}"));
        }

        Integer month = parseInt(form.getSubmissionDateMonth());

        if (month == null) {
            return Either.left(new FieldError(FieldErrorType.INVALID, "{index.submissionDate.month.error.invalid}"));
        }

        if (month < 1 || month > 12) {
            return Either.left(new FieldError(FieldErrorType.INVALID, "{index.submissionDate.month.error.range}"));
        }

        return Either.right(month);
    }

    private Either<FieldError, Integer> validateYear(SearchForm form) {
        if (isBlank(form.getSubmissionDateYear())) {
            return Either.left(new FieldError(FieldErrorType.REQUIRED, "{index.submissionDate.year.error.required}"));
        }

        Integer year = parseInt(form.getSubmissionDateYear());

        if (year == null) {
            return Either.left(new FieldError(FieldErrorType.INVALID, "{index.submissionDate.year.error.invalid}"));
        }

        return Either.right(year);
    }

    private Integer parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void addViolation(ConstraintValidatorContext context, String fieldName, String message) {
        context.buildConstraintViolationWithTemplate(message)
            .addPropertyNode(fieldName)
            .addConstraintViolation();
    }

    private void addViolations(ConstraintValidatorContext context) {
        addViolation(context, "submissionDateMonth", "{index.submissionDate.error.invalid}");
        addViolation(context, "submissionDateYear", "{index.submissionDate.error.invalid}");
    }

    private boolean isRequired(Either<FieldError, Integer> value) {
        return isInvalid(value) && value.getLeft().getType() == FieldErrorType.REQUIRED;
    }

    private boolean isValid(Either<FieldError, Integer> value) {
        return value.isRight();
    }

    private boolean isInvalid(Either<FieldError, Integer> value) {
        return value.isLeft();
    }
}
