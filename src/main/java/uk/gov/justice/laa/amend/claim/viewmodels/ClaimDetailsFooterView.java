package uk.gov.justice.laa.amend.claim.viewmodels;

import uk.gov.justice.laa.amend.claim.models.ClaimDetails;

public record ClaimDetailsFooterView(
    boolean hasAssessment,
    boolean isAssessmentButtonPresent,
    boolean isAmendmentButtonPresent,
    boolean isVoidButtonPresent) {

  public ClaimDetailsFooterView(
      ClaimDetails claim, boolean isEscapeCaseCaseworker, boolean isClaimAmendmentsCaseworker) {
    this(
        hasAssessment(claim),
        isAssessmentButtonPresent(claim, isEscapeCaseCaseworker),
        isAmendmentButtonPresent(claim, isClaimAmendmentsCaseworker),
        isVoidButtonPresent(claim, isClaimAmendmentsCaseworker));
  }

  private static boolean hasAssessment(ClaimDetails claim) {
    return claim.isHasAssessment();
  }

  private static boolean isAssessmentButtonPresent(
      ClaimDetails claim, boolean isEscapeCaseCaseworker) {
    boolean isEscapedCase = claim.isEscapedCase();
    boolean isStageDisbursement = claim.isStageDisbursement();
    return isEscapeCaseCaseworker && claim.isValid() && (isEscapedCase || isStageDisbursement);
  }

  private static boolean isAmendmentButtonPresent(
      ClaimDetails claim, boolean isClaimAmendmentsCaseworker) {
    return isClaimAmendmentsCaseworker && !claim.isHasAssessment();
  }

  public static boolean isVoidButtonPresent(
      ClaimDetails claim, boolean isClaimAmendmentsCaseworker) {
    return isClaimAmendmentsCaseworker && claim.isValid();
  }
}
