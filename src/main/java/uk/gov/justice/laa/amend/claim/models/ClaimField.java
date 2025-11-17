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
    private Object submitted;
    private Object calculated;
    private Object amended;
    private Cost cost;

    public ClaimField(String label, Object submitted, Object calculated) {
        this.label = label;
        this.submitted = submitted;
        this.calculated = calculated;
        this.amended = submitted;
        this.cost = null;
    }

    public ClaimField(String label, Object submitted, Object calculated, Object amended) {
        this.label = label;
        this.submitted = submitted;
        this.calculated = calculated;
        this.amended = amended;
        this.cost = null;
    }

    public ClaimField(String label, Object submitted, Object calculated, Cost cost) {
        this.label = label;
        this.submitted = submitted;
        this.calculated = calculated;
        this.amended = submitted;
        this.cost = cost;
    }
}
