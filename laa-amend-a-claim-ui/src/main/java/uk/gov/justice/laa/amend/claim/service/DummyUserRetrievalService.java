package uk.gov.justice.laa.amend.claim.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.amend.claim.models.MicrosoftApiUser;

@Profile({"local", "ephemeral", "e2e"})
@Service
public class DummyUserRetrievalService implements UserRetrievalService {
  @Override
  public MicrosoftApiUser getUser(String userId) {
    return new MicrosoftApiUser(userId, "Bloggs, Joe", "Joe", "Bloggs");
  }
}
