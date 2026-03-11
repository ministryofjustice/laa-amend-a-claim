package uk.gov.justice.laa.amend.claim.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoidClaimRequest {

    @JsonProperty("created_by_user_id")
    private UUID createdByUserId;

    @JsonProperty("assessment_reason")
    private String assessmentReason;
}
