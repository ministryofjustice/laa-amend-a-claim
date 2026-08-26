package uk.gov.justice.laa.payments.amend.viewmodels.claimclient;

import java.util.LinkedHashMap;
import uk.gov.justice.laa.payments.amend.models.ClaimDetails;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimViewField;

public interface ClaimClientView<K extends ClaimViewField<? extends ClaimDetails>> {

  /** A LinkedHashMap to ensure insertion order of rows is preserved. */
  LinkedHashMap<K, Object> client1Rows();

  LinkedHashMap<K, Object> client2Rows();
}
