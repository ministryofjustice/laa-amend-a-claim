package uk.gov.justice.laa.amend.claim.viewmodels;

import lombok.Getter;
import uk.gov.justice.laa.amend.claim.models.Claim;

import java.util.List;

@Getter
public class SearchResultViewModel {
    List<Claim> claims;
    int searchResults;
    Pagination pagination;

    public SearchResultViewModel(List<Claim> claims) {
        this.claims = claims;
        this.searchResults = claims.size();
        this.pagination = new Pagination(3, 10, 1, "/");
    }
}
