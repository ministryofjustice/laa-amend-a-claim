package uk.gov.justice.laa.amend.claim.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.amend.claim.client.ClaimsApiClient;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AmendmentReasonReference;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AmendmentRequestedByReferenceList;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
@Slf4j
public class SystemReferenceService {

    private final ClaimsApiClient claimsApiClient;

    public AmendmentRequestedByReferenceList getAmendmentRequestedByReferenceList() {
        try {
            return claimsApiClient.getAmendmentRequestedByReferenceList().block();
        } catch (Exception e) {
            log.error("Error getting amendment requested by reference list", e);
            throw e;
        }
    }

    public List<AmendmentReasonReference> getAmendmentReasonByProvider(String providerCode) {
        return Optional.ofNullable(getAmendmentRequestedByReferenceList())
            .map(AmendmentRequestedByReferenceList::getRequestedBy)
            .orElse(Collections.emptyList())
            .stream()
            .filter(requestedBy -> requestedBy != null 
                && providerCode != null 
                && providerCode.equalsIgnoreCase(requestedBy.getCode()))
            .findFirst()
            .map(requestedBy -> Optional.ofNullable(requestedBy.getReasons())
                .orElse(Collections.emptyList()))
            .orElse(Collections.emptyList());
    }
}
