package uk.gov.justice.laa.amend.claim.viewmodels.claimcase;

import java.util.LinkedHashMap;

public interface ClaimCaseView {

  /** A LinkedHashMap to ensure insertion order of rows is preserved. */
  LinkedHashMap<String, Object> caseTypeRows();

  LinkedHashMap<String, Object> caseDetailsRows();
}
