package uk.gov.justice.laa.amend.claim.utils;

import jakarta.servlet.http.HttpSession;
import java.util.UUID;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.justice.laa.amend.claim.exceptions.NoClaimInSessionException;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimStatus;

@UtilityClass
public class SessionUtils {

    public static ClaimDetails getClaim(HttpSession session, UUID submissionId, UUID claimId) {
        if (session == null) {
            throw new NoClaimInSessionException(submissionId, claimId, "Session not found");
        }

        var claim = (ClaimDetails) session.getAttribute(claimId.toString());

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

    public static ClaimDetails getValidEscapeCaseClaim(HttpSession session, UUID submissionId, UUID claimId) {
        var claim = getValidClaim(session, submissionId, claimId);

        if (claim.getEscaped() == null || !claim.getEscaped()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Claim is not an escape case");
        }

        return claim;
    }
}
