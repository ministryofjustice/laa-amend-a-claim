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
import uk.gov.justice.laa.amend.claim.viewmodels.ThymeleafMessage;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimPatch;

@Getter
public enum ClaimDetailsViewField implements ClaimViewField<ClaimDetails> {
  // Claim overview only fields
  CLIENT_NAME(FieldType.TEXT, ClaimDetailsViewField::getClientName),
  PROVIDER_NAME(FieldType.TEXT, ClaimDetailsViewField::getProviderName),
  OFFICE_CODE(FieldType.TEXT, ClaimDetails::getOfficeCode),
  SUBMITTED_DATE(FieldType.DATE, ClaimDetails::getSubmittedDate),
  AREA_OF_LAW(FieldType.TEXT, ClaimDetailsViewField::getAreaOfLaw),
  CATEGORY_OF_LAW(FieldType.TEXT, ClaimDetails::getCategoryOfLaw),
  FEE_CODE_DESCRIPTION(FieldType.TEXT, ClaimDetails::getFeeCodeDescription),
  ESCAPED(FieldType.BOOLEAN, ClaimDetails::getEscaped),
  VAT_REQUESTED(FieldType.BOOLEAN, ClaimDetails::getVatApplicable),
  TOTAL(FieldType.TEXT, ClaimDetails::getTotalAmount),

  // Common client fields
  INITIAL(
      FieldType.TEXT,
      String.class,
      ClaimDetails::getClientForename,
      ClaimPatch.Builder::clientForename,
      "client.clientForename"),
  FORENAME(
      FieldType.TEXT,
      String.class,
      ClaimDetails::getClientForename,
      ClaimPatch.Builder::clientForename,
      "client.clientForename"),
  SURNAME(
      FieldType.TEXT,
      String.class,
      ClaimDetails::getClientSurname,
      ClaimPatch.Builder::clientSurname,
      "client.clientSurname"),
  GENDER(
      FieldType.ENUM,
      String.class,
      ClaimDetails::getClientGender,
      ClaimPatch.Builder::genderCode,
      FieldOptions.GENDER,
      "client.genderCode"),
  ETHNICITY(
      FieldType.ENUM,
      String.class,
      ClaimDetails::getClientEthnicity,
      ClaimPatch.Builder::ethnicityCode,
      FieldOptions.ETHNICITY_CODE,
      "client.ethnicityCode"),
  DISABILITY(
      FieldType.ENUM,
      String.class,
      ClaimDetails::getClientDisability,
      ClaimPatch.Builder::disabilityCode,
      FieldOptions.DISABILITY_CODE,
      "client.disabilityCode"),

  // Common case details fields
  CASE_REFERENCE_NUMBER(
      FieldType.TEXT,
      String.class,
      Claim::getCaseReferenceNumber,
      ClaimPatch.Builder::caseReferenceNumber,
      "claim.caseReferenceNumber"),
  CASE_START_DATE(
      FieldType.DATE,
      String.class,
      Claim::getCaseStartDate,
      ClaimPatch.Builder::caseStartDate,
      Amendability.UNTIL_ASSESSED,
      "claim.caseStartDate"),
  UNIQUE_FILE_NUMBER(
      FieldType.TEXT,
      String.class,
      Claim::getUniqueFileNumber,
      ClaimPatch.Builder::uniqueFileNumber,
      "claim.uniqueFileNumber"),
  CASE_CONCLUDED_DATE(
      FieldType.DATE,
      String.class,
      Claim::getCaseEndDate,
      ClaimPatch.Builder::caseConcludedDate,
      "claim.caseConcludedDate"),
  FEE_CODE(
      FieldType.TEXT,
      String.class,
      ClaimDetails::getFeeCode,
      ClaimPatch.Builder::feeCode,
      Amendability.UNTIL_ASSESSED,
      "claim.feeCode"),

  // Common cost fields
  FIXED_FEE(
      FieldType.TEXT,
      Object.class,
      ClaimDetails::getFixedFee,
      (b, v) -> b,
      Amendability.NEVER,
      "fee.fixedFeeAmount"),
  PROFIT_COST(
      FieldType.BIG_DECIMAL,
      BigDecimal.class,
      ClaimDetails::getNetProfitCost,
      ClaimPatch.Builder::netProfitCostsAmount,
      "claimSummaryFee.netProfitCostsAmount",
      "fee.netProfitCostsAmount"),
  DISBURSEMENTS(
      FieldType.BIG_DECIMAL,
      BigDecimal.class,
      ClaimDetails::getNetDisbursementAmount,
      ClaimPatch.Builder::netDisbursementAmount,
      "claimSummaryFee.netDisbursementAmount",
      "fee.disbursementAmount"),
  DISBURSEMENTS_VAT(
      FieldType.BIG_DECIMAL,
      BigDecimal.class,
      ClaimDetails::getDisbursementVatAmount,
      ClaimPatch.Builder::disbursementsVatAmount,
      "claimSummaryFee.disbursementsVatAmount",
      "fee.disbursementVatAmount"),
  VAT(
      FieldType.BOOLEAN,
      Boolean.class,
      ClaimDetails::getVatClaimed,
      ClaimPatch.Builder::isVatApplicable,
      "claimSummaryFee.isVatApplicable",
      "fee.vatIndicator");

  private final ClaimDetailsViewFieldGetter<?> getter;
  private final String claimsApiFieldName;
  private final String feeApiFieldName;
  private final FieldType fieldType;
  private final ClaimViewFieldPatcher<?> patcher;
  private final Amendability amendability;
  private final List<FieldOption> options;

  <T> ClaimDetailsViewField(FieldType fieldType, Function<ClaimDetails, ?> getter) {
    this(fieldType, Object.class, getter, (b, v) -> b, List.of(), Amendability.NEVER, "", null);
  }

  <T> ClaimDetailsViewField(
      FieldType fieldType,
      Class<T> patchType,
      Function<ClaimDetails, ?> getter,
      BiFunction<ClaimPatch.Builder, T, ClaimPatch.Builder> patcher,
      String claimsApiFieldName) {
    this(
        fieldType,
        patchType,
        getter,
        patcher,
        List.of(),
        Amendability.ALWAYS,
        claimsApiFieldName,
        null);
  }

  <T> ClaimDetailsViewField(
      FieldType fieldType,
      Class<T> patchType,
      Function<ClaimDetails, ?> getter,
      BiFunction<ClaimPatch.Builder, T, ClaimPatch.Builder> patcher,
      String claimsApiFieldName,
      String feeApiFieldName) {
    this(
        fieldType,
        patchType,
        getter,
        patcher,
        List.of(),
        Amendability.ALWAYS,
        claimsApiFieldName,
        feeApiFieldName);
  }

  <T> ClaimDetailsViewField(
      FieldType fieldType,
      Class<T> patchType,
      Function<ClaimDetails, ?> getter,
      BiFunction<ClaimPatch.Builder, T, ClaimPatch.Builder> patcher,
      List<FieldOption> options,
      String claimsApiFieldName) {
    this(
        fieldType,
        patchType,
        getter,
        patcher,
        options,
        Amendability.ALWAYS,
        claimsApiFieldName,
        null);
  }

  <T> ClaimDetailsViewField(
      FieldType fieldType,
      Class<T> patchType,
      Function<ClaimDetails, ?> getter,
      BiFunction<ClaimPatch.Builder, T, ClaimPatch.Builder> patcher,
      Amendability amendability,
      String claimsApiFieldName) {
    this(fieldType, patchType, getter, patcher, List.of(), amendability, claimsApiFieldName, null);
  }

  <T> ClaimDetailsViewField(
      FieldType fieldType,
      Class<T> patchType,
      Function<ClaimDetails, ?> getter,
      BiFunction<ClaimPatch.Builder, T, ClaimPatch.Builder> patcher,
      List<FieldOption> options,
      Amendability amendability,
      String claimsApiFieldName,
      String feeApiFieldName) {
    this.getter = new ClaimDetailsViewFieldGetter<>(getter);
    this.claimsApiFieldName = claimsApiFieldName;
    this.feeApiFieldName = feeApiFieldName;
    this.fieldType = fieldType;
    this.patcher = new ClaimViewFieldPatcher<>(patchType, patcher);
    this.options = List.copyOf(options);
    this.amendability = amendability;
  }

  public record ClaimDetailsViewFieldGetter<T>(Function<ClaimDetails, T> getter)
      implements ClaimViewFieldGetter<ClaimDetails, T> {}

  private static String getClientName(ClaimDetails claim) {
    return "%s %s".formatted(claim.getClientForename(), claim.getClientSurname()).trim();
  }

  private static Object getProviderName(ClaimDetails claim) {
    return claim.getProviderName() == null
        ? new ThymeleafMessage("provider.firmName.notAvailable")
        : claim.getProviderName();
  }

  private static Object getAreaOfLaw(ClaimDetails claim) {
    return claim.getAreaOfLaw() != null
        ? new ThymeleafMessage(claim.getAreaOfLaw().getMessageKey())
        : null;
  }
}
