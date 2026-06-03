package uk.gov.justice.laa.amend.claim.viewmodels.claimcase;

import java.util.LinkedHashMap;
import uk.gov.justice.laa.amend.claim.models.MediationClaimDetails;

public record MediationClaimCaseView(
    LinkedHashMap<String, Object> caseTypeRows, LinkedHashMap<String, Object> caseDetailsRows)
    implements ClaimCaseView {

  public MediationClaimCaseView(MediationClaimDetails claim) {
    this(createCaseTypeRows(claim), createCaseDetailsRows(claim));
  }

  private static LinkedHashMap<String, Object> createCaseTypeRows(MediationClaimDetails claim) {
    var rows = new LinkedHashMap<String, Object>();

    rows.put("feeCode", claim.getFeeCode());
    rows.put("matterType", claim.getMatterTypeCode());

    return rows;
  }

  private static LinkedHashMap<String, Object> createCaseDetailsRows(MediationClaimDetails claim) {
    var rows = new LinkedHashMap<String, Object>();

    rows.put("caseReferenceNumber", claim.getCaseReferenceNumber());
    rows.put("caseStartDate", claim.getCaseStartDate());
    rows.put("caseId", claim.getCaseId());
    rows.put("uniqueCaseId", claim.getUniqueCaseId());
    rows.put("caseEndDate", claim.getCaseEndDate());
    rows.put("mediationSessionsCount", claim.getMediationSessionsCount());
    rows.put("mediationTimeMinutes", claim.getMediationTimeMinutes());
    rows.put("outcome", claim.getOutcome());
    rows.put("outreachLocation", claim.getOutreachLocation());
    rows.put("referralSource", claim.getReferralSource());
    rows.put("scheduleReference", claim.getScheduleReference());

    return rows;
  }
}
