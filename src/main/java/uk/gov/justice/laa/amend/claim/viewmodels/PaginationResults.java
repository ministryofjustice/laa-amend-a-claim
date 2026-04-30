package uk.gov.justice.laa.amend.claim.viewmodels;

import lombok.Getter;

@Getter
public class PaginationResults {
  private int count;
  private int from;
  private int to;
  private String text;

  public PaginationResults(
      int totalNumberOfResults,
      int numberOfResultsPerPage,
      int currentPage,
      int totalNumberOfPages) {
    if (totalNumberOfResults == 0) {
      this.from = 0;
      this.to = 0;
    } else {
      int from = (currentPage - 1) * numberOfResultsPerPage + 1;

      int numberOfResultsOnThisPage =
          (currentPage < totalNumberOfPages)
              ? numberOfResultsPerPage
              : (totalNumberOfResults % numberOfResultsPerPage == 0
                  ? numberOfResultsPerPage
                  : totalNumberOfResults % numberOfResultsPerPage);

      this.from = from;
      this.to = from + numberOfResultsOnThisPage - 1;
    }

    this.count = totalNumberOfResults;
    this.text = totalNumberOfResults == 1 ? "result" : "results";
  }

  public boolean hasOne() {
    return count == 1;
  }
}
