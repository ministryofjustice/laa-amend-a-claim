package uk.gov.justice.laa.amend.claim.viewmodels;

import jakarta.annotation.Nullable;
import lombok.Getter;

/** View model for handling individual pagination items. */
@Getter
public class PaginationItem {
    private String text;

    @Nullable
    private String href;

    private PaginationItemType type;

    @Nullable
    private String ariaLabel;

    /** The type of pagination item. */
    public enum PaginationItemType {
        INACTIVE_PAGE,
        ACTIVE_PAGE,
        ELLIPSIS
    }

    /**
     * Constructor for an active or inactive pagination item.
     *
     * @param page The page number
     * @param totalNumberOfPages The total number of pages
     * @param href The href of the page (without query parameters)
     * @param selected Whether the page is the current selected page or not
     */
    public PaginationItem(int page, int totalNumberOfPages, String href, boolean selected) {
        this.text = String.valueOf(page);
        this.href = href;
        this.type = selected ? PaginationItemType.ACTIVE_PAGE : PaginationItemType.INACTIVE_PAGE;
        this.ariaLabel = String.format("Page %d of %d", page, totalNumberOfPages);
    }

    /** Constructor for an ellipsis pagination item. */
    public PaginationItem() {
        this.text = "…";
        this.type = PaginationItemType.ELLIPSIS;
    }
}
