package uk.gov.justice.laa.amend.claim.viewmodels;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@AllArgsConstructor
@Data
public class ClaimFieldRow implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String label;
    private Object submitted;
    private Object calculated;
    private Object amended;
}