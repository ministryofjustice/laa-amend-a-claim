package uk.gov.justice.laa.amend.claim.viewmodels;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import java.util.LinkedHashMap;
import java.util.stream.Stream;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
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
    //    var rows = new LinkedHashMap<String, Object>();
    //
    //    rows.put("firstName", claim.getClientForename());
    //    rows.put("surname", claim.getClientSurname());
    //    rows.put("dateOfBirth", claim.getClientDateOfBirth());
    //    rows.put("gender", claim.getClientGender());
    //    rows.put("ethnicity", claim.getClientEthnicity());
    //    rows.put("disability", claim.getClientDisability());
    //    rows.put("postcode", claim.getClientPostcode());
    //    rows.put("isEligibleClient", claim.getIsEligibleClient());
    //    rows.put("clientType", claim.getClientType());
    //    rows.put("ucn", claim.getUniqueClientNumber());
    //    rows.put("homeOfficeClientNumber", claim.getHomeOfficeClientNumber());

    Stream<BaseClaimDetailsField<CivilClaimDetails>> fields =
        Stream.<BaseClaimDetailsField<CivilClaimDetails>>of(
            ClaimDetailsField.SURNAME, CivilClaimDetailsField.DATE_OF_BIRTH);

    Stream<BaseClaimDetailsField<? extends ClaimDetails>> fieldsJamie =
        Stream.of(ClaimDetailsField.SURNAME, CivilClaimDetailsField.DATE_OF_BIRTH);

    var jamieTest =
        fieldsJamie.collect(
            toMap(
                identity(),
                field -> field.getAccessor().getter().apply(claim),
                (a, b) -> b,
                LinkedHashMap::new));

    return fields.collect(
        toMap(
            identity(),
            field -> field.getAccessor().getter().apply(claim),
            (a, b) -> b,
            LinkedHashMap::new));
  }
}
