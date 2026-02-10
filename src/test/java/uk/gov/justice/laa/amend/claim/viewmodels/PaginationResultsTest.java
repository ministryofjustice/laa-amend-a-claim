package uk.gov.justice.laa.amend.claim.viewmodels;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Pagination results model tests")
class PaginationResultsTest {

    @Test
    @DisplayName("Should handle 0 results")
    void shouldHandleZeroResults() {
        PaginationResults result = new PaginationResults(0, 3, 1, 0);

        assertThat(result.getCount()).isEqualTo(0);
        assertThat(result.getFrom()).isEqualTo(0);
        assertThat(result.getTo()).isEqualTo(0);
        assertThat(result.getText()).isEqualTo("results");
    }

    @Test
    @DisplayName("Should handle 1 result")
    void shouldHandleOneResult() {
        PaginationResults result = new PaginationResults(1, 3, 1, 1);

        assertThat(result.getCount()).isEqualTo(1);
        assertThat(result.getFrom()).isEqualTo(1);
        assertThat(result.getTo()).isEqualTo(1);
        assertThat(result.getText()).isEqualTo("result");
    }

    @Test
    @DisplayName("Should handle 2 results")
    void shouldHandleTwoResults() {
        PaginationResults result = new PaginationResults(2, 3, 1, 1);

        assertThat(result.getCount()).isEqualTo(2);
        assertThat(result.getFrom()).isEqualTo(1);
        assertThat(result.getTo()).isEqualTo(2);
        assertThat(result.getText()).isEqualTo("results");
    }

    @Test
    @DisplayName("Should handle full page of results")
    void shouldHandleFullPageOfResults() {
        PaginationResults result = new PaginationResults(3, 3, 1, 1);

        assertThat(result.getCount()).isEqualTo(3);
        assertThat(result.getFrom()).isEqualTo(1);
        assertThat(result.getTo()).isEqualTo(3);
        assertThat(result.getText()).isEqualTo("results");
    }

    @Test
    @DisplayName("Should handle second page of results")
    void shouldHandleSecondPageOfResults() {
        PaginationResults result = new PaginationResults(6, 3, 2, 2);

        assertThat(result.getCount()).isEqualTo(6);
        assertThat(result.getFrom()).isEqualTo(4);
        assertThat(result.getTo()).isEqualTo(6);
        assertThat(result.getText()).isEqualTo("results");
    }
}
