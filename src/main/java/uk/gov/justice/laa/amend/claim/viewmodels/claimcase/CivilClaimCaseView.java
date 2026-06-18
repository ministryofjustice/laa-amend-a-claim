package uk.gov.justice.laa.amend.claim.viewmodels.claimcase;

import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField.FEE_CODE;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField.MATTER_TYPE_CODE_ONE;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField.MATTER_TYPE_CODE_TWO;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField.toFieldMap;

import java.util.LinkedHashMap;
import java.util.stream.Stream;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField;

public record CivilClaimCaseView(
    LinkedHashMap<ClaimViewField<CivilClaimDetails>, Object> caseTypeRows,
    LinkedHashMap<String, Object> caseDetailsRows)
    implements ClaimCaseView<ClaimViewField<CivilClaimDetails>> {

  public CivilClaimCaseView(CivilClaimDetails claim) {
    this(createCaseTypeRows(claim), createCaseDetailsRows(claim));
  }

  private static LinkedHashMap<ClaimViewField<CivilClaimDetails>, Object> createCaseTypeRows(
      CivilClaimDetails claim) {
    Stream<ClaimViewField<CivilClaimDetails>> fields =
        Stream.of(FEE_CODE, MATTER_TYPE_CODE_ONE, MATTER_TYPE_CODE_TWO);

    return toFieldMap(fields, claim);
  }

  private static LinkedHashMap<String, Object> createCaseDetailsRows(CivilClaimDetails claim) {
    var rows = new LinkedHashMap<String, Object>();

    rows.put("scheduleReferenceCivil", claim.getScheduleReference());
    rows.put("caseIdCivil", claim.getCaseId());
    rows.put("caseReferenceNumber", claim.getCaseReferenceNumber());
    rows.put("caseStartDate", claim.getCaseStartDate());
    rows.put("caseConcludedDate", claim.getCaseConcludedDate());
    rows.put("uniqueFileNumber", claim.getUniqueFileNumber());
    rows.put("caseStage", claim.getCaseStage());
    rows.put("valueOfCosts", claim.getValueOfCosts());
    rows.put("procurementArea", claim.getProcurementArea());
    rows.put("accessPoint", claim.getAccessPoint());
    rows.put("stageReached", claim.getStageReached());
    rows.put("outcomeForClient", claim.getOutcome());
    rows.put("exceptionalCaseFundingReference", claim.getExceptionalCaseFundingReference());
    rows.put("civilLegalAdviceReference", claim.getCivilLegalAdviceReference());
    rows.put("civilLegalAdviceExemption", claim.getCivilLegalAdviceExemption());
    rows.put("deliveryLocation", claim.getDeliveryLocation());
    rows.put("courtLocation", claim.getCourtLocation());
    rows.put("aitHearingCentre", claim.getAitHearingCentre());
    rows.put("localAuthorityNumber", claim.getLocalAuthorityNumber());
    rows.put("designatedAccreditedRepresentative", claim.getDesignatedAccreditedRepresentative());
    rows.put("adviceTime", claim.getAdviceTime());
    rows.put("travelTime", claim.getTravelTime());
    rows.put("waitingTime", claim.getWaitingTime());
    rows.put("isAdditionalTravelPayment", claim.getIsAdditionalTravelPayment());
    rows.put("followOnWork", claim.getFollowOnWork());
    rows.put("isToleranceApplicable", claim.getIsToleranceApplicable());
    rows.put("isLegacyCase", claim.getIsLegacyCase());
    rows.put("meetingsAttended", claim.getMeetingsAttended());
    rows.put("adviceType", claim.getAdviceType());
    rows.put("transferDate", claim.getTransferDate());
    rows.put("medicalReportsClaimed", claim.getMedicalReportsClaimed());
    rows.put("exemptionCriteriaSatisfied", claim.getExemptionCriteriaSatisfied());
    rows.put("isIrcSurgery", claim.getIsIrcSurgery());
    rows.put("surgeryDate", claim.getSurgeryDate());
    rows.put("surgeryClientsCount", claim.getSurgeryClientsCount());
    rows.put("surgeryMattersCount", claim.getSurgeryMattersCount());
    rows.put("mentalHealthTribunalReference", claim.getMentalHealthTribunalReference());
    rows.put("isNrmAdvice", claim.getIsNrmAdvice());

    return rows;
  }
}
