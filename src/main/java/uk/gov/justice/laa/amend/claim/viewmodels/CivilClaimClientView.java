package uk.gov.justice.laa.amend.claim.viewmodels;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import java.util.LinkedHashMap;
import java.util.stream.Stream;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.amendment.BaseClaimDetailsField;
import uk.gov.justice.laa.amend.claim.models.amendment.CivilClaimDetailsField;
import uk.gov.justice.laa.amend.claim.models.amendment.ClaimDetailsField;
import uk.gov.justice.laa.amend.claim.viewmodels.claimclient.ClaimClientView;

public record CivilClaimClientView(
    LinkedHashMap<BaseClaimDetailsField<CivilClaimDetails>, Object> client1Rows,
    LinkedHashMap<String, Object> client2Rows)
    implements ClaimClientView {

  public CivilClaimClientView(CivilClaimDetails claim) {
    this(createRows(claim), new LinkedHashMap<>());
  }

  private static LinkedHashMap<BaseClaimDetailsField<CivilClaimDetails>, Object> createRows(
      CivilClaimDetails claim) {
    Stream<BaseClaimDetailsField<CivilClaimDetails>> fields =
        Stream.of(asCivilField(ClaimDetailsField.SURNAME), CivilClaimDetailsField.DATE_OF_BIRTH);

    return fields.collect(
        toMap(
            identity(),
            field -> field.getAccessor().getter().apply(claim),
            (a, b) -> b,
            LinkedHashMap::new));
  }

  @SuppressWarnings("unchecked")
  private static BaseClaimDetailsField<CivilClaimDetails> asCivilField(
      BaseClaimDetailsField<? super CivilClaimDetails> field) {
    return (BaseClaimDetailsField<CivilClaimDetails>) field;
  }
}
