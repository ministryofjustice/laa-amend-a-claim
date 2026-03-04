package uk.gov.justice.laa.amend.claim.client;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoidClaimRequest {
    private UUID createdByUserId;
    private String assessmentReason;
}
