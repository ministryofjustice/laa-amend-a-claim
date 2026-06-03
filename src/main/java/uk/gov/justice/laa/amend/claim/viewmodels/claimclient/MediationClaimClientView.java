package uk.gov.justice.laa.amend.claim.viewmodels.claimclient;

import java.util.LinkedHashMap;
import uk.gov.justice.laa.amend.claim.models.MediationClaimDetails;

public record MediationClaimClientView(
    LinkedHashMap<String, Object> client1Rows, LinkedHashMap<String, Object> client2Rows)
    implements ClaimClientView {

  public MediationClaimClientView(MediationClaimDetails claim) {
    this(createClient1Rows(claim), createClient2Rows(claim));
  }

  private static LinkedHashMap<String, Object> createClient1Rows(MediationClaimDetails claim) {
    var rows = new LinkedHashMap<String, Object>();

    rows.put("firstName", claim.getClientForename());
    rows.put("surname", claim.getClientSurname());
    rows.put("dateOfBirth", claim.getClientDateOfBirth());
    rows.put("ucn", claim.getUniqueClientNumber());
    rows.put("postcode", claim.getClientPostcode());
    rows.put("gender", claim.getClientGender());
    rows.put("ethnicity", claim.getClientEthnicity());
    rows.put("disability", claim.getClientDisability());
    rows.put("legallyAided", claim.getIsClientLegallyAided());
    rows.put("postalApplicationAccepted", claim.getIsClientPostalApplicationAccepted());

    return rows;
  }

  private static LinkedHashMap<String, Object> createClient2Rows(MediationClaimDetails claim) {
    var rows = new LinkedHashMap<String, Object>();

    rows.put("firstName", claim.getClient2Forename());
    rows.put("surname", claim.getClient2Surname());
    rows.put("dateOfBirth", claim.getClient2DateOfBirth());
    rows.put("ucn", claim.getClient2Ucn());
    rows.put("postcode", claim.getClient2Postcode());
    rows.put("gender", claim.getClient2Gender());
    rows.put("ethnicity", claim.getClient2Ethnicity());
    rows.put("disability", claim.getClient2Disability());
    rows.put("legallyAided", claim.getIsClient2LegallyAided());
    rows.put("postalApplicationAccepted", claim.getIsClient2PostalApplicationAccepted());

    return rows;
  }
}
