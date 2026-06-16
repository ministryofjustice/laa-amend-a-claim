package uk.gov.justice.laa.amend.claim.viewmodels.claimclient;

import java.util.LinkedHashMap;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField;

public interface ClaimClientView<K extends ClaimViewField<? extends ClaimDetails>> {

  /** A LinkedHashMap to ensure insertion order of rows is preserved. */
  LinkedHashMap<K, Object> client1Rows();

  LinkedHashMap<K, Object> client2Rows();

  Class<?> claimDetailsType();
}
