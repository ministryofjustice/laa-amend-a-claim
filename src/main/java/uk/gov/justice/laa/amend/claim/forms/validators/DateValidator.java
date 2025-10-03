package uk.gov.justice.laa.amend.claim.forms.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import uk.gov.justice.laa.amend.claim.forms.SearchForm;
import uk.gov.justice.laa.amend.claim.forms.annotations.ValidSubmissionDate;

import java.time.YearMonth;

import static uk.gov.justice.laa.amend.claim.forms.helpers.StringUtils.isEmpty;

public class DateValidator implements ConstraintValidator<ValidSubmissionDate, SearchForm> {

    @Override
    public boolean isValid(SearchForm form, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();

        if (isEmpty(form.getSubmissionDateMonth()) && isEmpty(form.getSubmissionDateYear())) {
            return true;
        }

        if (isEmpty(form.getSubmissionDateMonth())) {
            context.buildConstraintViolationWithTemplate("{index.submissionDate.month.error.required}")
                .addPropertyNode("submissionDateMonth")
                .addConstraintViolation();
            return false;
        }

        if (isEmpty(form.getSubmissionDateYear())) {
            context.buildConstraintViolationWithTemplate("{index.submissionDate.year.error.required}")
                .addPropertyNode("submissionDateYear")
                .addConstraintViolation();
            return false;
        }

        Integer month;
        Integer year;

        try {
            month = Integer.parseInt(form.getSubmissionDateMonth());
        } catch (NumberFormatException e) {
            month = null;
            context.buildConstraintViolationWithTemplate("{index.submissionDate.month.error.invalid}")
                .addPropertyNode("submissionDateMonth")
                .addConstraintViolation();
        }

        try {
            year = Integer.parseInt(form.getSubmissionDateYear());
        } catch (NumberFormatException e) {
            year = null;
            context.buildConstraintViolationWithTemplate("{index.submissionDate.year.error.invalid}")
                .addPropertyNode("submissionDateYear")
                .addConstraintViolation();
        }

        if (month == null || year == null) return false;

        try {
            YearMonth.of(year, month);
            return true;
        } catch (Exception e) {
            if (month < 1 || month > 12) {
                context.buildConstraintViolationWithTemplate("{index.submissionDate.month.error.range}")
                    .addPropertyNode("submissionDateMonth")
                    .addConstraintViolation();
            }
            return false;
        }
    }
}
