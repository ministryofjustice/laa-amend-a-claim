package uk.gov.justice.laa.payments.amend.models;

import java.util.Arrays;
import java.util.List;
import lombok.Builder;

@Builder
public record ClientInsert(
    String id,
    String claimId,
    String clientForename,
    String clientSurname,
    String clientDateOfBirth,
    String uniqueClientNumber,
    String clientPostcode,
    String genderCode,
    String ethnicityCode,
    String disabilityCode,
    Boolean isLegallyAided,
    String userId)
    implements Insert {

  @Override
  public String table() {
    return "client";
  }

  @Override
  public List<Object> parameters() {
    return Arrays.asList(
        id,
        claimId,
        clientForename,
        clientSurname,
        clientDateOfBirth,
        uniqueClientNumber,
        clientPostcode,
        genderCode,
        ethnicityCode,
        disabilityCode,
        isLegallyAided,
        userId,
        userId);
  }
}
