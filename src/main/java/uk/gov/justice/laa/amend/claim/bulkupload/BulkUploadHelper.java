package uk.gov.justice.laa.amend.claim.bulkupload;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.amend.claim.bulkupload.civil.BulkUploadCivilClaim;
import uk.gov.justice.laa.amend.claim.models.AreaOfLaw;
import uk.gov.justice.laa.amend.claim.service.ClaimService;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResponseV2;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimStatus;

@Component
@RequiredArgsConstructor
public class BulkUploadHelper {
  public static final int MAX_ROWS = 10000;
  public static final int PAGE_SIZE = 200;
  private static final int MAX_FETCH_ATTEMPTS = 3;

  private final ClaimService claimService;

  public List<ClaimResponseV2> getAllClaims(
      List<BulkUploadCivilClaim> rows, List<BulkUploadError> errors) {
    var officeCodes =
        rows.stream().map(BulkUploadCivilClaim::getOfficeCode).collect(Collectors.toSet());
    var officeCodeUfnRows = getOfficeCodeUfnRows(rows, errors);
    return officeCodes.stream()
        .map(officeCode -> getAllClaims(officeCode, officeCodeUfnRows))
        .flatMap(List::stream)
        .collect(Collectors.toList());
  }

  private List<ClaimResponseV2> getAllClaims(
      String officeCode, Map<Pair<String, String>, BulkUploadCivilClaim> officeCodeUfnRows) {
    return fetchAllValidClaims(officeCode).values().stream()
        .filter(ufnExistsInRowsPredicate(officeCodeUfnRows))
        .collect(Collectors.toList());
  }

  /** Map officeCode with UFN and row index from given rows of csv. */
  public Map<Pair<String, String>, BulkUploadCivilClaim> getOfficeCodeUfnRows(
      List<BulkUploadCivilClaim> rows, List<BulkUploadError> errors) {

    var officeUfnToClaim = new HashMap<Pair<String, String>, BulkUploadCivilClaim>();
    Set<Pair<String, String>> appearedKeys = new HashSet<>();

    IntStream.range(0, rows.size())
        .forEach(
            i -> {
              BulkUploadCivilClaim row = rows.get(i);
              String office = row.getOfficeCode();
              String ufn = row.getUfn();

              Pair<String, String> key = Pair.of(office, ufn);
              if (!appearedKeys.add(key)) {
                errors.add(
                    new BulkUploadError(
                        row.getRowNumber(),
                        String.format("Duplicate row for office code %s and UFN %s", office, ufn)));
              }
              officeUfnToClaim.put(key, row);
            });
    return officeUfnToClaim;
  }

  private Predicate<ClaimResponseV2> ufnExistsInRowsPredicate(
      Map<Pair<String, String>, BulkUploadCivilClaim> ufnToRowIdx) {
    return claim -> {
      String ufn = claim.getUniqueFileNumber();
      String officeCode = claim.getOfficeCode();
      if (StringUtils.isBlank(ufn) || StringUtils.isBlank(officeCode)) {
        return false;
      }
      return ufnToRowIdx.containsKey(Pair.of(claim.getOfficeCode(), ufn));
    };
  }

  /**
   * Fetches all distinct valid escaped claims for an office, keyed by claim ID.
   *
   * <p>The Claims API does not guarantee a stable, unique sort order across pages. As a result, a
   * single paginated sweep may return the same claim more than once or omit claims entirely.
   *
   * <p>Duplicate claims are removed by keying on claim ID. Missing claims are detected by comparing
   * the number of distinct claims collected with the total element count reported by the initial
   * call.
   *
   * <p>If the reported total still cannot be reconciled after retrying, the upload is failed rather
   * than risking silent data loss.
   */
  private Map<UUID, ClaimResponseV2> fetchAllValidClaims(String officeCode) {
    Map<UUID, ClaimResponseV2> claimsById = new LinkedHashMap<>();
    int expectedTotal = collectDistinctClaimsInto(officeCode, claimsById);

    int attempts = 1;
    while (claimsById.size() < expectedTotal && attempts < MAX_FETCH_ATTEMPTS) {
      collectDistinctClaimsInto(officeCode, claimsById);
      attempts++;
    }

    if (claimsById.size() < expectedTotal) {
      throw new RuntimeException(
          String.format(
              "Could not retrieve all valid claims for office code %s: expected %d but only "
                  + "resolved %d distinct claims after %d attempts",
              officeCode, expectedTotal, claimsById.size(), attempts));
    }
    return claimsById;
  }

  private int collectDistinctClaimsInto(String officeCode, Map<UUID, ClaimResponseV2> claimsById) {
    var firstPage = safeFetch(officeCode, 1);
    addDistinct(firstPage, claimsById);

    int totalPages = firstPage.totalPages();
    for (int page = 2; page <= totalPages; page++) {
      addDistinct(safeFetch(officeCode, page), claimsById);
    }
    return firstPage.totalElements();
  }

  private void addDistinct(PageResult page, Map<UUID, ClaimResponseV2> claimsById) {
    page.claimResponseV2Stream()
        .forEach(
            claim ->
                claimsById.putIfAbsent(
                    UUID.fromString(Objects.requireNonNull(claim.getId())), claim));
  }

  private PageResult safeFetch(String officeCode, int page) {
    try {
      var result =
          claimService.searchClaims(
              officeCode,
              Optional.empty(),
              Optional.empty(),
              Optional.empty(),
              Optional.of(AreaOfLaw.LEGAL_HELP),
              Optional.of(true),
              List.of(ClaimStatus.VALID),
              page,
              BulkUploadHelper.PAGE_SIZE,
              null);
      if (result == null || result.getContent() == null) {
        throw new RuntimeException("No claims found for office code " + officeCode);
      }
      int totalPages = Optional.ofNullable(result.getTotalPages()).orElse(1);
      int totalElements = Optional.ofNullable(result.getTotalElements()).orElse(0);

      Stream<ClaimResponseV2> responseStream =
          (result.getContent().isEmpty()) ? Stream.empty() : result.getContent().stream();

      return new PageResult(responseStream, totalPages, totalElements);
    } catch (Exception e) {
      throw new RuntimeException(
          "Error fetching claims for office code " + officeCode + " page " + page, e);
    }
  }

  private record PageResult(
      Stream<ClaimResponseV2> claimResponseV2Stream, int totalPages, int totalElements) {}
}
