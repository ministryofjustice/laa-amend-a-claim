package uk.gov.justice.laa.amend.claim.viewmodels.claimclient;

import java.util.LinkedHashMap;
import uk.gov.justice.laa.amend.claim.models.CrimeClaimDetails;

public record CrimeClaimClientView(
    LinkedHashMap<String, Object> client1Rows, LinkedHashMap<String, Object> client2Rows)
    implements ClaimClientView {

  public CrimeClaimClientView(CrimeClaimDetails claim) {
    this(createRows(claim), new LinkedHashMap<>());
  }

  private static LinkedHashMap<String, Object> createRows(CrimeClaimDetails claim) {
    var rows = new LinkedHashMap<String, Object>();

    rows.put("initial", claim.getClientForename());
    rows.put("surname", claim.getClientSurname());
    rows.put("gender", claim.getClientGender());
    rows.put("ethnicity", claim.getClientEthnicity());
    rows.put("disability", claim.getClientDisability());

    return rows;
  }
}
