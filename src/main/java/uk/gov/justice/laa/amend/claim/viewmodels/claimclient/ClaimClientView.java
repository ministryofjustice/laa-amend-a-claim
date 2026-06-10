package uk.gov.justice.laa.amend.claim.viewmodels.claimclient;

import java.io.Serializable;
import java.util.LinkedHashMap;

public interface ClaimClientView extends Serializable {

  /** A LinkedHashMap to ensure insertion order of rows is preserved. */
  LinkedHashMap<?, Object> client1Rows();

  LinkedHashMap<String, Object> client2Rows();
}
