package uk.gov.justice.laa.payments.amend.viewmodels.viewfield;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import uk.gov.justice.laa.payments.amend.models.enums.AdviceType;
import uk.gov.justice.laa.payments.amend.models.enums.AitHearingCentre;
import uk.gov.justice.laa.payments.amend.models.enums.CaseStage;
import uk.gov.justice.laa.payments.amend.models.enums.ClaExemptionCode;
import uk.gov.justice.laa.payments.amend.models.enums.ClientTypeCode;
import uk.gov.justice.laa.payments.amend.models.enums.DesignatedAccreditedRepresentative;
import uk.gov.justice.laa.payments.amend.models.enums.DisabilityCode;
import uk.gov.justice.laa.payments.amend.models.enums.EthnicityCode;
import uk.gov.justice.laa.payments.amend.models.enums.ExemptionCriteriaSatisfied;
import uk.gov.justice.laa.payments.amend.models.enums.GenderCode;
import uk.gov.justice.laa.payments.amend.models.enums.MeetingsAttended;
import uk.gov.justice.laa.payments.amend.models.enums.ReferralSource;
import uk.gov.justice.laa.payments.amend.models.enums.StageReached;
import uk.gov.justice.laa.payments.amend.models.enums.StandardFeeCategory;

public final class FieldOptions {

  public static final List<FieldOption> ADVICE_TYPE = from(AdviceType.values());
  public static final List<FieldOption> AIT_HEARING_CENTRE = from(AitHearingCentre.values());
  public static final List<FieldOption> CASE_STAGE = from(CaseStage.values());
  public static final List<FieldOption> CLA_EXEMPTION_CODE = from(ClaExemptionCode.values());
  public static final List<FieldOption> CLIENT_TYPE = from(ClientTypeCode.values());
  public static final List<FieldOption> CRIME_LOWER_OUTCOME = from(CrimeLowerOutcomeCode.values());
  public static final List<FieldOption> DESIGNATED_ACCREDITED_REPRESENTATIVE =
      from(DesignatedAccreditedRepresentative.values());
  public static final List<FieldOption> DISABILITY_CODE = from(DisabilityCode.values());
  public static final List<FieldOption> ETHNICITY_CODE = from(EthnicityCode.values());
  public static final List<FieldOption> EXEMPTION_CRITERIA_SATISFIED =
      from(ExemptionCriteriaSatisfied.values());
  public static final List<FieldOption> GENDER = from(GenderCode.values());
  public static final List<FieldOption> MEDIATION_OUTCOME = from(MediationOutcomeCode.values());
  public static final List<FieldOption> MEETINGS_ATTENDED = from(MeetingsAttended.values());
  public static final List<FieldOption> REFERRAL_SOURCE = from(ReferralSource.values());
  public static final List<FieldOption> CRIME_STAGE_REACHED = from(StageReached.values());
  public static final List<FieldOption> STANDARD_FEE_CATEGORY = from(StandardFeeCategory.values());

  private FieldOptions() {}

  public static Optional<FieldOption> find(List<FieldOption> options, Object value) {
    if (options == null || value == null) {
      return Optional.empty();
    }
    var stringValue = value.toString();
    return options.stream().filter(o -> o.value().equalsIgnoreCase(stringValue)).findFirst();
  }

  private static List<FieldOption> from(FieldOption[] options) {
    return Arrays.stream(options).toList();
  }
}
