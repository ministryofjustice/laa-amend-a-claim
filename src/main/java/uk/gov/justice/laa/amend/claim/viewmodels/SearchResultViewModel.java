package uk.gov.justice.laa.amend.claim.viewmodels;

import lombok.Data;
import uk.gov.justice.laa.amend.claim.models.Claim;

import java.util.List;

@Data
public class SearchResultViewModel {
    private List<Claim> claims;
    private Pagination pagination;
}
