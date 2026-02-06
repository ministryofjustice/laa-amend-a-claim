package uk.gov.justice.laa.amend.claim.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.amend.claim.viewmodels.SearchResultView;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResultSet;

class ClaimResultMapperTest {

    @Test
    void givenEmptyClaimResultSet_whenToDtoIsCalled_thenEmptyViewModelIsReturned() {
        // Arrange
        ClaimResultSet claimResultSet = mock(ClaimResultSet.class);

        when(claimResultSet.getTotalElements()).thenReturn(0);
        when(claimResultSet.getSize()).thenReturn(10);
        when(claimResultSet.getNumber()).thenReturn(1);
        when(claimResultSet.getContent()).thenReturn(List.of());

        ClaimResultMapper mapper = new ClaimResultMapperImpl();
        ClaimMapper claimMapper = new ClaimMapperImpl();

        // Act
        SearchResultView resultViewModel = mapper.toDto(claimResultSet, "/", claimMapper);

        assertEquals(0, resultViewModel.getPagination().getResults().getCount());
        assertEquals(0, resultViewModel.getPagination().getItems().size());
        assertEquals(0, resultViewModel.getClaims().size());
    }
}
