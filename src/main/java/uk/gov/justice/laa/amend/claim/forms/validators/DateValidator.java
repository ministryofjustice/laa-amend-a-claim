package uk.gov.justice.laa.amend.claim.forms.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import uk.gov.justice.laa.amend.claim.forms.SearchForm;
import uk.gov.justice.laa.amend.claim.forms.annotations.ValidSubmissionDate;

import java.time.YearMonth;

public class DateValidator implements ConstraintValidator<ValidSubmissionDate, SearchForm> {

    @Override
    public boolean isValid(SearchForm form, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();

        if (form.getSubmissionDateMonth() == null && form.getSubmissionDateYear() == null) {
            return true;
        }

        if (form.getSubmissionDateMonth() == null || form.getSubmissionDateMonth().isBlank()) {
            context.buildConstraintViolationWithTemplate("{index.submissionDate.month.error.required}")
                .addPropertyNode("submissionDateMonth")
                .addConstraintViolation();
            return false;
        }

        if (form.getSubmissionDateYear() == null || form.getSubmissionDateYear().isBlank()) {
            context.buildConstraintViolationWithTemplate("{index.submissionDate.year.error.required}")
                .addPropertyNode("submissionDateYear")
                .addConstraintViolation();
            return false;
        }

        int month;
        int year;

        try {
            month = Integer.parseInt(form.getSubmissionDateMonth());
        } catch (NumberFormatException e) {
            context.buildConstraintViolationWithTemplate("{index.submissionDate.error.invalid}")
                .addPropertyNode("submissionDateMonth")
                .addConstraintViolation();
            return false;
        }

        try {
            year = Integer.parseInt(form.getSubmissionDateYear());
        } catch (NumberFormatException e) {
            context.buildConstraintViolationWithTemplate("{index.submissionDate.error.invalid}")
                .addPropertyNode("submissionDateYear")
                .addConstraintViolation();
            return false;
        }

        try {
            YearMonth.of(year, month);
            return true;
        } catch (Exception e) {
            context.buildConstraintViolationWithTemplate("{index.submissionDate.error.invalid}")
                .addPropertyNode("submissionDateMonth")
                .addConstraintViolation();
            return false;
        }
    }
}
