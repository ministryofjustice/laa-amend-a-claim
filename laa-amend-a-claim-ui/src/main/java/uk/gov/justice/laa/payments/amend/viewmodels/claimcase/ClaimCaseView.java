package uk.gov.justice.laa.payments.amend.viewmodels.claimcase;

import java.util.LinkedHashMap;
import uk.gov.justice.laa.payments.amend.models.ClaimDetails;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimViewField;

public interface ClaimCaseView<K extends ClaimViewField<? extends ClaimDetails>> {

  /** A LinkedHashMap to ensure insertion order of rows is preserved. */
  LinkedHashMap<K, Object> caseTypeRows();

  LinkedHashMap<K, Object> caseDetailsRows();
}
