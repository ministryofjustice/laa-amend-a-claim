package uk.gov.justice.laa.amend.claim.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import uk.gov.justice.laa.amend.claim.client.ClaimsApiClient;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimInquestData;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimInquestDataWrite;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.InquestDepartmentReference;

/**
 * Service layer for a claim's inquest data.
 *
 * <p>Wraps the Data Claims API's inquest-data endpoint (GET/POST/PUT) and its department reference
 * endpoint. Callers do not need to know whether a save results in a create or a replace - this
 * service determines that itself, since the API rejects a duplicate create with a 409.
 */
@Service
@Slf4j
public class InquestDataService {

  private final ClaimsApiClient claimsApiClient;

  public InquestDataService(ClaimsApiClient claimsApiClient) {
    this.claimsApiClient = claimsApiClient;
  }

  /**
   * Retrieves the current inquest data for a claim, if any exists.
   *
   * @param claimId the claim to retrieve inquest data for
   * @return the claim's inquest data, or an empty {@link Optional} if none exists
   */
  public Optional<ClaimInquestData> get(UUID claimId) {
    try {
      return Optional.ofNullable(claimsApiClient.getClaimInquestData(claimId).block());
    } catch (WebClientResponseException.NotFound e) {
      log.info("No inquest data found for claim ID: {}", claimId);
      return Optional.empty();
    }
  }

  /**
   * Saves inquest data for a claim, creating it if none exists or replacing it if some already
   * does. The caller does not need to know which of these happens.
   *
   * @param claimId the claim to save inquest data against
   * @param request the inquest data to save
   * @return the saved inquest data as returned by the Data Claims API
   */
  public ClaimInquestData save(UUID claimId, ClaimInquestDataWrite request) {
    Optional<ClaimInquestData> existing = get(claimId);

    if (existing.isPresent()) {
      log.info("Replacing existing inquest data for claim ID: {}", claimId);
      return claimsApiClient.replaceClaimInquestData(claimId, request).block();
    }

    log.info("Creating inquest data for claim ID: {}", claimId);
    return claimsApiClient.createClaimInquestData(claimId, request).block();
  }

  /**
   * Retrieves the governed list of interested-department options (code + display label).
   *
   * @return the list of inquest department references
   */
  public List<InquestDepartmentReference> getInquestDepartments() {
    return claimsApiClient.getInquestDepartments().block();
  }
}
