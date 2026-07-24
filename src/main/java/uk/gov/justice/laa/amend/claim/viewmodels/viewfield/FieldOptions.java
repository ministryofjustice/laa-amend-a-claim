package uk.gov.justice.laa.amend.claim.viewmodels.viewfield;

import java.util.Arrays;
import java.util.List;

public final class FieldOptions {

  public static final List<FieldOption> ADVICE_TYPE = from(AdviceType.values());
  public static final List<FieldOption> AIT_HEARING_CENTRE = from(AitHearingCentre.values());
  public static final List<FieldOption> CASE_STAGE = from(CaseStage.values());
  public static final List<FieldOption> DESIGNATED_ACCREDITED_REPRESENTATIVE =
      from(DesignatedAccreditedRepresentative.values());
  public static final List<FieldOption> EXEMPTION_CRITERIA_SATISFIED =
      from(ExemptionCriteriaSatisfied.values());
  public static final List<FieldOption> MEETINGS_ATTENDED = from(MeetingsAttended.values());
  public static final List<FieldOption> OUTCOME = from(OutcomeCode.values());
  public static final List<FieldOption> REFERRAL_SOURCE = from(ReferralSource.values());
  public static final List<FieldOption> STAGE_REACHED = from(StageReached.values());
  public static final List<FieldOption> STANDARD_FEE_CATEGORY = from(StandardFeeCategory.values());

  private FieldOptions() {}

  private static List<FieldOption> from(FieldOption[] options) {
    return Arrays.stream(options).toList();
  }
}
