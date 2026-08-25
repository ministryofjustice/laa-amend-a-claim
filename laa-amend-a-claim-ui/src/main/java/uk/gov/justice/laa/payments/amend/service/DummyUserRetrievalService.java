package uk.gov.justice.laa.payments.amend.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.payments.amend.models.MicrosoftApiUser;

@Profile({"local", "ephemeral", "e2e"})
@Service
public class DummyUserRetrievalService implements UserRetrievalService {
  @Override
  public MicrosoftApiUser getUser(String userId) {
    return new MicrosoftApiUser(userId, "Bloggs, Joe", "Joe", "Bloggs");
  }
}
