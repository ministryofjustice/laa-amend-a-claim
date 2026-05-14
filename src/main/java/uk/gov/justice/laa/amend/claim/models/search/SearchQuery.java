package uk.gov.justice.laa.amend.claim.models.search;

import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.DEFAULT_PAGE_SIZE;

import jakarta.validation.constraints.Min;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.justice.laa.amend.claim.forms.SearchForm;
import uk.gov.justice.laa.amend.claim.models.AreaOfLaw;
import uk.gov.justice.laa.amend.claim.models.PageQuery;
import uk.gov.justice.laa.amend.claim.models.sorting.SortDirection;

@Builder
@Getter
public class SearchQuery implements PageQuery<SearchSortField, SearchSort> {

  private static final int DEFAULT_PAGE = 1;

  @Min(DEFAULT_PAGE)
  private Integer page;

  private SearchSort sort;

  private String officeCode;
  private String submissionDateMonth;
  private String submissionDateYear;
  private String uniqueFileNumber;
  private String caseReferenceNumber;
  private AreaOfLaw areaOfLaw;
  private Boolean escapeCase;

  public SearchQuery(
      Integer page,
      SearchSort sort,
      String officeCode,
      String submissionDateMonth,
      String submissionDateYear,
      String uniqueFileNumber,
      String caseReferenceNumber,
      AreaOfLaw areaOfLaw,
      Boolean escapeCase) {
    this.page = Objects.requireNonNullElse(page, DEFAULT_PAGE);
    this.sort = Objects.requireNonNullElse(sort, SearchSort.defaults());

    this.officeCode = officeCode;
    this.submissionDateMonth = submissionDateMonth;
    this.submissionDateYear = submissionDateYear;
    this.uniqueFileNumber = uniqueFileNumber;
    this.caseReferenceNumber = caseReferenceNumber;
    this.areaOfLaw = areaOfLaw;
    this.escapeCase = escapeCase;
  }

  public Integer getSize() {
    return DEFAULT_PAGE_SIZE;
  }

  public static SearchQuery from(SearchForm form) {
    return SearchQuery.builder()
        .officeCode(form.getOfficeCode())
        .submissionDateMonth(form.getSubmissionDateMonth())
        .submissionDateYear(form.getSubmissionDateYear())
        .uniqueFileNumber(form.getUniqueFileNumber())
        .caseReferenceNumber(form.getCaseReferenceNumber())
        .areaOfLaw(form.getAreaOfLaw())
        .escapeCase(form.getEscapeCase())
        .build();
  }

  @Override
  public String getRedirectUrl(SearchSortField field, SortDirection direction) {
    return getRedirectUrl(
        DEFAULT_PAGE, SearchSort.builder().field(field).direction(direction).build());
  }

  @Override
  public String getRedirectUrl(int page, SearchSort sort) {
    UriComponentsBuilder builder = UriComponentsBuilder.fromPath("/");

    addQueryParam(builder, "officeCode", getOfficeCode());
    addQueryParam(builder, "submissionDateMonth", submissionDateMonth);
    addQueryParam(builder, "submissionDateYear", submissionDateYear);
    addQueryParam(builder, "uniqueFileNumber", uniqueFileNumber);
    addQueryParam(builder, "caseReferenceNumber", caseReferenceNumber);
    addQueryParam(builder, "areaOfLaw", areaOfLaw != null ? areaOfLaw.name() : null);
    addQueryParam(builder, "escapeCase", Objects.toString(escapeCase, null));

    addQueryParam(builder, "page", String.valueOf(page));
    addQueryParam(builder, "sort", Objects.toString(sort, null));

    return builder.build().toUriString();
  }
}
