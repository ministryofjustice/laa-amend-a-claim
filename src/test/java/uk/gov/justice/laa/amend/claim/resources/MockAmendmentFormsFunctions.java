package uk.gov.justice.laa.amend.claim.resources;

import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForms;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.viewmodels.claimcase.ClaimCaseViewFactory;
import uk.gov.justice.laa.amend.claim.viewmodels.claimclient.ClaimClientViewFactory;

public class MockAmendmentFormsFunctions {

  public static AmendmentForms empty() {
    return AmendmentForms.builder()
        .client1(new AmendmentForm())
        .client2(new AmendmentForm())
        .caseType(new AmendmentForm())
        .caseDetails(new AmendmentForm())
        .build();
  }

  public static AmendmentForms justClient1Filled(ClaimDetails claim) {
    var view = ClaimClientViewFactory.create(claim);
    return AmendmentForms.builder()
        .client1(new AmendmentForm(view.client1Rows()))
        .client2(new AmendmentForm())
        .caseType(new AmendmentForm())
        .caseDetails(new AmendmentForm())
        .build();
  }

  public static AmendmentForms justClient2Filled(ClaimDetails claim) {
    var view = ClaimClientViewFactory.create(claim);
    return AmendmentForms.builder()
        .client1(new AmendmentForm())
        .client2(new AmendmentForm(view.client2Rows()))
        .caseType(new AmendmentForm())
        .caseDetails(new AmendmentForm())
        .build();
  }

  public static AmendmentForms justCaseTypeFilled(ClaimDetails claim) {
    var view = ClaimCaseViewFactory.create(claim);
    return AmendmentForms.builder()
        .client1(new AmendmentForm())
        .client2(new AmendmentForm())
        .caseType(new AmendmentForm(view.caseTypeRows()))
        .caseDetails(new AmendmentForm())
        .build();
  }

  public static AmendmentForms justCaseDetailsFilled(ClaimDetails claim) {
    var view = ClaimCaseViewFactory.create(claim);
    return AmendmentForms.builder()
        .client1(new AmendmentForm())
        .client2(new AmendmentForm())
        .caseType(new AmendmentForm())
        .caseDetails(new AmendmentForm(view.caseDetailsRows()))
        .build();
  }
}
