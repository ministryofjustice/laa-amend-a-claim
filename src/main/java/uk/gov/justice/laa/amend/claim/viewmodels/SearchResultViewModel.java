package uk.gov.justice.laa.amend.claim.viewmodels;

import lombok.Data;
import uk.gov.justice.laa.amend.claim.models.Claim;

import java.util.List;

@Data
public class SearchResultViewModel {
    private List<ClaimViewModel<? extends Claim>> claims;
    private Pagination pagination;
}
