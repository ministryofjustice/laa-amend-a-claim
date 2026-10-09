package uk.gov.justice.laa.payments.amend.models;

import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AmendmentError {

  private static final Set<String> CLAIM_MODIFIED_CODES =
      Set.of("CLAIM_VERSION_CONFLICT", "INVALID_VOIDED_CLAIM_NOT_AMENDABLE");

  private String code;
  private String message;
  private String fieldName;

  public static AmendmentError withMessage(String message) {
    return new AmendmentError(null, message, null);
  }

  public static boolean anyClaimModified(List<AmendmentError> errors) {
    return errors.stream().map(AmendmentError::getCode).anyMatch(CLAIM_MODIFIED_CODES::contains);
  }
}
