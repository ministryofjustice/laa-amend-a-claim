package uk.gov.justice.laa.amend.claim.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.justice.laa.amend.claim.exceptions.NoClaimInSessionException;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimStatus;

public class SessionUtilsTest {

  private static final UUID SUBMISSION_ID = UUID.randomUUID();
  private static final UUID CLAIM_ID = UUID.randomUUID();

  @Nested
  class GetClaimTests {
    @Test
    void getClaim() {
      var claim = MockClaimsFunctions.createMockCrimeClaim();
      var session = new MockHttpSession();
      session.setAttribute(CLAIM_ID.toString(), claim);

      var result = SessionUtils.getClaim(session, SUBMISSION_ID, CLAIM_ID);

      assertEquals(claim, result);
    }

    @Test
    void nullSessionThrowsException() {
      assertThrows(
          NoClaimInSessionException.class,
          () -> SessionUtils.getClaim(null, SUBMISSION_ID, CLAIM_ID));
    }

    @Test
    void missingClaimThrowsException() {
      assertThrows(
          NoClaimInSessionException.class,
          () -> SessionUtils.getClaim(new MockHttpSession(), SUBMISSION_ID, CLAIM_ID));
    }
  }

  @Nested
  class GetValidClaimTests {
    @Test
    void getValidClaim() {
      var claim = MockClaimsFunctions.createMockCrimeClaim();
      claim.setStatus(ClaimStatus.VALID);
      var session = new MockHttpSession();
      session.setAttribute(CLAIM_ID.toString(), claim);

      var result = SessionUtils.getValidClaim(session, SUBMISSION_ID, CLAIM_ID);

      assertEquals(claim, result);
    }

    @ParameterizedTest
    @EnumSource(
        value = ClaimStatus.class,
        names = {"VALID"},
        mode = EnumSource.Mode.EXCLUDE)
    void nonValidClaimThrowsException(ClaimStatus status) {
      var claim = MockClaimsFunctions.createMockCrimeClaim();
      claim.setStatus(status);
      var session = new MockHttpSession();
      session.setAttribute(CLAIM_ID.toString(), claim);

      var exception =
          assertThrows(
              ResponseStatusException.class,
              () -> SessionUtils.getValidClaim(session, SUBMISSION_ID, CLAIM_ID));

      assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }
  }

  @Nested
  class GetValidEscapeCaseClaimTests {
    @Test
    void getValidEscapeCaseClaim() {
      var claim = MockClaimsFunctions.createMockCrimeClaim();
      claim.setStatus(ClaimStatus.VALID);
      claim.setEscaped(true);
      var session = new MockHttpSession();
      session.setAttribute(CLAIM_ID.toString(), claim);

      var result = SessionUtils.getValidEscapeCaseClaim(session, SUBMISSION_ID, CLAIM_ID);

      assertEquals(claim, result);
    }

    @Test
    void nonValidEscapeCaseClaimThrowsException() {
      var claim = MockClaimsFunctions.createMockCrimeClaim();
      claim.setStatus(ClaimStatus.VALID);
      claim.setEscaped(false);
      var session = new MockHttpSession();
      session.setAttribute(CLAIM_ID.toString(), claim);

      var exception =
          assertThrows(
              ResponseStatusException.class,
              () -> SessionUtils.getValidEscapeCaseClaim(session, SUBMISSION_ID, CLAIM_ID));

      assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }
  }
}
