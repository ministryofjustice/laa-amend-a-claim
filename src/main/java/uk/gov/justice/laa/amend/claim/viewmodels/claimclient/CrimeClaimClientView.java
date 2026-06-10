package uk.gov.justice.laa.amend.claim.viewmodels.claimclient;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import java.util.LinkedHashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;
import uk.gov.justice.laa.amend.claim.models.Claim;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.CrimeClaimDetails;
import uk.gov.justice.laa.amend.claim.models.amendment.ClaimDetailsField;

public record CrimeClaimClientView(
    LinkedHashMap<ClaimDetailsField, Object> client1Rows, LinkedHashMap<String, Object> client2Rows)
    implements ClaimClientView {

  public CrimeClaimClientView(CrimeClaimDetails claim) {
    this(createRows(claim), new LinkedHashMap<>());
  }

  private static LinkedHashMap<ClaimDetailsField, Object> createRows(CrimeClaimDetails claim) {
    Stream<ClaimDetailsField> fields =
        Stream.of(
            ClaimDetailsField.INITIAL,
            ClaimDetailsField.SURNAME,
            ClaimDetailsField.GENDER,
            ClaimDetailsField.ETHNICITY,
            ClaimDetailsField.DISABILITY);

    return fields.collect(
        toMap(
            identity(),
            field -> field.getAccessor().getter().apply(claim),
            (a, b) -> b,
            LinkedHashMap::new));
  }

  public record AmendmentField<T>(
      Function<? extends ClaimDetails, T> getter, BiConsumer<? extends ClaimDetails, T> setter) {}

  public record CrimeAmendmentField<T>(
      Function<CrimeClaimDetails, T> getter, BiConsumer<CrimeClaimDetails, T> setter) {}

  public enum ClaimFields {
    INITIAL(
        "initial",
        "initialFromValidationPackage",
        new AmendmentField<>(Claim::getClientForename, ClaimDetails::setClientForename));

    private final String fieldName;
    private final String validationFieldName;
    private final AmendmentField<?> amendmentField;

    ClaimFields(String fieldName, String validationFieldName, AmendmentField<?> amendmentField) {
      this.fieldName = fieldName;
      this.validationFieldName = validationFieldName;
      this.amendmentField = amendmentField;
    }
  }

  public enum CrimeClaimFields {
    MATTER_TYPE_CODE(
        "matterTypeCode",
        "matterTypeCode",
        new CrimeAmendmentField<>(
            CrimeClaimDetails::getMatterTypeCode, CrimeClaimDetails::setMatterTypeCode));

    private final String fieldName;
    private final String validationFieldName;
    private final CrimeAmendmentField<?> amendmentField;

    CrimeClaimFields(
        String fieldName, String validationFieldName, CrimeAmendmentField<?> amendmentField) {
      this.fieldName = fieldName;
      this.validationFieldName = validationFieldName;
      this.amendmentField = amendmentField;
    }
  }
}
