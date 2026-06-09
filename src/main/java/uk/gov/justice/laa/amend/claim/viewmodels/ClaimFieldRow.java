package uk.gov.justice.laa.amend.claim.viewmodels;

import static uk.gov.justice.laa.amend.claim.utils.NumberUtils.getOrElseZero;

import uk.gov.justice.laa.amend.claim.models.*;
import uk.gov.justice.laa.amend.claim.utils.FormUtils;

public record ClaimFieldRow(
    String key,
    Object submitted,
    Object calculated,
    Object assessed,
    boolean assessable,
    String changeUrl) {

  public static ClaimFieldRow from(AllowedClaimField claimField) {
    return new ClaimFieldRow(
        claimField.getKey(),
        claimField.getSubmitted(),
        getOrElseZero(claimField.getCalculated()),
        claimField.getAssessed(),
        claimField.isAssessable(),
        "/submissions/%s/claims/%s/allowed-totals");
  }

  public static ClaimFieldRow from(AssessedClaimField claimField) {
    return new ClaimFieldRow(
        claimField.getKey(),
        claimField.getSubmitted(),
        claimField.getCalculated(),
        claimField.getAssessed(),
        claimField.isAssessable(),
        "/submissions/%s/claims/%s/assessed-totals");
  }

  public static ClaimFieldRow from(BoltOnClaimField claimField) {
    if (claimField.hasSubmittedValue()) {
      return createRow(claimField, claimField.getSubmitted(), claimField.getCalculated());
    }
    return null;
  }

  public static ClaimFieldRow fromCustom(BoltOnClaimField claimField) {
    if (!claimField.hasSubmittedValue()) {
        return createRow(claimField, "Not applicable", "Not applicable");
      }
    return createRow(claimField, claimField.getSubmitted(), claimField.getCalculated());
  }
  public static ClaimFieldRow from(CostClaimField claimField) {
    Object submitted;
    Object calculated;
    Object assessed;
    switch (claimField.getCost()) {
      case PROFIT_COSTS, DISBURSEMENTS, DISBURSEMENTS_VAT -> {
        submitted = claimField.getSubmitted();
        calculated = claimField.getCalculated();
        assessed = claimField.getAssessed();
      }
      default -> {
        submitted = getOrElseZero(claimField.getSubmitted());
        calculated = getOrElseZero(claimField.getCalculated());
        assessed = getOrElseZero(claimField.getAssessed());
      }
    }
    return new ClaimFieldRow(
        claimField.getKey(),
        submitted,
        calculated,
        assessed,
        claimField.isAssessable(),
        claimField.getCost().getChangeUrl());
  }

  public static ClaimFieldRow from(CalculatedTotalClaimField claimField) {
    return new ClaimFieldRow(
        claimField.getKey(),
        claimField.getSubmitted(),
        claimField.getCalculated(),
        claimField.getAssessed(),
        claimField.isAssessable(),
        null);
  }

  public static ClaimFieldRow from(FixedFeeClaimField claimField) {
    return new ClaimFieldRow(
        claimField.getKey(),
        claimField.getSubmitted(),
        claimField.getCalculated(),
        claimField.getAssessed(),
        claimField.isAssessable(),
        null);
  }

  public static ClaimFieldRow from(VatLiabilityClaimField claimField) {
    return new ClaimFieldRow(
        claimField.getKey(),
        claimField.getSubmitted(),
        claimField.getCalculated(),
        claimField.getAssessed(),
        claimField.isAssessable(),
        "/submissions/%s/claims/%s/assessment-outcome");
  }

  public static ClaimFieldRow from(SubmittedClaimField claimField) {
    return new ClaimFieldRow(
        claimField.getKey(),
        claimField.getSubmitted(),
        null,
        null,
        claimField.isAssessable(),
        null);
  }

  public String getLabel() {
    return String.format("claimSummary.rows.%s", key);
  }

  public String getId() {
    return FormUtils.toFieldId(key);
  }

  public String getErrorKey() {
    return String.format("claimSummary.rows.%s.error", key);
  }

  public String getChangeUrl(String submissionId, String claimId) {
    if (changeUrl == null) {
      return null;
    }

    return String.format(changeUrl, submissionId, claimId);
  }

  public boolean isAssessableAndUnassessed() {
    return assessable && !hasAssessedValue();
  }

  public boolean isAssessableAndAssessed() {
    return assessable && hasAssessedValue();
  }

  public boolean hasAssessedValue() {
    return assessed != null;
  }

  private static ClaimFieldRow createRow(ClaimField claimField, Object submitted, Object calculated){
    return new ClaimFieldRow(
      claimField.getKey(),
      submitted,
      calculated,
      claimField.getAssessed(),
      claimField.isAssessable(),
      null);
    }
}
