package uk.gov.justice.laa.amend.claim.viewmodels.viewfield;

import java.util.List;
import java.util.function.Function;
import lombok.Getter;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.Claim;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.MediationClaimDetails;

@Getter
public enum ClaimDetailsViewField implements ClaimViewField<ClaimDetails> {
  // Common client fields
  INITIAL(new Accessor<>(ClaimDetails::getClientForename)),
  FORENAME(new Accessor<>(ClaimDetails::getClientForename)),
  SURNAME(new Accessor<>(ClaimDetails::getClientSurname)),
  GENDER(new Accessor<>(ClaimDetails::getClientGender), FieldOptions.GENDER),
  ETHNICITY(new Accessor<>(ClaimDetails::getClientEthnicity), FieldOptions.ETHNICITY_CODE),
  DISABILITY(new Accessor<>(ClaimDetails::getClientDisability), FieldOptions.DISABILITY_CODE),

  // Common case details fields
  CASE_REFERENCE_NUMBER(new Accessor<>(Claim::getCaseReferenceNumber)),
  CASE_START_DATE(new Accessor<>(Claim::getCaseStartDate), FieldType.DATE),
  UNIQUE_FILE_NUMBER(new Accessor<>(Claim::getUniqueFileNumber)),
  FEE_CODE(new Accessor<>(ClaimDetails::getFeeCode)),
  MATTER_TYPE_CODE_1(new Accessor<>(ClaimDetailsViewField::matterTypeCodeOne)),
  MATTER_TYPE_CODE_2(new Accessor<>(ClaimDetailsViewField::matterTypeCodeTwo)),
  STAGE_REACHED(new Accessor<>(ClaimDetails::getStageReached), FieldOptions.STAGE_REACHED),
  CASE_CONCLUDED_DATE(new Accessor<>(Claim::getCaseEndDate), FieldType.DATE),

  // Common cost fields
  FIXED_FEE(new Accessor<>(ClaimDetails::getFixedFee), FieldType.TEXT, false),
  PROFIT_COST(new Accessor<>(ClaimDetails::getNetProfitCost), FieldType.BIG_DECIMAL),
  DISBURSEMENTS(new Accessor<>(ClaimDetails::getNetDisbursementAmount), FieldType.BIG_DECIMAL),
  DISBURSEMENTS_VAT(new Accessor<>(ClaimDetails::getDisbursementVatAmount), FieldType.BIG_DECIMAL),
  VAT(new Accessor<>(ClaimDetails::getVatClaimed), FieldType.BOOLEAN);

  private final Accessor<?> accessor;
  private final FieldType type;
  private final boolean editable;
  private final List<FieldOption> options;

  ClaimDetailsViewField(Accessor<?> accessor) {
    this(accessor, FieldType.TEXT, true, List.of());
  }

  ClaimDetailsViewField(Accessor<?> accessor, FieldType type) {
    this(accessor, type, true, List.of());
  }

  ClaimDetailsViewField(Accessor<?> accessor, FieldType type, boolean editable) {
    this(accessor, type, editable, List.of());
  }

  ClaimDetailsViewField(Accessor<?> accessor, List<FieldOption> options) {
    this(accessor, FieldType.ENUM, true, options);
  }

  ClaimDetailsViewField(
      Accessor<?> accessor, FieldType type, boolean editable, List<FieldOption> options) {
    this.accessor = accessor;
    this.type = type;
    this.editable = editable;
    this.options = List.copyOf(options);
  }

  public record Accessor<T>(Function<ClaimDetails, T> getter)
      implements ClaimViewFieldAccessor<ClaimDetails, T> {}

  private static String matterTypeCodeOne(ClaimDetails claim) {
    if (claim instanceof CivilClaimDetails civilClaim) {
      return civilClaim.getMatterType1();
    }
    if (claim instanceof MediationClaimDetails mediationClaim) {
      return mediationClaim.getMatterType1();
    }
    return null;
  }

  private static String matterTypeCodeTwo(ClaimDetails claim) {
    if (claim instanceof CivilClaimDetails civilClaim) {
      return civilClaim.getMatterType2();
    }
    if (claim instanceof MediationClaimDetails mediationClaim) {
      return mediationClaim.getMatterType2();
    }
    return null;
  }
}
