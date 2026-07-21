package uk.gov.justice.laa.amend.claim.viewmodels.viewfield;

public interface FieldOption {
  String value();

  default String messageKey() {
    return "claimCase.options." + messageKeyPrefix() + "." + ((Enum<?>) this).name();
  }

  default String messageKeyPrefix() {
    return getClass().getSimpleName().substring(0, 1).toLowerCase()
        + getClass().getSimpleName().substring(1);
  }
}
