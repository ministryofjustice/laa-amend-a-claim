package uk.gov.justice.laa.amend.claim.viewmodels.claimcase;

import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField.ACCESS_POINT;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField.ADDITIONAL_TRAVEL_PAYMENT;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField.ADVICE_TIME;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField.ADVICE_TYPE;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField.AIT_HEARING_CENTRE;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField.CASE_CONCLUDED_DATE;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField.CASE_ID;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField.CASE_REFERENCE_NUMBER;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField.CASE_STAGE;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField.CASE_START_DATE;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField.CIVIL_LEGAL_ADVICE_EXEMPTION;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField.CIVIL_LEGAL_ADVICE_REFERENCE;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField.COURT_LOCATION;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField.DELIVERY_LOCATION;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField.DESIGNATED_ACCREDITED_REPRESENTATIVE;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField.EXCEPTIONAL_CASE_FUNDING;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField.EXEMPTION_CRITERIA_SATISFIED;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField.FEE_CODE;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField.FOLLOW_ON_WORK;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField.IRC_SURGERY;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField.IS_NRM_ADVICE;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField.LEGACY_CASE;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField.LOCAL_AUTHORITY_NUMBER;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField.MATTER_TYPE_CODE_ONE;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField.MATTER_TYPE_CODE_TWO;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField.MEDICAL_REPORTS_CLAIMED;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField.MEETINGS_ATTENDED;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField.MENTAL_HEALTH_TRIBUNAL_REFERENCE;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField.OUTCOME_FOR_CLIENT;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField.PROCUREMENT_AREA;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField.SCHEDULE_REFERENCE_CIVIL;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField.STAGE_REACHED;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField.SURGERY_CLIENTS_COUNT;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField.SURGERY_DATE;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField.SURGERY_MATTERS_COUNT;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField.TOLERANCE_INDICATOR;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField.TRANSFER_DATE;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField.TRAVEL_TIME;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField.UNIQUE_FILE_NUMBER;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField.VALUE_OF_COSTS;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField.WAITING_TIME;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField.toFieldMap;

import java.util.LinkedHashMap;
import java.util.stream.Stream;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
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
        Stream.of(FEE_CODE, MATTER_TYPE_CODE_ONE, MATTER_TYPE_CODE_TWO);

    return toFieldMap(fields, claim);
  }

  private static LinkedHashMap<ClaimViewField<CivilClaimDetails>, Object> createCaseDetailsRows(
      CivilClaimDetails claim) {
    Stream<ClaimViewField<CivilClaimDetails>> fields =
        Stream.of(
            SCHEDULE_REFERENCE_CIVIL,
            CASE_ID,
            CASE_REFERENCE_NUMBER,
            CASE_START_DATE,
            CASE_CONCLUDED_DATE,
            UNIQUE_FILE_NUMBER,
            CASE_STAGE,
            VALUE_OF_COSTS,
            PROCUREMENT_AREA,
            ACCESS_POINT,
            STAGE_REACHED,
            OUTCOME_FOR_CLIENT,
            EXCEPTIONAL_CASE_FUNDING,
            CIVIL_LEGAL_ADVICE_REFERENCE,
            CIVIL_LEGAL_ADVICE_EXEMPTION,
            DELIVERY_LOCATION,
            COURT_LOCATION,
            AIT_HEARING_CENTRE,
            LOCAL_AUTHORITY_NUMBER,
            DESIGNATED_ACCREDITED_REPRESENTATIVE,
            ADVICE_TIME,
            TRAVEL_TIME,
            WAITING_TIME,
            ADDITIONAL_TRAVEL_PAYMENT,
            FOLLOW_ON_WORK,
            TOLERANCE_INDICATOR,
            LEGACY_CASE,
            MEETINGS_ATTENDED,
            ADVICE_TYPE,
            TRANSFER_DATE,
            MEDICAL_REPORTS_CLAIMED,
            EXEMPTION_CRITERIA_SATISFIED,
            IRC_SURGERY,
            SURGERY_DATE,
            SURGERY_CLIENTS_COUNT,
            SURGERY_MATTERS_COUNT,
            MENTAL_HEALTH_TRIBUNAL_REFERENCE,
            IS_NRM_ADVICE);
    return toFieldMap(fields, claim);
  }
}
