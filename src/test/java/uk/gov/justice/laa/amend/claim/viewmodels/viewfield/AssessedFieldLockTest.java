package uk.gov.justice.laa.amend.claim.viewmodels.viewfield;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.amend.claim.models.enums.Amendability;

class AssessedFieldLockTest {

  @Test
  void locksOnlyTheFinancialFieldsSharedAcrossAreasOfLaw() {
    assertThat(lockedIn(ClaimDetailsViewField.values()))
        .containsExactlyInAnyOrder(
            ClaimDetailsViewField.FEE_CODE, ClaimDetailsViewField.CASE_START_DATE);
  }

  @Test
  void locksOnlyTheFinancialCrimeLowerFields() {
    assertThat(lockedIn(CrimeClaimDetailsViewField.values()))
        .containsExactlyInAnyOrder(
            CrimeClaimDetailsViewField.UNIQUE_FILE_NUMBER,
            CrimeClaimDetailsViewField.REPRESENTATION_ORDER_DATE,
            CrimeClaimDetailsViewField.CASE_CONCLUDED_DATE,
            CrimeClaimDetailsViewField.POLICE_STATION_COURT_PRISON_ID,
            CrimeClaimDetailsViewField.SCHEME_ID);
  }

  @Test
  void locksOnlyTheFinancialLegalHelpFields() {
    assertThat(lockedIn(CivilClaimDetailsViewField.values()))
        .containsExactlyInAnyOrder(CivilClaimDetailsViewField.CASE_CONCLUDED_CLAIMED_DATE);
  }

  @Test
  void locksNoMediationSpecificFields() {
    assertThat(lockedIn(MediationClaimDetailsViewField.values())).isEmpty();
  }

  @Test
  void locksCaseConcludedDateForCrimeLowerButNotMediation() {
    assertThat(CrimeClaimDetailsViewField.CASE_CONCLUDED_DATE.isEditable(true)).isFalse();
    assertThat(MediationClaimDetailsViewField.CASE_CONCLUDED_DATE.isEditable(true)).isTrue();

    assertThat(CrimeClaimDetailsViewField.CASE_CONCLUDED_DATE.name())
        .isEqualTo(MediationClaimDetailsViewField.CASE_CONCLUDED_DATE.name());
  }

  @Test
  void locksTheUniqueFileNumberForCrimeLowerButNotLegalHelp() {
    assertThat(CrimeClaimDetailsViewField.UNIQUE_FILE_NUMBER.isEditable(true)).isFalse();
    assertThat(CivilClaimDetailsViewField.UNIQUE_FILE_NUMBER.isEditable(true)).isTrue();

    assertThat(CrimeClaimDetailsViewField.UNIQUE_FILE_NUMBER.name())
        .isEqualTo(CivilClaimDetailsViewField.UNIQUE_FILE_NUMBER.name());
  }

  @Test
  void locksNothingWhileTheClaimHasNotBeenAssessed() {
    assertThat(ClaimDetailsViewField.FEE_CODE.isEditable(false)).isTrue();
    assertThat(CrimeClaimDetailsViewField.UNIQUE_FILE_NUMBER.isEditable(false)).isTrue();
    assertThat(CrimeClaimDetailsViewField.SCHEME_ID.isEditable(false)).isTrue();
  }

  @Test
  void leavesTheNonFinancialCaseFieldsEditableOnAnAssessedClaim() {
    assertThat(CrimeClaimDetailsViewField.STAGE_REACHED.isEditable(true)).isTrue();
    assertThat(CrimeClaimDetailsViewField.OUTCOME_FOR_CLIENT.isEditable(true)).isTrue();
    assertThat(CrimeClaimDetailsViewField.STANDARD_FEE_CATEGORY.isEditable(true)).isTrue();
    assertThat(MediationClaimDetailsViewField.CASE_CONCLUDED_DATE.isEditable(true)).isTrue();
    assertThat(CivilClaimDetailsViewField.MATTER_TYPE_CODE_1.isEditable(true)).isTrue();
    assertThat(CivilClaimDetailsViewField.STAGE_REACHED.isEditable(true)).isTrue();
  }

  @Test
  void stillHonoursFieldsThatAreNeverEditable() {
    assertThat(ClaimDetailsViewField.FIXED_FEE.getAmendability()).isEqualTo(Amendability.NEVER);
    assertThat(ClaimDetailsViewField.FIXED_FEE.isEditable()).isFalse();
    assertThat(ClaimDetailsViewField.FIXED_FEE.isEditable(false)).isFalse();
    assertThat(ClaimDetailsViewField.FIXED_FEE.isEditable(true)).isFalse();
  }

  @Test
  void treatsNoArgIsEditableAsTheUnassessedCase() {
    assertThat(ClaimDetailsViewField.FEE_CODE.isEditable()).isTrue();
    assertThat(CrimeClaimDetailsViewField.SCHEME_ID.isEditable()).isTrue();
    assertThat(CrimeClaimDetailsViewField.MAAT_ID.isEditable()).isTrue();
  }

  private static Stream<ClaimViewField<?>> lockedIn(ClaimViewField<?>[] fields) {
    return Arrays.stream(fields)
        .filter(field -> field.getAmendability() == Amendability.UNTIL_ASSESSED);
  }
}
