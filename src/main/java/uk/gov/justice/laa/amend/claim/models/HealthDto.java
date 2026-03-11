package uk.gov.justice.laa.amend.claim.models;

import lombok.Data;
import org.springframework.boot.health.contributor.Status;

@Data
public class HealthDto {
    private Status status;
}
