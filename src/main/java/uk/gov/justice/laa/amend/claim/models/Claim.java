package uk.gov.justice.laa.amend.claim.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResponse;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimStatus;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
public class Claim {
    private String uniqueFileNumber;
    private String caseReferenceNumber;
    private String clientSurname;
    private LocalDate dateSubmitted;
    private String account;
    private String type;
    private String status;

    public Claim(ClaimResponse response) {
        this.uniqueFileNumber = response.getUniqueFileNumber();
        this.caseReferenceNumber = response.getCaseReferenceNumber();
        this.clientSurname = response.getClientSurname();
        this.dateSubmitted = response.getCaseStartDate() != null ? LocalDate.parse(response.getCaseStartDate()) : null;
        this.account = response.getScheduleReference() != null ? response.getScheduleReference().split("/")[0] : null;
        this.type = ""; // TODO - response doesn't contain this yet
        this.status = ""; // TODO - response doesn't contain this yet
    }

    public String getDateSubmittedForDisplay() {
        return dateSubmitted != null ? dateSubmitted.format(DateTimeFormatter.ofPattern("dd MMM yyyy")) : "";
    }

    public Long getDateSubmittedForSorting() {
        return dateSubmitted != null ? dateSubmitted.toEpochDay() : 0;
    }
}
