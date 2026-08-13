package uk.gov.justice.laa.amend.claim.viewmodels.viewfield;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import lombok.Getter;
import uk.gov.justice.laa.amend.claim.models.Claim;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.enums.Amendability;
import uk.gov.justice.laa.amend.claim.models.enums.FieldType;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimPatch;

@Getter
public enum ClaimDetailsViewField implements ClaimViewField<ClaimDetails> {
  // Common client fields
  INITIAL(
      FieldType.TEXT,
      String.class,
      ClaimDetails::getClientForename,
      ClaimPatch.Builder::clientForename),
  FORENAME(
      FieldType.TEXT,
      String.class,
      ClaimDetails::getClientForename,
      ClaimPatch.Builder::clientForename),
  SURNAME(
      FieldType.TEXT,
      String.class,
      ClaimDetails::getClientSurname,
      ClaimPatch.Builder::clientSurname),
  GENDER(
      FieldType.ENUM,
      String.class,
      ClaimDetails::getClientGender,
      ClaimPatch.Builder::genderCode,
      FieldOptions.GENDER),
  ETHNICITY(
      FieldType.ENUM,
      String.class,
      ClaimDetails::getClientEthnicity,
      ClaimPatch.Builder::ethnicityCode,
      FieldOptions.ETHNICITY_CODE),
  DISABILITY(
      FieldType.ENUM,
      String.class,
      ClaimDetails::getClientDisability,
      ClaimPatch.Builder::disabilityCode,
      FieldOptions.DISABILITY_CODE),

  // Common case details fields
  CASE_REFERENCE_NUMBER(
      FieldType.TEXT,
      String.class,
      Claim::getCaseReferenceNumber,
      ClaimPatch.Builder::caseReferenceNumber),
  CASE_START_DATE(
      FieldType.DATE,
      String.class,
      Claim::getCaseStartDate,
      ClaimPatch.Builder::caseStartDate,
      Amendability.UNTIL_ASSESSED),
  FEE_CODE(
      FieldType.TEXT,
      String.class,
      ClaimDetails::getFeeCode,
      ClaimPatch.Builder::feeCode,
      Amendability.UNTIL_ASSESSED),

  // Common cost fields
  FIXED_FEE(
      FieldType.TEXT, Object.class, ClaimDetails::getFixedFee, (b, v) -> b, Amendability.NEVER),
  PROFIT_COST(
      FieldType.BIG_DECIMAL,
      BigDecimal.class,
      ClaimDetails::getNetProfitCost,
      ClaimPatch.Builder::netProfitCostsAmount),
  DISBURSEMENTS(
      FieldType.BIG_DECIMAL,
      BigDecimal.class,
      ClaimDetails::getNetDisbursementAmount,
      ClaimPatch.Builder::netDisbursementAmount),
  DISBURSEMENTS_VAT(
      FieldType.BIG_DECIMAL,
      BigDecimal.class,
      ClaimDetails::getDisbursementVatAmount,
      ClaimPatch.Builder::disbursementsVatAmount),
  VAT(
      FieldType.BOOLEAN,
      Boolean.class,
      ClaimDetails::getVatClaimed,
      ClaimPatch.Builder::isVatApplicable);

  private final ClaimDetailsViewFieldGetter<?> getter;
  private final FieldType fieldType;
  private final ClaimViewFieldPatcher<?> patcher;
  private final Amendability amendability;
  private final List<FieldOption> options;

  <T> ClaimDetailsViewField(
      FieldType fieldType,
      Class<T> patchType,
      Function<ClaimDetails, ?> getter,
      BiFunction<ClaimPatch.Builder, T, ClaimPatch.Builder> patcher) {
    this(fieldType, patchType, getter, patcher, List.of(), Amendability.ALWAYS);
  }

  <T> ClaimDetailsViewField(
      FieldType fieldType,
      Class<T> patchType,
      Function<ClaimDetails, ?> getter,
      BiFunction<ClaimPatch.Builder, T, ClaimPatch.Builder> patcher,
      List<FieldOption> options) {
    this(fieldType, patchType, getter, patcher, options, Amendability.ALWAYS);
  }

  <T> ClaimDetailsViewField(
      FieldType fieldType,
      Class<T> patchType,
      Function<ClaimDetails, ?> getter,
      BiFunction<ClaimPatch.Builder, T, ClaimPatch.Builder> patcher,
      Amendability amendability) {
    this(fieldType, patchType, getter, patcher, List.of(), amendability);
  }

  <T> ClaimDetailsViewField(
      FieldType fieldType,
      Class<T> patchType,
      Function<ClaimDetails, ?> getter,
      BiFunction<ClaimPatch.Builder, T, ClaimPatch.Builder> patcher,
      List<FieldOption> options,
      Amendability amendability) {
    this.getter = new ClaimDetailsViewFieldGetter<>(getter);
    this.fieldType = fieldType;
    this.patcher = new ClaimViewFieldPatcher<>(patchType, patcher);
    this.options = List.copyOf(options);
    this.amendability = amendability;
  }

  public record ClaimDetailsViewFieldGetter<T>(Function<ClaimDetails, T> getter)
      implements ClaimViewFieldGetter<ClaimDetails, T> {}
}
