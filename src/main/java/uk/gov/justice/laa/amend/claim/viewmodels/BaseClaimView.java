package uk.gov.justice.laa.amend.claim.viewmodels;

import uk.gov.justice.laa.amend.claim.models.Claim;

import java.time.format.DateTimeFormatter;

import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.DEFAULT_PERIOD_FORMAT;
import static uk.gov.justice.laa.amend.claim.utils.DateUtils.displayDateValue;

public interface BaseClaimView<T extends Claim> {
    T claim();

    default T getClaim() {
        return claim();
    }

    default String getClientName() {
        String clientForename = claim().getClientForename();
        String clientSurname = claim().getClientSurname();
        if (clientForename != null & clientSurname != null) {
            return String.format("%s %s", clientForename, clientSurname);
        } else if (clientForename != null) {
            return clientForename;
        } else {
            return clientSurname;
        }
    }


    default String getAccountNumber() {
        return claim().getScheduleReference() != null ? claim().getScheduleReference().split("/")[0] : null;
    }

    default String getCaseStartDateForDisplay() {
        return displayDateValue(claim().getCaseStartDate());
    }

    default String getCaseEndDateForDisplay() {
        return displayDateValue(claim().getCaseEndDate());
    }

    default String getSubmissionPeriodForDisplay() {
        return claim().getSubmissionPeriod() != null ? claim().getSubmissionPeriod().format(DateTimeFormatter.ofPattern(DEFAULT_PERIOD_FORMAT)) : null;
    }

    default long getSubmissionPeriodForSorting() {
        return claim().getSubmissionPeriod() != null ? claim().getSubmissionPeriod().atDay(1).toEpochDay() : 0;
    }

}
