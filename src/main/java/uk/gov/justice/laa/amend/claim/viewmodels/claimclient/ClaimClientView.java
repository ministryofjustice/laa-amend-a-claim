package uk.gov.justice.laa.amend.claim.viewmodels.claimclient;

import java.util.LinkedHashMap;

public interface ClaimClientView {

  /** A LinkedHashMap to ensure insertion order of rows is preserved. */
  LinkedHashMap<String, Object> client1Rows();

  LinkedHashMap<String, Object> client2Rows();
}
