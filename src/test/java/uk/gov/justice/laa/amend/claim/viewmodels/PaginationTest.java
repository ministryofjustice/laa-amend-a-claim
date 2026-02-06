package uk.gov.justice.laa.amend.claim.viewmodels;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static uk.gov.justice.laa.amend.claim.viewmodels.PaginationItem.PaginationItemType.ACTIVE_PAGE;
import static uk.gov.justice.laa.amend.claim.viewmodels.PaginationItem.PaginationItemType.ELLIPSIS;
import static uk.gov.justice.laa.amend.claim.viewmodels.PaginationItem.PaginationItemType.INACTIVE_PAGE;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Pagination results model tests")
class PaginationTest {

    @Test
    @DisplayName("Should handle empty results set")
    void shouldHandleEmptyResultsSet() {
        Pagination result = new Pagination(0, 3, 1, "/foo?page=1");

        assertThat(result.getItems().size()).isEqualTo(0);

        assertThat(result.getResults().getCount()).isEqualTo(0);
        assertThat(result.getResults().getFrom()).isEqualTo(0);
        assertThat(result.getResults().getTo()).isEqualTo(0);
        assertThat(result.getResults().getText()).isEqualTo("results");

        assertThat(result.getPrevious()).isNull();

        assertThat(result.getNext()).isNull();
    }

    @Test
    @DisplayName("Should handle one page of results")
    void shouldHandleOnePageOfResults() {
        Pagination result = new Pagination(3, 3, 1, "/foo?page=1");

        assertThat(result.getItems().size()).isEqualTo(0);

        assertThat(result.getResults().getCount()).isEqualTo(3);
        assertThat(result.getResults().getFrom()).isEqualTo(1);
        assertThat(result.getResults().getTo()).isEqualTo(3);
        assertThat(result.getResults().getText()).isEqualTo("results");

        assertThat(result.getPrevious()).isNull();

        assertThat(result.getNext()).isNull();
    }

    @Test
    @DisplayName("Should handle first page of two pages of results")
    void shouldHandleFirstPageOfTwoPagesOfResults() {
        Pagination result = new Pagination(6, 3, 1, "/foo?page=1");

        assertThat(result.getItems().size()).isEqualTo(2);

        assertThat(result.getItems().get(0).getText()).isEqualTo("1");
        assertThat(result.getItems().get(0).getHref()).isEqualTo("/foo?page=1");
        assertThat(result.getItems().get(0).getType()).isEqualTo(ACTIVE_PAGE);
        assertThat(result.getItems().get(0).getAriaLabel()).isEqualTo("Page 1 of 2");

        assertThat(result.getItems().get(1).getText()).isEqualTo("2");
        assertThat(result.getItems().get(1).getHref()).isEqualTo("/foo?page=2");
        assertThat(result.getItems().get(1).getType()).isEqualTo(INACTIVE_PAGE);
        assertThat(result.getItems().get(1).getAriaLabel()).isEqualTo("Page 2 of 2");

        assertThat(result.getResults().getCount()).isEqualTo(6);
        assertThat(result.getResults().getFrom()).isEqualTo(1);
        assertThat(result.getResults().getTo()).isEqualTo(3);
        assertThat(result.getResults().getText()).isEqualTo("results");

        assertThat(result.getPrevious()).isNull();

        assertThat(result.getNext().getText()).isEqualTo("Next");
        assertThat(result.getNext().getHref()).isEqualTo("/foo?page=2");
    }

    @Test
    @DisplayName("Should handle second page of two pages of results")
    void shouldHandleSecondPageOfTwoPagesOfResults() {
        Pagination result = new Pagination(6, 3, 2, "/foo?page=2");

        assertThat(result.getItems().size()).isEqualTo(2);

        assertThat(result.getItems().get(0).getText()).isEqualTo("1");
        assertThat(result.getItems().get(0).getHref()).isEqualTo("/foo?page=1");
        assertThat(result.getItems().get(0).getType()).isEqualTo(INACTIVE_PAGE);
        assertThat(result.getItems().get(0).getAriaLabel()).isEqualTo("Page 1 of 2");

        assertThat(result.getItems().get(1).getText()).isEqualTo("2");
        assertThat(result.getItems().get(1).getHref()).isEqualTo("/foo?page=2");
        assertThat(result.getItems().get(1).getType()).isEqualTo(ACTIVE_PAGE);
        assertThat(result.getItems().get(1).getAriaLabel()).isEqualTo("Page 2 of 2");

        assertThat(result.getResults().getCount()).isEqualTo(6);
        assertThat(result.getResults().getFrom()).isEqualTo(4);
        assertThat(result.getResults().getTo()).isEqualTo(6);
        assertThat(result.getResults().getText()).isEqualTo("results");

        assertThat(result.getPrevious().getText()).isEqualTo("Previous");
        assertThat(result.getPrevious().getHref()).isEqualTo("/foo?page=1");

        assertThat(result.getNext()).isNull();
    }

    @Test
    @DisplayName("Should handle second page of five pages of results")
    void shouldHandleSecondPageOfFivePagesOfResults() {
        Pagination result = new Pagination(15, 3, 2, "/foo?page=2");

        assertThat(result.getItems().size()).isEqualTo(5);

        assertThat(result.getItems().get(0).getText()).isEqualTo("1");
        assertThat(result.getItems().get(0).getHref()).isEqualTo("/foo?page=1");
        assertThat(result.getItems().get(0).getType()).isEqualTo(INACTIVE_PAGE);
        assertThat(result.getItems().get(0).getAriaLabel()).isEqualTo("Page 1 of 5");

        assertThat(result.getItems().get(1).getText()).isEqualTo("2");
        assertThat(result.getItems().get(1).getHref()).isEqualTo("/foo?page=2");
        assertThat(result.getItems().get(1).getType()).isEqualTo(ACTIVE_PAGE);
        assertThat(result.getItems().get(1).getAriaLabel()).isEqualTo("Page 2 of 5");

        assertThat(result.getItems().get(2).getText()).isEqualTo("3");
        assertThat(result.getItems().get(2).getHref()).isEqualTo("/foo?page=3");
        assertThat(result.getItems().get(2).getType()).isEqualTo(INACTIVE_PAGE);
        assertThat(result.getItems().get(2).getAriaLabel()).isEqualTo("Page 3 of 5");

        assertThat(result.getItems().get(3).getText()).isEqualTo("…");
        assertThat(result.getItems().get(3).getHref()).isNull();
        assertThat(result.getItems().get(3).getType()).isEqualTo(ELLIPSIS);
        assertThat(result.getItems().get(3).getAriaLabel()).isNull();

        assertThat(result.getItems().get(4).getText()).isEqualTo("5");
        assertThat(result.getItems().get(4).getHref()).isEqualTo("/foo?page=5");
        assertThat(result.getItems().get(4).getType()).isEqualTo(INACTIVE_PAGE);
        assertThat(result.getItems().get(4).getAriaLabel()).isEqualTo("Page 5 of 5");

        assertThat(result.getResults().getCount()).isEqualTo(15);
        assertThat(result.getResults().getFrom()).isEqualTo(4);
        assertThat(result.getResults().getTo()).isEqualTo(6);
        assertThat(result.getResults().getText()).isEqualTo("results");

        assertThat(result.getPrevious().getText()).isEqualTo("Previous");
        assertThat(result.getPrevious().getHref()).isEqualTo("/foo?page=1");

        assertThat(result.getNext().getText()).isEqualTo("Next");
        assertThat(result.getNext().getHref()).isEqualTo("/foo?page=3");
    }

    @Test
    @DisplayName("Should handle fifth page of nine pages of results")
    void shouldHandleFifthPageOfNinePagesOfResults() {
        Pagination result = new Pagination(27, 3, 5, "/foo?page=5");

        assertThat(result.getItems().size()).isEqualTo(7);

        assertThat(result.getItems().get(0).getText()).isEqualTo("1");
        assertThat(result.getItems().get(0).getHref()).isEqualTo("/foo?page=1");
        assertThat(result.getItems().get(0).getType()).isEqualTo(INACTIVE_PAGE);
        assertThat(result.getItems().get(0).getAriaLabel()).isEqualTo("Page 1 of 9");

        assertThat(result.getItems().get(1).getText()).isEqualTo("…");
        assertThat(result.getItems().get(1).getHref()).isNull();
        assertThat(result.getItems().get(1).getType()).isEqualTo(ELLIPSIS);
        assertThat(result.getItems().get(1).getAriaLabel()).isNull();

        assertThat(result.getItems().get(2).getText()).isEqualTo("4");
        assertThat(result.getItems().get(2).getHref()).isEqualTo("/foo?page=4");
        assertThat(result.getItems().get(2).getType()).isEqualTo(INACTIVE_PAGE);
        assertThat(result.getItems().get(2).getAriaLabel()).isEqualTo("Page 4 of 9");

        assertThat(result.getItems().get(3).getText()).isEqualTo("5");
        assertThat(result.getItems().get(3).getHref()).isEqualTo("/foo?page=5");
        assertThat(result.getItems().get(3).getType()).isEqualTo(ACTIVE_PAGE);
        assertThat(result.getItems().get(3).getAriaLabel()).isEqualTo("Page 5 of 9");

        assertThat(result.getItems().get(4).getText()).isEqualTo("6");
        assertThat(result.getItems().get(4).getHref()).isEqualTo("/foo?page=6");
        assertThat(result.getItems().get(4).getType()).isEqualTo(INACTIVE_PAGE);
        assertThat(result.getItems().get(4).getAriaLabel()).isEqualTo("Page 6 of 9");

        assertThat(result.getItems().get(5).getText()).isEqualTo("…");
        assertThat(result.getItems().get(5).getHref()).isNull();
        assertThat(result.getItems().get(5).getType()).isEqualTo(ELLIPSIS);
        assertThat(result.getItems().get(5).getAriaLabel()).isNull();

        assertThat(result.getItems().get(6).getText()).isEqualTo("9");
        assertThat(result.getItems().get(6).getHref()).isEqualTo("/foo?page=9");
        assertThat(result.getItems().get(6).getType()).isEqualTo(INACTIVE_PAGE);
        assertThat(result.getItems().get(6).getAriaLabel()).isEqualTo("Page 9 of 9");

        assertThat(result.getResults().getCount()).isEqualTo(27);
        assertThat(result.getResults().getFrom()).isEqualTo(13);
        assertThat(result.getResults().getTo()).isEqualTo(15);
        assertThat(result.getResults().getText()).isEqualTo("results");

        assertThat(result.getPrevious().getText()).isEqualTo("Previous");
        assertThat(result.getPrevious().getHref()).isEqualTo("/foo?page=4");

        assertThat(result.getNext().getText()).isEqualTo("Next");
        assertThat(result.getNext().getHref()).isEqualTo("/foo?page=6");
    }

    @Test
    void shouldHandleHrefWithNoPageQueryParameter() {
        Pagination result = new Pagination(6, 3, 1, "/foo");

        assertThat(result.getNext().getHref()).isEqualTo("/foo?page=2");
    }

    @Test
    void shouldHandleHrefWithOtherQueryParameter() {
        Pagination result = new Pagination(6, 3, 1, "/foo?key=value");

        assertThat(result.getNext().getHref()).isEqualTo("/foo?key=value&page=2");
    }
}
