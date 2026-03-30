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
import uk.gov.justice.laa.amend.claim.mappers.ClaimMapper;
import uk.gov.justice.laa.amend.claim.models.AreaOfLaw;
import uk.gov.justice.laa.amend.claim.service.ClaimService;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResponseV2;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResultSetV2;

@Component
@RequiredArgsConstructor
public class BulkUploadHelper {
    public static final int ROW_OFFSET = 2;
    public static final int MAX_ROWS = 1300;

    private final ClaimService claimService;

    private final ClaimMapper claimMapper;

    /**
     * Process Claims for each office code and return the matched UFNs from file
     */
    private List<ClaimResponseV2> processAllClaimsForOfficeCodeAndUpdateRow(
            String officeCode, Map<Pair<String, String>, BulkUploadCivilClaim> officeCodeUfnRows) {
        int pageSize = 200;

        List<ClaimResponseV2> claims = new ArrayList<>();
        fetchClaimsPages(officeCode, pageSize)
                .filter(ufnExistsInRowsPredicate(officeCodeUfnRows))
                .forEach(claim -> {
                    String ufn = claim.getUniqueFileNumber();
                    BulkUploadCivilClaim row = officeCodeUfnRows.get(Pair.of(officeCode, ufn));
                    claims.add(claim);
                });
        return claims;
    }

    public List<ClaimResponseV2> getAllClaims(List<BulkUploadCivilClaim> rows, List<String> errors) {
        var officeCodes = rows.stream().map(BulkUploadCivilClaim::getOfficeCode).collect(Collectors.toSet());
        var officeCodeUfnRows = getOfficeCodeUfnRows(rows, errors);
        return officeCodes.stream()
                .map(officeCode -> processAllClaimsForOfficeCodeAndUpdateRow(officeCode, officeCodeUfnRows))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    /**
     * Map officeCode with UFN and row index from given rows of csv
     */
    public Map<Pair<String, String>, BulkUploadCivilClaim> getOfficeCodeUfnRows(
            List<BulkUploadCivilClaim> rows, List<String> errors) {

        var officeUfnToClaim = new HashMap<Pair<String, String>, BulkUploadCivilClaim>();
        Set<Pair<String, String>> appearedKeys = new HashSet<>();

        IntStream.range(0, rows.size()).forEach(i -> {
            BulkUploadCivilClaim row = rows.get(i);
            row.setRowNumber(i + ROW_OFFSET); // update row number (CSV header = line 1)
            String office = row.getOfficeCode();
            String ufn = row.getUfn();

            Pair<String, String> key = Pair.of(office.trim(), ufn.trim());
            // Duplicate Rows (officeCode + UFN)
            if (!appearedKeys.add(key)) {
                errors.add(String.format(
                        "Line %d: Duplicate row for office code %s and UFN %s", row.getRowNumber(), office, ufn));
            }
            // Save mapping
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

    private Stream<ClaimResponseV2> fetchClaimsPages(String officeCode, int pageSize) {

        ClaimResultSetV2 first = safeFetch(officeCode, 1, pageSize);
        if (first == null || first.getContent() == null) {
            throw new RuntimeException("No claims found for office code " + officeCode);
        }
        if (first.getContent().isEmpty()) {
            return Stream.empty();
        }
        int totalPages = Optional.ofNullable(first.getTotalPages()).orElse(1);

        // Stream first page
        Stream<ClaimResponseV2> pageOne = first.getContent().stream();

        // Stream remaining pages
        Stream<ClaimResponseV2> others = IntStream.rangeClosed(2, totalPages)
                .mapToObj(p -> safeFetch(officeCode, p, pageSize))
                .filter(r -> r.getContent() != null)
                .flatMap(r -> r.getContent().stream());

        return Stream.concat(pageOne, others);
    }

    private ClaimResultSetV2 safeFetch(String officeCode, int page, int pageSize) {
        try {
            return claimService.searchClaims(
                    officeCode,
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.of(AreaOfLaw.LEGAL_HELP),
                    Optional.of(true),
                    page,
                    pageSize,
                    null);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching claims for office code " + officeCode + " page " + page, e);
        }
    }
}
