package uk.gov.justice.laa.amend.claim.viewmodels;

import jakarta.annotation.Nullable;
import lombok.Getter;

@Getter
public class PaginationItem {
  private String text;

  @Nullable private String href;

  private PaginationItemType type;

  @Nullable private String ariaLabel;

  public enum PaginationItemType {
    INACTIVE_PAGE,
    ACTIVE_PAGE,
    ELLIPSIS
  }

  public PaginationItem(int page, int totalNumberOfPages, String href, boolean selected) {
    this.text = String.valueOf(page);
    this.href = href;
    this.type = selected ? PaginationItemType.ACTIVE_PAGE : PaginationItemType.INACTIVE_PAGE;
    this.ariaLabel = String.format("Page %d of %d", page, totalNumberOfPages);
  }

  public PaginationItem() {
    this.text = "…";
    this.type = PaginationItemType.ELLIPSIS;
  }
}
