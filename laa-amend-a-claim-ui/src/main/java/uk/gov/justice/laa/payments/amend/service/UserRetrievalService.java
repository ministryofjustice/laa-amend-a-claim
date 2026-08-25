package uk.gov.justice.laa.payments.amend.service;

import uk.gov.justice.laa.payments.amend.models.MicrosoftApiUser;

public interface UserRetrievalService {
  MicrosoftApiUser getUser(String userId);
}
