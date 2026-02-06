package uk.gov.justice.laa.amend.claim.viewmodels;

import java.util.List;
import lombok.Data;
import uk.gov.justice.laa.amend.claim.models.Claim;

@Data
public class SearchResultView {
    private List<BaseClaimView<Claim>> claims;
    private Pagination pagination;
}
