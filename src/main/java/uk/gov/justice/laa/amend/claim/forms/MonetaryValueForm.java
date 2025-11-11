package uk.gov.justice.laa.amend.claim.forms;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.PROVIDER_ACCOUNT_NUMBER_REQUIRED_ERROR;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MonetaryValueForm {

    @NotNull(message = "{monetaryValue.error.required}")
    @DecimalMin(value = "0.00", message = "{monetaryValue.error.min}")
    @DecimalMax(value = "1000000.00", inclusive = false, message = "{monetaryValue.error.max}")
    @Digits(integer = 10, fraction = 2, message = "${monetaryValue.error.invalid}")
    private BigDecimal value;
}
