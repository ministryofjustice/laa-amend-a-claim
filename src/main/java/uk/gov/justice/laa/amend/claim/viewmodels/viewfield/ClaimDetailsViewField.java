package uk.gov.justice.laa.amend.claim.viewmodels.viewfield;

import java.util.List;
import java.util.function.Function;
import lombok.Getter;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.MediationClaimDetailsViewField.Accessor;

@Getter
public enum ClaimDetailsViewField implements ClaimViewField<ClaimDetails> {
  // Common client fields
  INITIAL(new Accessor<>(ClaimDetails::getClientForename)),
  FORENAME(new Accessor<>(ClaimDetails::getClientForename)),
  SURNAME(new Accessor<>(ClaimDetails::getClientSurname)),
  GENDER(new Accessor<>(ClaimDetails::getClientGender)),
  ETHNICITY(new Accessor<>(ClaimDetails::getClientEthnicity), FieldOptions.ETHNICITY_CODE),
  DISABILITY(new Accessor<>(ClaimDetails::getClientDisability), FieldOptions.DISABILITY_CODE);

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
