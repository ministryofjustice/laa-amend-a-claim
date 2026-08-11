package uk.gov.justice.laa.amend.claim.utils;

import jakarta.servlet.http.HttpSession;
import java.util.Set;
import java.util.UUID;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.justice.laa.amend.claim.exceptions.NoClaimInSessionException;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForms;
import uk.gov.justice.laa.amend.claim.models.Claim;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimStatus;

@UtilityClass
public class SessionUtils {

  public static final String CLAIM_KEY = "%s";
  public static final String AMENDMENTS_KEY = "amendments:%s";
  public static final String AMENDED_FIELDS_KEY = "amendedFields:%s";

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

  public static void saveAmendedFields(HttpSession session, UUID claimId, Set<String> fields) {
    var key = AMENDED_FIELDS_KEY.formatted(claimId.toString());
    session.setAttribute(key, fields);
  }

  @SuppressWarnings("unchecked")
  public static Set<String> getAmendedFields(HttpSession session, UUID claimId) {
    var key = AMENDED_FIELDS_KEY.formatted(claimId.toString());
    var fields = session.getAttribute(key);

    if (fields == null) {
      return Set.of();
    }

    return (Set<String>) fields;
  }

  public static void removeAmendedFields(HttpSession session, UUID claimId) {
    var key = AMENDED_FIELDS_KEY.formatted(claimId.toString());
    session.removeAttribute(key);
  }

  public static void removeAllForClaim(HttpSession session, UUID claimId) {
    removeClaim(session, claimId);
    removeAmendmentForms(session, claimId);
    removeAmendedFields(session, claimId);
  }
}
