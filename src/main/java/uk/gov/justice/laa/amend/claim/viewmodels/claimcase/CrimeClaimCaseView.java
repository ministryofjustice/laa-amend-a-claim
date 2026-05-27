package uk.gov.justice.laa.amend.claim.viewmodels.claimcase;

import java.util.LinkedHashMap;
import uk.gov.justice.laa.amend.claim.models.CrimeClaimDetails;

public record CrimeClaimCaseView(
    LinkedHashMap<String, Object> caseTypeRows, LinkedHashMap<String, Object> caseDetailsRows)
    implements ClaimCaseView {

  public CrimeClaimCaseView(CrimeClaimDetails claim) {
    this(createCaseTypeRows(claim), createCaseDetailsRows(claim));
  }

  private static LinkedHashMap<String, Object> createCaseTypeRows(CrimeClaimDetails claim) {
    var rows = new LinkedHashMap<String, Object>();

    rows.put("feeCode", claim.getFeeCode());
    rows.put("matterType", claim.getMatterTypeCode());

    return rows;
  }

  private static LinkedHashMap<String, Object> createCaseDetailsRows(CrimeClaimDetails claim) {
    var rows = new LinkedHashMap<String, Object>();

    rows.put("stageReached", claim.getStageReached());
    rows.put("ufn", claim.getUniqueFileNumber());
    rows.put("representationOrderDate", claim.getRepresentationOrderDate());
    rows.put("caseEndDate", claim.getCaseEndDate());
    rows.put("standardFeeCategory", claim.getStandardFeeCategory());
    rows.put("outcome", claim.getOutcome());
    rows.put("suspectsDefendantsCount", claim.getSuspectsDefendantsCount());
    rows.put("policeStationCourtAttendancesCount", claim.getPoliceStationCourtAttendancesCount());
    rows.put("policeStationCourtPrisonId", claim.getPoliceStationCourtPrisonId());
    rows.put("schemeId", claim.getSchemeId());
    rows.put("dsccNumber", claim.getDsccNumber());
    rows.put("maatId", claim.getMaatId());
    rows.put("prisonLawPriorApprovalNumber", claim.getPrisonLawPriorApprovalNumber());
    rows.put("isDutySolicitor", claim.getIsDutySolicitor());
    rows.put("isYouthCourt", claim.getIsYouthCourt());

    return rows;
  }
}
