package uk.gov.justice.laa.amend.claim.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
public class Claim {
    private String uniqueFileNumber;
    private String clientReferenceNumber;
    private String clientSurname;
    private LocalDate dateSubmitted;
    private String account;
    private String type; // TODO - create enum?
    private String status; // TODO - create enum?

    public String getDateSubmitted() {
        return dateSubmitted.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
    }
}
