package uk.gov.justice.laa.amend.claim.bulkupload;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
    List<ClaimResponseV2> claims = new ArrayList<>();
    fetchValidClaims(officeCode)
        .filter(ufnExistsInRowsPredicate(officeCodeUfnRows))
        .forEach(claims::add);
    return claims;
  }

  /** Map officeCode with UFN and row index from given rows of csv */
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
              // Duplicate Rows (officeCode + UFN)
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

  private Stream<ClaimResponseV2> fetchValidClaims(String officeCode) {

    var firstPage = safeFetch(officeCode, 1, PAGE_SIZE);
    int totalPages = Optional.of(firstPage.totalPages()).orElse(1);

    // Stream first page
    Stream<ClaimResponseV2> pageOne = firstPage.claimResponseV2Stream();

    // Stream remaining pages
    Stream<ClaimResponseV2> others =
        IntStream.rangeClosed(2, totalPages)
            .mapToObj(p -> safeFetch(officeCode, p, PAGE_SIZE))
            .flatMap(PageResult::claimResponseV2Stream);

    return Stream.concat(pageOne, others);
  }

  private PageResult safeFetch(String officeCode, int page, int pageSize) {
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
              pageSize,
              null);
      if (result == null || result.getContent() == null) {
        throw new RuntimeException("No claims found for office code " + officeCode);
      }
      int totalPages = Optional.ofNullable(result.getTotalPages()).orElse(1);

      Stream<ClaimResponseV2> responseStream =
          (result.getContent().isEmpty()) ? Stream.empty() : result.getContent().stream();

      return new PageResult(responseStream, totalPages);
    } catch (Exception e) {
      throw new RuntimeException(
          "Error fetching claims for office code " + officeCode + " page " + page, e);
    }
  }

  private record PageResult(Stream<ClaimResponseV2> claimResponseV2Stream, int totalPages) {}
}
