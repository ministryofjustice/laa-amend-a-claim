package uk.gov.justice.laa.amend.claim.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoidClaim201Response {

    @JsonProperty("id")
    private UUID id;
}
