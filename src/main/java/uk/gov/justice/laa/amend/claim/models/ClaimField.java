package uk.gov.justice.laa.amend.claim.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClaimField implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String label;
    private ClaimFieldValue submitted;
    private ClaimFieldValue calculated;
    private ClaimFieldValue amended;
    private Cost cost;

    public ClaimField(String label, ClaimFieldValue submitted, ClaimFieldValue calculated) {
        this.label = label;
        this.submitted = submitted;
        this.calculated = calculated;
        this.amended = submitted;
        this.cost = null;
    }

    public ClaimField(String label, ClaimFieldValue submitted, ClaimFieldValue calculated, ClaimFieldValue amended) {
        this(label, submitted, calculated);
        this.amended = amended;
    }

    public ClaimField(String label, ClaimFieldValue submitted, ClaimFieldValue calculated, Cost cost) {
        this.label = label;
        this.submitted = submitted;
        this.calculated = calculated;
        this.amended = submitted;
        this.cost = cost;
    }
}
