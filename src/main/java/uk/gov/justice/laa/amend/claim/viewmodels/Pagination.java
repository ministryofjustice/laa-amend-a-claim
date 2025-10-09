package uk.gov.justice.laa.amend.claim.viewmodels;

import jakarta.annotation.Nullable;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/** View model for handling pagination. */
@Getter
public class Pagination {
    List<PaginationItem> items;
    PaginationResults results;
    @Nullable PaginationLink previous;
    @Nullable PaginationLink next;

    public Pagination(
        int totalNumberOfResults,
        int numberOfResultsPerPage,
        int currentPage,
        String href
    ) {
        int totalNumberOfPages = getTotalNumberOfPages(totalNumberOfResults, numberOfResultsPerPage);

        if (currentPage > 1) {
            this.previous = new PaginationLink("Previous", buildHref(href, currentPage - 1));
        }

        if (currentPage < totalNumberOfPages) {
            this.next = new PaginationLink("Next", buildHref(href, currentPage + 1));
        }

        this.items = buildPaginationItems(currentPage, totalNumberOfPages, href);

        this.results =
            new PaginationResults(
                totalNumberOfResults, numberOfResultsPerPage, currentPage, totalNumberOfPages);
    }

    private int getTotalNumberOfPages(int totalNumberOfResults, int numberOfResultsPerPage) {
        return (int) Math.max(1, Math.ceil((double) totalNumberOfResults / numberOfResultsPerPage));
    }

    private List<PaginationItem> buildPaginationItems(
        int currentPage, int totalNumberOfPages, String href) {
        List<PaginationItem> paginationItems = new ArrayList<>();
        if (totalNumberOfPages == 1) {
            return paginationItems;
        } else {
            boolean isLastItemEllipsis = false;
            for (int page = 1; page <= totalNumberOfPages; page++) {
                if (page == 1 || Math.abs(page - currentPage) <= 1 || page == totalNumberOfPages) {
                    PaginationItem paginationItem =
                        new PaginationItem(
                            page, totalNumberOfPages, buildHref(href, page), page == currentPage);
                    paginationItems.add(paginationItem);
                    isLastItemEllipsis = false;
                } else if (!isLastItemEllipsis) {
                    PaginationItem paginationItem = new PaginationItem();
                    paginationItems.add(paginationItem);
                    isLastItemEllipsis = true;
                }
            }
        }
        return paginationItems;
    }

    private String buildHref(String href, int page) {
        if (href.matches(".*[?&]page=\\d+.*")) {
            return href.replaceAll("([?&]page=)\\d+", "$1" + page);
        } else if (href.contains("?")) {
            return href + "&page=" + page;
        } else {
            return href + "?page=" + page;
        }
    }
}
