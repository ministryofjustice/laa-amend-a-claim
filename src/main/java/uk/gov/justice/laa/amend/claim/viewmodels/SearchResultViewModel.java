package uk.gov.justice.laa.amend.claim.viewmodels;

import lombok.Getter;
import uk.gov.justice.laa.amend.claim.models.Claim;

import java.time.LocalDate;
import java.util.List;

@Getter
public class SearchResultViewModel {
    List<Claim> claims;
    Pagination pagination;
    int searchResults;

    public SearchResultViewModel() {
        this.claims = List.of(
            new Claim(
                "01012025/123",
                "No data",
                "Doe",
                LocalDate.of(2025, 7, 25),
                "ABC123",
                "Family",
                "EC"
            ),
            new Claim(
                "1203022025/123",
                "No data",
                "White",
                LocalDate.of(2025, 7, 25),
                "ABC123",
                "Family",
                "EC"
            ),
            new Claim(
                "18042025/123",
                "No data",
                "Stevens",
                LocalDate.of(2025, 7, 25),
                "ABC123",
                "Family",
                "EC"
            )
        );

        this.searchResults = claims.size();

        this.pagination = new Pagination(3, 10, 1, "/");
    }
}
