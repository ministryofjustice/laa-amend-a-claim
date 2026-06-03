package uk.gov.justice.laa.amend.claim.viewmodels;

import java.util.LinkedHashMap;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.viewmodels.claimclient.ClaimClientView;

public record CivilClaimClientView(LinkedHashMap<String, Object> client1Rows, LinkedHashMap<String, Object> client2Rows) implements ClaimClientView {

  public CivilClaimClientView(CivilClaimDetails claim) {
      this(createRows(claim), new LinkedHashMap<>());
  }

  private static LinkedHashMap<String, Object> createRows(CivilClaimDetails claim) {
    var rows = new LinkedHashMap<String, Object>();

    rows.put("firstName", claim.getClientForename());
    rows.put("surname", claim.getClientSurname());
    rows.put("dateOfBirth", claim.getClientDateOfBirth());
    rows.put("gender", claim.getClientGender());
    rows.put("ethnicity", claim.getClientEthnicity());
    rows.put("disability", claim.getClientDisability());
    rows.put("postcode", claim.getClientPostcode());
    rows.put("eligibleClient", claim.getEligibleClient());
    rows.put("clientType", claim.getClientType());
    rows.put("ucn", claim.getUniqueClientNumber());
    rows.put("hoUcn", claim.getHoUcn());

    return rows;
  }
}
