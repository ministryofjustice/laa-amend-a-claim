package uk.gov.justice.laa.amend.claim.forms.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import uk.gov.justice.laa.amend.claim.forms.SearchForm;
import uk.gov.justice.laa.amend.claim.forms.annotations.ValidSubmissionDate;

import java.time.YearMonth;

import static org.apache.commons.lang3.StringUtils.isBlank;


public class DateValidator implements ConstraintValidator<ValidSubmissionDate, SearchForm> {

    @Override
    public boolean isValid(SearchForm form, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();

        if (isBlank(form.getSubmissionDateMonth()) && isBlank(form.getSubmissionDateYear())) {
            return true;
        }

        if (isBlank(form.getSubmissionDateMonth())) {
            addViolation(context, "submissionDateMonth", "{index.submissionDate.month.error.required}");
            return false;
        }

        if (isBlank(form.getSubmissionDateYear())) {
            addViolation(context, "submissionDateYear", "{index.submissionDate.year.error.required}");
            return false;
        }

        Integer month = parseInt(form.getSubmissionDateMonth());
        Integer year = parseInt(form.getSubmissionDateYear());

        if (month == null && year == null) {
            addViolation(context, "submissionDateMonth", "{index.submissionDate.error.invalid}");
            addViolation(context, "submissionDateYear", "{index.submissionDate.error.invalid}");
            return false;
        }

        if (month == null) {
            addViolation(context, "submissionDateMonth", "{index.submissionDate.month.error.invalid}");
            return false;
        }

        if (year == null) {
            addViolation(context, "submissionDateYear", "{index.submissionDate.year.error.invalid}");
            return false;
        }

        if (month < 1 || month > 12) {
            addViolation(context, "submissionDateMonth", "{index.submissionDate.month.error.range}");
            return false;
        }

        try {
            YearMonth.of(year, month);
            return true;
        } catch (Exception e) {
            addViolation(context, "submissionDateMonth", "{index.submissionDate.error.invalid}");
            addViolation(context, "submissionDateYear", "{index.submissionDate.error.invalid}");
            return false;
        }
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
}
