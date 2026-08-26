package uk.gov.justice.laa.payments.amend.forms.amendments.validators;

import java.math.BigDecimal;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import uk.gov.justice.laa.payments.amend.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.payments.amend.models.ClaimDetails;
import uk.gov.justice.laa.payments.amend.models.enums.AreaOfLaw;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimDetailsViewField;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimViewField;

@Slf4j
@Component
public class DisbursementVatAmountValidator implements FieldSpecificAmendmentValidator {

  public static final String ERROR_CODE = "amendmentForm.dates.disbursementVatExceeded";
  public static final double MAX_LEGAL_HELP_VAT_AMOUNT = 99999.99;
  public static final double MAX_CRIME_LOWER_VAT_AMOUNT = 999999.99;
  public static final double MAX_MEDIATION_VAT_AMOUNT = 9999999.99;

  @Override
  public boolean appliesTo(ClaimViewField<?> field) {
    return ClaimDetailsViewField.DISBURSEMENTS_VAT.equals(field);
  }

  @Override
  public void validate(
      ClaimDetails claim, ClaimViewField<?> field, AmendmentForm form, Errors errors) {
    Optional<BigDecimal> disbursementsVatValue = parseDisbursementsVat(form, field);
    var areaOfLaw = claim.getAreaOfLaw();

    if (disbursementsVatValue.isEmpty() || areaOfLaw == null) {
      return;
    }

    BigDecimal maxAllowed = getMaxDisbursementVatAllowed(areaOfLaw);

    if (disbursementsVatValue.get().compareTo(maxAllowed) > 0) {
      addUniqueFieldError(
          field,
          ERROR_CODE,
          new Object[] {
            new DefaultMessageSourceResolvable(new String[] {areaOfLaw.getMessageKey()}), maxAllowed
          },
          errors);
    }
  }

  private static @NonNull BigDecimal getMaxDisbursementVatAllowed(AreaOfLaw areaOfLaw) {
    return switch (areaOfLaw) {
      case LEGAL_HELP -> BigDecimal.valueOf(MAX_LEGAL_HELP_VAT_AMOUNT);
      case CRIME_LOWER -> BigDecimal.valueOf(MAX_CRIME_LOWER_VAT_AMOUNT);
      case MEDIATION -> BigDecimal.valueOf(MAX_MEDIATION_VAT_AMOUNT);
    };
  }

  private Optional<BigDecimal> parseDisbursementsVat(AmendmentForm form, ClaimViewField<?> field) {
    try {
      return Optional.ofNullable(form.getBigDecimalValue(field.name()));
    } catch (IllegalArgumentException e) {
      log.debug(
          "Disbursements VAT amount is malformed, validation error will have been caught in "
              + "BigDecimalAmendmentFieldValidator so can be safely ignored here.");
      return Optional.empty();
    }
  }
}
