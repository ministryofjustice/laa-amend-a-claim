package uk.gov.justice.laa.amend.claim.service;

import uk.gov.justice.laa.amend.claim.models.MicrosoftApiUser;

public interface UserRetrievalService {
  MicrosoftApiUser getUser(String userId);
}
