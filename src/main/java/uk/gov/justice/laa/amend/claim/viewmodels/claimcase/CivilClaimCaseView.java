package uk.gov.justice.laa.amend.claim.viewmodels.claimcase;

import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField.toFieldMap;

import java.util.LinkedHashMap;
import java.util.stream.Stream;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField;

public record CivilClaimCaseView(
    LinkedHashMap<ClaimViewField<CivilClaimDetails>, Object> caseTypeRows,
    LinkedHashMap<ClaimViewField<CivilClaimDetails>, Object> caseDetailsRows)
    implements ClaimCaseView<ClaimViewField<CivilClaimDetails>> {

  public CivilClaimCaseView(CivilClaimDetails claim) {
    this(createCaseTypeRows(claim), createCaseDetailsRows(claim));
  }

  private static LinkedHashMap<ClaimViewField<CivilClaimDetails>, Object> createCaseTypeRows(
      CivilClaimDetails claim) {
    Stream<ClaimViewField<CivilClaimDetails>> fields =
        Stream.of(
            CivilClaimDetailsViewField.FEE_CODE,
            CivilClaimDetailsViewField.MATTER_TYPE_CODE_1,
            CivilClaimDetailsViewField.MATTER_TYPE_CODE_2);

    return toFieldMap(fields, claim);
  }

  private static LinkedHashMap<ClaimViewField<CivilClaimDetails>, Object> createCaseDetailsRows(
      CivilClaimDetails claim) {
    Stream<ClaimViewField<CivilClaimDetails>> fields =
        Stream.of(
            CivilClaimDetailsViewField.SCHEDULE_REFERENCE_CIVIL,
            CivilClaimDetailsViewField.CASE_ID,
            CivilClaimDetailsViewField.CASE_REFERENCE_NUMBER,
            CivilClaimDetailsViewField.CASE_START_DATE,
            CivilClaimDetailsViewField.CASE_CONCLUDED_CLAIMED_DATE,
            CivilClaimDetailsViewField.UNIQUE_FILE_NUMBER,
            CivilClaimDetailsViewField.CASE_STAGE,
            CivilClaimDetailsViewField.VALUE_OF_COSTS,
            CivilClaimDetailsViewField.PROCUREMENT_AREA,
            CivilClaimDetailsViewField.ACCESS_POINT,
            CivilClaimDetailsViewField.STAGE_REACHED,
            CivilClaimDetailsViewField.OUTCOME_FOR_CLIENT,
            CivilClaimDetailsViewField.EXCEPTIONAL_CASE_FUNDING,
            CivilClaimDetailsViewField.CIVIL_LEGAL_ADVICE_REFERENCE,
            CivilClaimDetailsViewField.CIVIL_LEGAL_ADVICE_EXEMPTION,
            CivilClaimDetailsViewField.DELIVERY_LOCATION,
            CivilClaimDetailsViewField.COURT_LOCATION,
            CivilClaimDetailsViewField.AIT_HEARING_CENTRE,
            CivilClaimDetailsViewField.LOCAL_AUTHORITY_NUMBER,
            CivilClaimDetailsViewField.DESIGNATED_ACCREDITED_REPRESENTATIVE,
            CivilClaimDetailsViewField.ADVICE_TIME,
            CivilClaimDetailsViewField.TRAVEL_TIME,
            CivilClaimDetailsViewField.WAITING_TIME,
            CivilClaimDetailsViewField.ADDITIONAL_TRAVEL_PAYMENT,
            CivilClaimDetailsViewField.FOLLOW_ON_WORK,
            CivilClaimDetailsViewField.TOLERANCE_INDICATOR,
            CivilClaimDetailsViewField.LEGACY_CASE,
            CivilClaimDetailsViewField.MEETINGS_ATTENDED,
            CivilClaimDetailsViewField.ADVICE_TYPE,
            CivilClaimDetailsViewField.TRANSFER_DATE,
            CivilClaimDetailsViewField.MEDICAL_REPORTS_CLAIMED,
            CivilClaimDetailsViewField.EXEMPTION_CRITERIA_SATISFIED,
            CivilClaimDetailsViewField.IRC_SURGERY,
            CivilClaimDetailsViewField.SURGERY_DATE,
            CivilClaimDetailsViewField.SURGERY_CLIENTS_COUNT,
            CivilClaimDetailsViewField.SURGERY_MATTERS_COUNT,
            CivilClaimDetailsViewField.MENTAL_HEALTH_TRIBUNAL_REFERENCE,
            CivilClaimDetailsViewField.IS_NRM_ADVICE);
    return toFieldMap(fields, claim);
  }
}
