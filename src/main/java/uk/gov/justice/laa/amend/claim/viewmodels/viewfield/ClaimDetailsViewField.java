package uk.gov.justice.laa.amend.claim.viewmodels.viewfield;

import java.util.List;
import java.util.function.Function;
import lombok.Getter;
import uk.gov.justice.laa.amend.claim.models.Claim;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;

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
  STAGE_REACHED(new Accessor<>(ClaimDetails::getStageReached), FieldOptions.STAGE_REACHED),
  CASE_CONCLUDED_DATE(new Accessor<>(Claim::getCaseEndDate), FieldType.DATE);

  private final Accessor<?> accessor;
  private final FieldType type;
  private final List<FieldOption> options;

  ClaimDetailsViewField(Accessor<?> accessor) {
    this(accessor, FieldType.TEXT);
  }

  ClaimDetailsViewField(Accessor<?> accessor, FieldType type) {
    this(accessor, type, List.of());
  }

  ClaimDetailsViewField(Accessor<?> accessor, List<FieldOption> options) {
    this(accessor, FieldType.ENUM, options);
  }

  ClaimDetailsViewField(Accessor<?> accessor, FieldType type, List<FieldOption> options) {
    this.accessor = accessor;
    this.type = type;
    this.options = List.copyOf(options);
  }

  public record Accessor<T>(Function<ClaimDetails, T> getter)
      implements ClaimViewFieldAccessor<ClaimDetails, T> {}
}
