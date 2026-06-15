package uk.gov.justice.laa.amend.claim.forms.amendment;

import java.util.LinkedHashMap;
import lombok.experimental.UtilityClass;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.CrimeClaimDetails;
import uk.gov.justice.laa.amend.claim.models.MediationClaimDetails;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField;

@UtilityClass
public class AmendFormFactory {

  public static AmendForm create(
      LinkedHashMap<? extends ClaimViewField<?>, Object> viewRows, Class<?> clazz) {
    if (clazz == CrimeClaimDetails.class) {
      return new CrimeAmendForm(viewRows);
    } else if (clazz == CivilClaimDetails.class) {
      return new CivilAmendForm(viewRows);
    } else if (clazz == MediationClaimDetails.class) {
      return new MediationAmendForm(viewRows);
    }
    throw new IllegalArgumentException("Unsupported claim details type");
  }
}
