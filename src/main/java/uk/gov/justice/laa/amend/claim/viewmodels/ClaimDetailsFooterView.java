package uk.gov.justice.laa.amend.claim.viewmodels;

import uk.gov.justice.laa.amend.claim.models.ClaimDetails;

public record ClaimDetailsFooterView(
    ClaimDetails claim, boolean isEscapeCaseCaseworker, boolean isClaimAmendmentsCaseworker) {

  public boolean isHasAssessment() {
    return claim.isHasAssessment();
  }

  public boolean isAssessmentButtonPresent() {
    boolean isEscapedCase = claim.isEscapedCase();
    boolean isStageDisbursement = claim.isStageDisbursement();
    return isEscapeCaseCaseworker && claim.isValid() && (isEscapedCase || isStageDisbursement);
  }

  public boolean isVoidButtonPresent() {
    return isClaimAmendmentsCaseworker && claim.isValid();
  }
}
