package uk.gov.justice.laa.amend.claim.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import uk.gov.justice.laa.amend.claim.client.ClaimsApiClient;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimInquestData;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimInquestDataWrite;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.InquestDepartmentReference;

@ExtendWith(MockitoExtension.class)
class InquestDataServiceTest {

  @Mock private ClaimsApiClient claimsApiClient;

  private InquestDataService inquestDataService;

  private UUID claimId;

  @BeforeEach
  void setUp() {
    claimId = UUID.randomUUID();
    inquestDataService = new InquestDataService(claimsApiClient);
  }

  @Nested
  class Get {

    @Test
    void returnsPopulatedOptionalWhenDataExists() {
      ClaimInquestData data = ClaimInquestData.builder().deceasedForename("John").build();
      when(claimsApiClient.getClaimInquestData(claimId)).thenReturn(Mono.just(data));

      Optional<ClaimInquestData> result = inquestDataService.get(claimId);

      assertThat(result).contains(data);
    }

    @Test
    void returnsEmptyOptionalOnNotFound() {
      when(claimsApiClient.getClaimInquestData(claimId))
          .thenReturn(
              Mono.error(WebClientResponseException.create(404, "Not Found", null, null, null)));

      Optional<ClaimInquestData> result = inquestDataService.get(claimId);

      assertThat(result).isEmpty();
    }
  }

  @Nested
  class Save {

    @Test
    void createsInquestDataWhenNoneExists() {
      ClaimInquestDataWrite request =
          ClaimInquestDataWrite.builder().deceasedForename("Jane").build();
      ClaimInquestData response = ClaimInquestData.builder().deceasedForename("Jane").build();

      when(claimsApiClient.getClaimInquestData(claimId))
          .thenReturn(
              Mono.error(WebClientResponseException.create(404, "Not Found", null, null, null)));
      when(claimsApiClient.createClaimInquestData(claimId, request))
          .thenReturn(Mono.just(response));

      ClaimInquestData result = inquestDataService.save(claimId, request);

      assertThat(result).isEqualTo(response);
      verify(claimsApiClient).createClaimInquestData(claimId, request);
      verify(claimsApiClient, never()).replaceClaimInquestData(claimId, request);
    }

    @Test
    void replacesInquestDataWhenExistingDataIsPresent() {
      ClaimInquestDataWrite request =
          ClaimInquestDataWrite.builder().deceasedForename("Jane").build();
      ClaimInquestData existing = ClaimInquestData.builder().deceasedForename("John").build();
      ClaimInquestData response = ClaimInquestData.builder().deceasedForename("Jane").build();

      when(claimsApiClient.getClaimInquestData(claimId)).thenReturn(Mono.just(existing));
      when(claimsApiClient.replaceClaimInquestData(claimId, request))
          .thenReturn(Mono.just(response));

      ClaimInquestData result = inquestDataService.save(claimId, request);

      assertThat(result).isEqualTo(response);
      verify(claimsApiClient).replaceClaimInquestData(claimId, request);
      verify(claimsApiClient, never()).createClaimInquestData(claimId, request);
    }
  }

  @Nested
  class GetInquestDepartments {

    @Test
    void delegatesToClientAndReturnsList() {
      List<InquestDepartmentReference> departments =
          List.of(
              InquestDepartmentReference.builder()
                  .code("MOJ")
                  .displayLabel("Ministry of Justice")
                  .build());

      when(claimsApiClient.getInquestDepartments()).thenReturn(Mono.just(departments));

      List<InquestDepartmentReference> result = inquestDataService.getInquestDepartments();

      assertThat(result).isEqualTo(departments);
    }
  }
}
