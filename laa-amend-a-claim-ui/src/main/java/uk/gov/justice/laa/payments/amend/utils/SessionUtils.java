package uk.gov.justice.laa.payments.amend.utils;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.UUID;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimStatus;
import uk.gov.justice.laa.payments.amend.exceptions.NoClaimInSessionException;
import uk.gov.justice.laa.payments.amend.forms.amendments.AmendmentForms;
import uk.gov.justice.laa.payments.amend.models.Claim;
import uk.gov.justice.laa.payments.amend.models.ClaimDetails;

@UtilityClass
public class SessionUtils {

  public static final String CLAIM_KEY = "%s";
  public static final String AMENDMENTS_KEY = "amendments:%s";
  public static final String AMENDMENT_ERRORS_KEY = "amendmentErrors:%s";

  public static void saveClaim(HttpSession session, UUID claimId, Claim claim) {
    var key = CLAIM_KEY.formatted(claimId.toString());
    session.setAttribute(key, claim);
  }

  public static ClaimDetails getClaim(HttpSession session, UUID submissionId, UUID claimId) {
    if (session == null) {
      throw new NoClaimInSessionException(submissionId, claimId, "Session not found");
    }

    var claim = (ClaimDetails) session.getAttribute(CLAIM_KEY.formatted(claimId.toString()));

    if (claim == null) {
      throw new NoClaimInSessionException(submissionId, claimId, "Claim not found");
    }

    return claim;
  }

  public static ClaimDetails getValidClaim(HttpSession session, UUID submissionId, UUID claimId) {
    var claim = getClaim(session, submissionId, claimId);

    if (claim.getStatus() != ClaimStatus.VALID) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Claim status is not VALID");
    }

    return claim;
  }

  public static ClaimDetails getValidAssessableClaim(
      HttpSession session, UUID submissionId, UUID claimId) {
    var claim = getValidClaim(session, submissionId, claimId);
    if (!claim.isEscapedCase() && !claim.isStageDisbursement()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Claim is not assessable");
    }
    return claim;
  }

  public static void removeClaim(HttpSession session, UUID claimId) {
    var key = CLAIM_KEY.formatted(claimId.toString());
    session.removeAttribute(key);
  }

  public static void saveAmendmentForms(HttpSession session, UUID claimId, AmendmentForms forms) {
    var key = AMENDMENTS_KEY.formatted(claimId.toString());
    session.setAttribute(key, forms);
  }

  public static AmendmentForms getAmendmentForms(HttpSession session, UUID claimId) {
    var key = AMENDMENTS_KEY.formatted(claimId.toString());
    var form = session.getAttribute(key);

    if (form == null) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, "Amendment forms not found: %s".formatted(key));
    }

    return (AmendmentForms) form;
  }

  public static void removeAmendmentForms(HttpSession session, UUID claimId) {
    var key = AMENDMENTS_KEY.formatted(claimId.toString());
    session.removeAttribute(key);
  }

  public static void removeAllForClaim(HttpSession session, UUID claimId) {
    removeClaim(session, claimId);
    removeAmendmentForms(session, claimId);
  }

  public static void saveAmendmentErrors(HttpSession session, UUID claimId, List<String> errors) {
    var key = AMENDMENT_ERRORS_KEY.formatted(claimId.toString());
    session.setAttribute(key, errors);
  }

  @SuppressWarnings("unchecked")
  public static List<String> getAmendmentErrors(HttpSession session, UUID claimId) {
    var key = AMENDMENT_ERRORS_KEY.formatted(claimId.toString());
    var errors = session.getAttribute(key);
    return errors == null ? List.of() : (List<String>) errors;
  }

  public static void removeAmendmentErrors(HttpSession session, UUID claimId) {
    var key = AMENDMENT_ERRORS_KEY.formatted(claimId.toString());
    session.removeAttribute(key);
  }
}
