package uk.gov.justice.laa.amend.claim.viewmodels.claimclient;

import java.io.Serializable;
import java.util.LinkedHashMap;
import uk.gov.justice.laa.amend.claim.models.amendment.ClaimDetailsField;

public interface ClaimClientView extends Serializable {

  /** A LinkedHashMap to ensure insertion order of rows is preserved. */
  LinkedHashMap<ClaimDetailsField, Object> client1Rows();

  LinkedHashMap<String, Object> client2Rows();
}
