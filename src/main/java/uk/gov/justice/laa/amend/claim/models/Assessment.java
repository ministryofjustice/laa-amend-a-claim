package uk.gov.justice.laa.amend.claim.models;

import lombok.Data;

import java.io.Serializable;

@Data
public class Assessment implements Serializable {
    OutcomeType outcome;
}
